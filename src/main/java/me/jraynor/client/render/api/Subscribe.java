package me.jraynor.client.render.api;

import net.minecraftforge.eventbus.api.Event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Subscribe {
    /**
     * @return the event type
     */
    Class<? extends Event> value();
}
