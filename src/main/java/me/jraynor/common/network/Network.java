package me.jraynor.common.network;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import me.jraynor.NoMoreWires;
import me.jraynor.api.packet.RemoveNode;
import me.jraynor.old.INode2;
import me.jraynor.common.network.packets.*;
import me.jraynor.old.ClientNode;
import me.jraynor.old.ServerNode;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * This class will handle all of the networking between client and server.
 */
public final class Network {
    private Network() {}

    private static final Logger logger = LogManager.getLogger(NoMoreWires.MOD_ID);
    //    static final Map<Class<? extends IPacket>, List<Pair<BiConsumer<IPacket, Supplier<NetworkEvent.Context>>, Object>>> packetCallbacks = new HashMap<>();
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
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(NoMoreWires.MOD_ID, "nomorewires"),
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
        registerPacket(INode2.class);
        registerPacket(ClientNode.class);
        registerPacket(ServerNode.class);
        registerPacket(AddNode.class);
        registerPacket(AddLink.class);
        registerPacket(RemoveNode.class);
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
     * This will get take all of the methods inside that object that have
     * as the first parameter a child of {@link IPacket} and have
     * {@link NetworkEvent.Context} as the second parameter.
     *
     * @param instance the instance of the object to analyze/subscribe
     */
    public static void subscribe(Object instance) {
        var methods = findSubscribers(instance);
        for(var pair : methods){
            var cls = pair.getFirst();
            var method = pair.getSecond();
            var map = callbacks.computeIfAbsent(cls, aClass -> new HashMap<>());
            var subscribers = map.computeIfAbsent(instance, o -> new ArrayList<>());
            subscribers.add((packet, ctx) -> {
                try {
                    method.invoke(instance, packet, ctx);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    NoMoreWires.logger.error("Failed to invoke " + packet.getClass().getSimpleName() + " callback for " + instance.getClass().getSimpleName() + " instance.");
                    e.printStackTrace();
                }
            });
            NoMoreWires.logger.debug("Successfully subscribed " + cls.getSimpleName() + " callback for " + instance.getClass().getSimpleName() + " instance.");
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
     * This will simply remove any consumers/callbacks that are relatedt to lthe given instance
     *
     * @param instance the instance to check
     */
    public static void unsubscribe(Object instance) {
        callbacks.forEach((cls, cbs) -> {
            cbs.remove(instance);
            NoMoreWires.logger.debug("Successfully unsubscribed " + cls.getSimpleName() + " callback for " + instance.getClass().getSimpleName() + " instance.");
        });
    }

    /**
     * This will use reflection to find all the methods in a class that are packet callbacks
     *
     * @param object the object to scan for callbacks
     */
//    public static void register(Object object) {
//        Method[] methods = object.getClass().getMethods();
//        for (Method method : methods) {
//            if (method.getParameterCount() == 2) {
//                Class<?>[] types = method.getParameterTypes();
//                if (IPacket.class.isAssignableFrom(types[0]) && Supplier.class.isAssignableFrom(types[1])) {
//                    Class<? extends IPacket> packetClass = (Class<? extends IPacket>) types[0];
//                    register(packetClass, object, (packet, contextSupplier) -> {
//                        try {
//                            method.invoke(object, packetClass.cast(packet), contextSupplier);
////                            logger.info("Invoked packet callback method for class " + object.getClass().getSimpleName() + " and method name " + method.getName());
//                        } catch (IllegalAccessException | InvocationTargetException e) {
//                            logger.error("Failed to invoke the callback method for packet " + packetClass.getSimpleName());
//                            e.printStackTrace();
//                        }
//                    });
//                    logger.debug("Successfully registered method " + method.getName() + " for callback to packet " + packetClass.getSimpleName());
//                }
//            }
//        }
//    }

    /**
     * This will remove the current object from the list of registered subscribers
     *
     * @param object the object to unsubscribe
     */
//    public static void unregister(Object object) {
//    }


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


}
