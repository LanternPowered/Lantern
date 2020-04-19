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
package org.lanternpowered.api.script;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;

public final class Import {

    /**
     * Constructs a {@link Import} based on a method in the target class.
     *
     * @param target The target class
     * @param methodName The method name
     * @return The import
     */
    public static Import ofMethod(Class<?> target, String methodName) {
        return new Import(target.getName() + '.' + methodName, true);
    }

    /**
     * Constructs a {@link Import} based on a static field in the target class.
     *
     * @param target The target class
     * @param fieldName The field name
     * @return The import
     */
    public static Import ofField(Class<?> target, String fieldName) {
        return new Import(target.getName() + '.' + fieldName, true);
    }

    /**
     * Constructs a {@link Import} for the target class.
     *
     * @param target The target class
     * @return The import
     */
    public static Import ofClass(Class<?> target) {
        return new Import(target.getName(), false);
    }

    /**
     * Constructs a {@link Import} for the import value.
     *
     * @param value The import value
     * @param isStatic Whether the import is static
     * @return The import
     */
    public static Import of(String value, boolean isStatic) {
        return new Import(value, isStatic);
    }

    private final String value;
    private final boolean isStatic;

    private Import(String value, boolean isStatic) {
        this.value = checkNotNull(value, "value");
        this.isStatic = isStatic;
    }

    /**
     * Gets the value of the {@link Import}.
     *
     * @return The value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Whether the import is static.
     *
     * @return Is static
     */
    public boolean isStatic() {
        return this.isStatic;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("value", this.value)
                .add("static", this.isStatic)
                .toString();
    }
}
