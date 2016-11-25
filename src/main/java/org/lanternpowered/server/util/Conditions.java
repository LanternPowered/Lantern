/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.Optional;

import javax.annotation.Nullable;

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
