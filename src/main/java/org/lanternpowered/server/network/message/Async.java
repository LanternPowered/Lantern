package org.lanternpowered.server.network.message;

import org.lanternpowered.server.network.message.handler.Handler;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Can be used to define whether a specific {@link Message} or {@link Handler}
 * type should be handled asynchronous.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Async {

}
