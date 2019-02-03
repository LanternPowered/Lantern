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
package org.lanternpowered.server.item.predicate;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.data.property.Property;
import org.spongepowered.api.data.property.PropertyMatcher;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

public final class PropertyItemPredicates {

    /**
     * Constructs a {@link ItemPredicate} for the given {@link PropertyMatcher}.
     *
     * @param propertyMatcher The property matcher
     * @return The item filter
     */
    public static ItemPredicate hasMatchingProperty(PropertyMatcher<?> propertyMatcher) {
        checkNotNull(propertyMatcher, "propertyMatcher");
        return new ItemPredicate() {
            @Override
            public boolean test(ItemStack stack) {
                return propertyMatcher.matchesHolder(stack);
            }

            @Override
            public boolean test(ItemStackSnapshot stack) {
                return propertyMatcher.matchesHolder(stack);
            }

            @Override
            public boolean test(ItemType type) {
                return propertyMatcher.matchesHolder(type);
            }
        };
    }

    /**
     * Constructs a {@link ItemPredicate} that matches whether
     * the {@link Property} is present on the {@link ItemStack}.
     *
     * @param property The property
     * @return The item filter
     */
    public static ItemPredicate hasProperty(Property<?> property) {
        checkNotNull(property, "property");
        return new ItemPredicate() {
            @Override
            public boolean test(ItemStack stack) {
                return stack.getProperty(property).isPresent();
            }

            @Override
            public boolean test(ItemStackSnapshot stack) {
                return stack.getProperty(property).isPresent();
            }

            @Override
            public boolean test(ItemType type) {
                return type.getProperty(property).isPresent();
            }
        };
    }

    private PropertyItemPredicates() {
    }
}
