/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
package org.lanternpowered.server.world.rules;

import java.util.Optional;

import org.spongepowered.api.event.cause.Cause;

public interface GameRule {

    /**
     * Gets the name of the game rule.
     * 
     * @return the name
     */
    String getName();

    /**
     * Sets the value of the game rule.
     * 
     * @param object the object
     */
    <T> void set(T object);

    /**
     * Sets the value of the game rule with a specific cause.
     * 
     * @param object the object
     * @param cause the cause
     */
    <T> void set(T object, Cause cause);

    /**
     * Gets the value of the game rule as a string.
     * 
     * @return the value
     */
    Optional<String> asString();

    /**
     * Gets the value of the game rule as a boolean.
     * 
     * @return the value
     */
    Optional<Boolean> asBoolean();

    /**
     * Gets the value of the game rule as a double.
     * 
     * @return the value
     */
    Optional<Double> asDouble();

    /**
     * Gets the value of the game rule as a float.
     * 
     * @return the value
     */
    Optional<Float> asFloat();

    /**
     * Gets the value of the game rule as a integer.
     * 
     * @return the value
     */
    Optional<Integer> asInt();
}
