/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.Sponge;
import org.spongepowered.plugin.PluginContainer;

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class Conditions {

    private static final String NOT_AVAILABLE = "This function is not available yet.";

    /**
     * Ensures that an object reference passed as a parameter to the calling
     * method is a valid plugin container or plugin reference.
     * 
     * @param object An object reference
     * @param message The exception message to use if the check fails; will be
     *        converted to a string using {@link String#valueOf(Object)}.
     * @return The resulted plugin container
     * @throws NullPointerException - If reference is null
     * @throws IllegalArgumentException - If reference is invalid
     */
    public static PluginContainer checkPlugin(Object object, @Nullable Object message) {
        //noinspection ConstantConditions
        checkState(Lantern.getGame() != null, NOT_AVAILABLE);
        checkNotNull(object, message);

        if (object instanceof PluginContainer) {
            return (PluginContainer) object;
        }

        final Optional<PluginContainer> container = Sponge.getPluginManager().fromInstance(object);
        checkArgument(container.isPresent(), (message != null ? message + ": " : "") + "invalid plugin (%s)", object);
        return container.get();
    }

    /**
     * Ensures that an string reference passed as a parameter to the calling
     * method is not null or empty.
     * 
     * @param object An object reference
     * @param message The exception message to use if the check fails; will be
     *        converted to a string using {@link String#valueOf(Object)}.
     * @return the reference that was validated
     * @throws NullPointerException - If reference is null
     * @throws IllegalArgumentException - If reference is empty
     */
    public static String checkNotNullOrEmpty(String object, @Nullable Object message) {
        checkNotNull(object, message);
        checkArgument(!object.isEmpty(), (message != null ? message + ": " : "") + "empty object");
        return object;
    }

    /**
     * Ensures that an string reference passed as a parameter to the calling
     * method is not null or empty.
     * 
     * @param object An object reference
     * @return The reference that was validated
     * @throws NullPointerException - If reference is null
     * @throws IllegalArgumentException - If reference is empty
     */
    public static String checkNotNullOrEmpty(String object) {
        checkNotNull(object);
        checkArgument(!object.isEmpty());
        return object;
    }

    /**
     * Checks whether the specified index suitable is to be used for
     * a array with the specified length, this will throw a {@link 
     * ArrayIndexOutOfBoundsException} if false.
     * 
     * @param index The index to check
     * @param length The length
     */
    public static void checkArrayRange(int index, int length) {
        if (index < 0 || index >= length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    private Conditions() {
    }

}
