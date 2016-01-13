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
package org.lanternpowered.server.world.rules;

import org.spongepowered.api.util.Coerce;

import java.util.function.Function;

public final class RuleDataTypes {

    public static final RuleDataType<Integer> INTEGER = new RuleDataType<>(Integer.class,
            s -> Coerce.asInteger(s).orElseGet(() -> { throw new IllegalArgumentException("Expected a integer, but input '" + s + "' was not"); }));

    public static final RuleDataType<Boolean> BOOLEAN = new RuleDataType<>(Boolean.class,
            s -> {
                if ("true".equalsIgnoreCase(s)) {
                    return true;
                } else if ("false".equalsIgnoreCase(s)) {
                    return false;
                } else {
                    throw new IllegalArgumentException("Expected a boolean, but input '" + s + "' was not");
                }
            });

    public static final RuleDataType<String> STRING = new RuleDataType<>(String.class, Function.identity());

}
