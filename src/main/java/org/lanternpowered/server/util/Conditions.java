/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) Contributors
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

import java.util.Optional;

import javax.annotation.Nullable;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@NonnullByDefault
public final class Conditions {

    private static final String NOT_AVAILABLE = "This function is not available yet.";

    /**
     * Ensures that an object reference passed as a parameter to the calling
     * method is a valid plugin container or plugin reference.
     * 
     * @param object an object reference
     * @param message the exception message to use if the check fails; will be
     *        converted to a string using {@link String#valueOf(Object)}.
     * @return the resulted plugin container
     * @throws NullPointerException - if reference is null
     * @throws IllegalArgumentException - if reference is invalid
     */
    public static PluginContainer checkPlugin(Object object, @Nullable Object message) {
        // Make sure that the game and plugin manager is already loaded
        LanternGame game = LanternGame.get();

        checkState(game != null && game.getPluginManager() != null, NOT_AVAILABLE);
        checkNotNull(object, message);

        if (object instanceof PluginContainer) {
            return (PluginContainer) object;
        }

        Optional<PluginContainer> container = LanternGame.get().getPluginManager().fromInstance(object);
        checkArgument(container.isPresent(), (message != null ? message + ": " : "") + "invalid plugin (%s)", object);
        return container.get();
    }

    /**
     * Ensures that an string reference passed as a parameter to the calling
     * method is not null or empty.
     * 
     * @param object an object reference
     * @param message the exception message to use if the check fails; will be
     *        converted to a string using {@link String#valueOf(Object)}.
     * @return the reference that was validated
     * @throws NullPointerException - if reference is null
     * @throws IllegalArgumentException - if reference is empty
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
     * @param object an object reference
     * @return the reference that was validated
     * @throws NullPointerException - if reference is null
     * @throws IllegalArgumentException - if reference is empty
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
     * @param index the index to check
     * @param length the length
     */
    public static void checkArrayRange(int index, int length) {
        if (index < 0 || index >= length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    private Conditions() {
    }

}
