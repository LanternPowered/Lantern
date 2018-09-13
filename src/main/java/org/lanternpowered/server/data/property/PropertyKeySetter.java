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
package org.lanternpowered.server.data.property;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.inventory.property.AbstractInventoryProperty;
import org.lanternpowered.server.util.UncheckedThrowables;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.property.AbstractProperty;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class PropertyKeySetter {

    private static final Field ABSTRACT_PROPERTY_KEY;
    private static final Field ABSTRACT_INVENTORY_PROPERTY_KEY;

    static {
        try {
            ABSTRACT_PROPERTY_KEY = AbstractProperty.class.getDeclaredField("key");
            ABSTRACT_PROPERTY_KEY.setAccessible(true);

            ABSTRACT_INVENTORY_PROPERTY_KEY = AbstractInventoryProperty.class.getDeclaredField("key");
            ABSTRACT_INVENTORY_PROPERTY_KEY.setAccessible(true);

            final Field mField = Field.class.getDeclaredField("modifiers");
            mField.setAccessible(true);

            mField.set(ABSTRACT_PROPERTY_KEY, ABSTRACT_PROPERTY_KEY.getModifiers() & ~Modifier.FINAL);
            mField.set(ABSTRACT_INVENTORY_PROPERTY_KEY, ABSTRACT_INVENTORY_PROPERTY_KEY.getModifiers() & ~Modifier.FINAL);
        } catch (Exception e) {
            throw UncheckedThrowables.throwUnchecked(e);
        }
    }

    public static <K> void setKey(Property<K, ?> property, K key) {
        checkNotNull(property, "property");
        checkNotNull(key, "key");
        try {
            if (property instanceof AbstractProperty) {
                ABSTRACT_PROPERTY_KEY.set(property, key);
                return;
            } else if (property instanceof AbstractInventoryProperty) {
                ABSTRACT_INVENTORY_PROPERTY_KEY.set(property, key);
                return;
            }
        } catch (Exception e) {
            throw UncheckedThrowables.throwUnchecked(e);
        }
        throw new IllegalArgumentException("Unsupported property type: " + property.getClass().getName());
    }
}
