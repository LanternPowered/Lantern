package org.lanternpowered.server.game;

/**
 * A simple factory interface that is used to create
 * builder instances in the game registry.
 */
@FunctionalInterface
interface BuilderFactory {

    Object create(Class<?> objectType);
}
