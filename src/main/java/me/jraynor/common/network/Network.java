package me.jraynor.common.network;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import lombok.extern.log4j.Log4j2;
import me.jraynor.Nmw;
import me.jraynor.api.packet.*;
import me.jraynor.common.network.packets.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * This class will handle all of the networking between client and server.
 */
@Log4j2
public final class Network {
    private Network() {}

    static final Map<Class<? extends IPacket>, Map<Object, List<BiConsumer<IPacket, NetworkEvent.Context>>>> callbacks = Maps.newConcurrentMap();


    private static SimpleChannel INSTANCE;
    private static int ID = 0;

    /**
     * @return the next id for synchronized networking packet's
     */
    private static int nextID() {
        return ID++;
    }

    /**
     * This will register the messages on both the client and server.
     */
    public static void initializeNetwork() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Nmw.MOD_ID, "nomorewires"),
                () -> "1.0",
                s -> true,
                s -> true);
        registerPacket(TransferData.class);
        registerPacket(TransferUpdate.class);
        registerPacket(LeftClickAir.class);
        registerPacket(LeftClickBlock.class);
        registerPacket(LinkStart.class);
        registerPacket(LinkReset.class);
        registerPacket(LinkComplete.class);
        registerPacket(OpenScreen.class);
        registerPacket(AddNode.class);
        registerPacket(AddLink.class);
        registerPacket(RemoveNode.class);
        registerPacket(RequestSync.class);
        registerPacket(ResponseSync.class);
        registerPacket(SyncRead.class);
    }

    /**
     * This will perfectly dynamically register a packet.
     *
     * @param cls the packet class to register
     * @param <T> the generic type of class
     */
    private static <T extends IPacket> void registerPacket(Class<T> cls) {
        INSTANCE.messageBuilder(cls, nextID())
                .encoder(IPacket::writeBuffer)
                .decoder(buffer -> createPacket(cls, buffer))
                .consumer(IPacket::handle)
                .add();
    }

    /**
     * This will generate a packet based upon the class type
     *
     * @param cls    the packet class
     * @param buffer the buffer to help generate the class with
     * @param <T>    the generic packet type
     * @return returns a packet of the given type
     */
    private static <T extends IPacket> T createPacket(Class<T> cls, PacketBuffer buffer) {
        try {
            try {
                Constructor<T> noArgs = cls.getConstructor();
                T packet = noArgs.newInstance();
                packet.readBuffer(buffer);
                return packet;
            } catch (NoSuchMethodException e) {
                Constructor<?>[] constructors = cls.getConstructors();
                for (Constructor<?> constructor : constructors) {
                    int count = constructor.getParameterCount();
                    Object[] objects = new Object[count];
                    for (int i = 0; i < count; i++)
                        objects[i] = null;
                    T packet = (T) constructor.newInstance(objects);
                    packet.readBuffer(buffer);
                    return packet;
                }
            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * This needs to be synchronized because the callbacks may be in use else where
     * This will get take all of the methods inside that object that have
     * as the first parameter a child of {@link IPacket} and have
     * {@link NetworkEvent.Context} as the second parameter.
     *
     * @param instance the instance of the object to analyze/subscribe
     */
    public static void subscribe(Object instance) {
        var methods = findSubscribers(instance);
        for (var pair : methods) {
            var cls = pair.getFirst();
            var method = pair.getSecond();
            if (!callbacks.containsKey(cls))
                callbacks.put(cls, new HashMap<>());
            var map = callbacks.get(cls);
            if (!map.containsKey(instance))
                map.put(instance, new ArrayList<>());
            if (map.get(instance).add((packet, ctx) -> {
                        try {
                            method.setAccessible(true);
                            method.invoke(instance, packet, ctx);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            log.error("Failed to invoke " + packet.getClass().getSimpleName() + " callback for " + instance.getClass().getSimpleName() + " instance.");
                            e.printStackTrace();
                        }
                    }
            ))
                log.debug("Successfully subscribed " + cls.getSimpleName() + " callback for " + instance.getClass().getSimpleName() + " instance.");
        }
    }


    /**
     * This will find all of the methods within an instance that can potentially be subscribers.
     *
     * @param instance the instance to check
     * @return a list of potential subscribers
     */
    private static List<Pair<Class<? extends IPacket>, Method>> findSubscribers(Object instance) {
        return Arrays.stream(instance.getClass()
                .getDeclaredMethods())
                .filter(method -> {
                    var params = method.getParameterTypes();
                    if (params.length != 2) return false;
                    return (IPacket.class.isAssignableFrom(params[0]) && NetworkEvent.Context.class.isAssignableFrom(params[1]));
                })
                .map(Network::mapMethodToPacketClass)
                .collect(Collectors.toList());
    }

    /**
     * This method will map the given method, which at this point we know is a subscriber,
     * to a pair that contains the IPacket class type, and the given method.
     *
     * @param method the method given
     * @return a pair with the packet class and method
     */
    private static Pair<Class<? extends IPacket>, Method> mapMethodToPacketClass(Method method) {
        var params = method.getParameterTypes();
        if (IPacket.class.isAssignableFrom(params[0]))
            return new Pair<>((Class<? extends IPacket>) params[0], method);
        return null;
    }

    /**
     * This needs to be synchronized because the callbacks
     * This will simply remove any consumers/callbacks that are relatedt to lthe given instance
     *
     * @param instance the instance to check
     */
    synchronized public static void unsubscribe(Object instance) {
        synchronized (callbacks) {
            for (var entry : callbacks.entrySet()) {
                if (entry.getValue().remove(instance) != null)
                    log.debug("Successfully unsubscribed " + entry.getKey().getSimpleName() + " callback for " + instance.getClass().getSimpleName() + " instance.");
            }
        }

    }

    /**
     * This will send the packet directly to the given player
     *
     * @param packet the packet to send to the client
     * @param player the player to recieve the packet
     */
    public static void sendToClient(IPacket packet, ServerPlayerEntity player) {
        INSTANCE.sendTo(packet, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }

    /**
     * This will broadcast to all clients.
     *
     * @param packet the packet to broadcast
     */
    public static void sendToAllClients(IPacket packet) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
    }

    /**
     * This will broadcast to all the clients with the specified chunk.
     *
     * @param packet the packet to send
     * @param chunk  the chunk to use
     */
    public static void sendToClientsWithChunk(IPacket packet, Chunk chunk) {
        INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), packet);
    }

    /**
     * This will broadcast to all the clients with the specified chunk.
     *
     * @param packet the packet to send
     * @param near   The target point to use as reference for what is near
     */
    public static void sendToClientsNear(IPacket packet, PacketDistributor.TargetPoint near) {
        INSTANCE.send(PacketDistributor.NEAR.with(() -> near), packet);
    }


    /**
     * This will send the packet directly to the server
     *
     * @param packet the packet to be sent
     */
    public static void sendToServer(IPacket packet) {
        INSTANCE.sendToServer(packet);
    }

    /**
     * This will delete everything.
     */
    public static void delete() {
        callbacks.clear();
    }
}
