/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
