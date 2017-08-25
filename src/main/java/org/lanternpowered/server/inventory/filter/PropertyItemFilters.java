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
package org.lanternpowered.server.inventory.filter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.inventory.filter.ItemFilter.ofTypePredicate;

import org.spongepowered.api.data.Property;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.property.AcceptsItems;

import java.util.Collection;

public final class PropertyItemFilters {

    /**
     * Constructs a {@link ItemFilter} that matches whether
     * the {@link Property} is present and matches on the
     * {@link ItemStack}.
     *
     * @param property The property
     * @return The item filter
     */
    public static ItemFilter hasMatchingProperty(Property<?,?> property) {
        checkNotNull(property, "property");
        return new ItemFilter() {
            @Override
            public boolean isValid(ItemStack stack) {
                return stack.getProperty(property.getClass()).map(property::matches).orElse(false);
            }

            @Override
            public boolean isValid(ItemStackSnapshot stack) {
                return stack.getProperty(property.getClass()).map(property::matches).orElse(false);
            }

            @Override
            public boolean isValid(ItemType type) {
                return type.getDefaultProperty(property.getClass()).map(property::matches).orElse(false);
            }
        };
    }

    /**
     * Constructs a {@link ItemFilter} that matches whether
     * the {@link Property} type is present on the {@link ItemStack}.
     *
     * @param propertyType The property type
     * @return The item filter
     */
    public static ItemFilter hasProperty(Class<? extends Property<?,?>> propertyType) {
        checkNotNull(propertyType, "propertyType");
        return new ItemFilter() {
            @Override
            public boolean isValid(ItemStack stack) {
                return stack.getProperty(propertyType).isPresent();
            }

            @Override
            public boolean isValid(ItemStackSnapshot stack) {
                return stack.getProperty(propertyType).isPresent();
            }

            @Override
            public boolean isValid(ItemType type) {
                return type.getDefaultProperty(propertyType).isPresent();
            }
        };
    }

    /**
     * Constructs a {@link ItemFilter} for the
     * {@link AcceptsItems} property.
     *
     * @param acceptsItems The accepts items property
     * @return The item filter
     */
    public static ItemFilter of(AcceptsItems acceptsItems) {
        checkNotNull(acceptsItems, "acceptsItems");
        final Collection<ItemType> itemTypes = acceptsItems.getValue();
        checkNotNull(itemTypes, "value");
        final Property.Operator operator = acceptsItems.getOperator();
        checkArgument(operator == Property.Operator.EQUAL || operator == Property.Operator.NOTEQUAL,
                "Only the operators EQUAL and NOTEQUAL are supported, %s is not.", operator);
        if (operator == Property.Operator.EQUAL) {
            return ofTypePredicate(itemTypes::contains);
        } else {
            return ofTypePredicate(type -> !itemTypes.contains(type));
        }
    }

    private PropertyItemFilters() {
    }
}
