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
package org.lanternpowered.server.text.selector;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.spongepowered.api.text.selector.Argument;
import org.spongepowered.api.text.selector.ArgumentType;
import org.spongepowered.api.text.selector.Selector;
import org.spongepowered.api.text.selector.SelectorType;

import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

public class LanternSelectorBuilder implements Selector.Builder {

    @Nullable private SelectorType type;
    private Map<ArgumentType<?>, Argument<?>> arguments;

    public LanternSelectorBuilder() {
        this.reset();
    }

    public LanternSelectorBuilder(Selector selector) {
        this.from(selector);
    }

    @Override
    public Selector.Builder type(SelectorType type) {
        this.type = checkNotNull(type, "type");
        return this;
    }

    @Override
    public Selector.Builder add(Argument<?>... arguments) {
        for (Argument<?> argument : checkNotNull(arguments, "arguments")) {
            checkNotNull(argument, "argument");
            this.arguments.put(argument.getType(), argument);
        }
        return this;
    }

    @Override
    public Selector.Builder add(Iterable<Argument<?>> arguments) {
        for (Argument<?> argument : checkNotNull(arguments, "arguments")) {
            checkNotNull(argument, "argument");
            this.arguments.put(argument.getType(), argument);
        }
        return this;
    }

    @Override
    public <T> Selector.Builder add(ArgumentType<T> type, T value) {
        this.arguments.put(type, new LanternArgument<>(type, value));
        return this;
    }

    @Override
    public Selector.Builder remove(Argument<?>... arguments) {
        for (Argument<?> argument : checkNotNull(arguments, "arguments")) {
            checkNotNull(argument, "argument");
            this.arguments.remove(argument.getType());
        }
        return this;
    }

    @Override
    public Selector.Builder remove(Iterable<Argument<?>> arguments) {
        for (Argument<?> argument : checkNotNull(arguments, "arguments")) {
            checkNotNull(argument, "argument");
            this.arguments.remove(argument.getType());
        }
        return this;
    }

    @Override
    public Selector.Builder remove(ArgumentType<?>... types) {
        for (ArgumentType<?> type : checkNotNull(types, "types")) {
            checkNotNull(type, "type");
            this.arguments.remove(type);
        }
        return this;
    }

    @Override
    public Selector build() {
        checkState(this.type != null, "type is not set");
        return new LanternSelector(this.type, ImmutableMap.copyOf(this.arguments));
    }

    @Override
    public Selector.Builder from(Selector selector) {
        this.type = selector.getType();
        this.arguments = Maps.newLinkedHashMap(((LanternSelector) selector).arguments);
        return this;
    }

    @Override
    public Selector.Builder reset() {
        this.type = null;
        this.arguments = Maps.newLinkedHashMap();
        return this;
    }

}
