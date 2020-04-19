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

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.text.selector.Argument;
import org.spongepowered.api.text.selector.ArgumentType;

public class LanternArgument<T> implements Argument<T> {

    public static class Invertible<T> extends LanternArgument<T> implements Argument.Invertible<T> {

        private final boolean inverted;

        public Invertible(ArgumentType.Invertible<T> type, T value, boolean inverted) {
            super(type, value);
            this.inverted = inverted;
        }

        @Override
        String getEqualitySymbols() {
            return isInverted() ? "!=" : "=";
        }

        @Override
        public boolean isInverted() {
            return this.inverted;
        }

        @Override
        public Argument.Invertible<T> invert() {
            return new LanternArgument.Invertible<>((ArgumentType.Invertible<T>) this.getType(), this.getValue(), !this.isInverted());
        }

    }

    private static String toSelectorArgument(Object val) {
        if (val instanceof CatalogType) {
            return ((CatalogType) val).getName();
        }
        return String.valueOf(val);
    }

    private final ArgumentType<T> type;
    private final T value;

    public LanternArgument(ArgumentType<T> type, T value) {
        this.type = type;
        this.value = value;
    }

    String getEqualitySymbols() {
        return "=";
    }

    @Override
    public ArgumentType<T> getType() {
        return this.type;
    }

    @Override
    public T getValue() {
        return this.value;
    }

    @Override
    public String toPlain() {
        return this.type.getKey() + getEqualitySymbols() + toSelectorArgument(getValue());
    }

}
