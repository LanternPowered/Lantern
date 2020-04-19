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
