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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Function;

public final class RuleDataType<T>  {

    private final Class<T> type;
    private final Function<String, T> parser;
    private final Function<T, String> serializer;

    public RuleDataType(Class<T> type, Function<String, T> parser) {
        this(type, parser, Object::toString);
    }

    public RuleDataType(Class<T> type, Function<String, T> parser, Function<T, String> serializer) {
        this.serializer = serializer;
        this.parser = parser;
        this.type = type;
    }

    public Class<T> getType() {
        return this.type;
    }

    /**
     * Parses the string value for the type.
     *
     * @param value the string value
     * @return the parsed value
     * @throws IllegalArgumentException if the string value couldn't be parsed
     */
    public T parse(String value) throws IllegalArgumentException {
        return this.parser.apply(checkNotNull(value, "value"));
    }

    public String serialize(T value) {
        return this.serializer.apply(checkNotNull(value, "value"));
    }

}
