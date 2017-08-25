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

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.inventory.LanternItemStackSnapshot;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.function.Predicate;

public interface ItemFilter {

    /**
     * Tests whether the provided {@link ItemStack} is valid.
     *
     * @param stack The item stack
     * @return Whether the stack is valid
     */
    boolean isValid(ItemStack stack);

    /**
     * Tests whether the provided {@link ItemType} is valid.
     *
     * @param type The item type
     * @return Whether the type is valid
     */
    boolean isValid(ItemType type);

    /**
     * Tests whether the provided {@link ItemStackSnapshot} is valid.
     *
     * @param stack The item stack snapshot
     * @return Whether the stack is valid
     */
    boolean isValid(ItemStackSnapshot stack);

    /**
     * Combines this {@link ItemFilter} with the other one. Both
     * {@link ItemFilter}s must succeed in order to get {@code true}
     * as a result.
     *
     * @param itemFilter The item filter
     * @return The combined item filter
     */
    default ItemFilter andThen(ItemFilter itemFilter) {
        final ItemFilter thisFilter = this;
        return new ItemFilter() {
            @Override
            public boolean isValid(ItemStack stack) {
                return thisFilter.isValid(stack) && itemFilter.isValid(stack);
            }

            @Override
            public boolean isValid(ItemType type) {
                return thisFilter.isValid(type) && itemFilter.isValid(type);
            }

            @Override
            public boolean isValid(ItemStackSnapshot stack) {
                return thisFilter.isValid(stack) && itemFilter.isValid(stack);
            }
        };
    }

    default ItemFilter invert() {
        final ItemFilter thisFilter = this;
        return new ItemFilter() {
            @Override
            public boolean isValid(ItemStack stack) {
                return thisFilter.isValid(stack);
            }

            @Override
            public boolean isValid(ItemType type) {
                return thisFilter.isValid(type);
            }

            @Override
            public boolean isValid(ItemStackSnapshot stack) {
                return thisFilter.isValid(stack);
            }
        };
    }

    /**
     * Constructs a {@link ItemFilter} for the provided
     * {@link ItemStack} predicate.
     *
     * @param predicate The predicate
     * @return The item filter
     */
    static ItemFilter ofStackPredicate(Predicate<ItemStack> predicate) {
        checkNotNull(predicate, "predicate");
        return new ItemFilter() {
            @Override
            public boolean isValid(ItemStack stack) {
                return predicate.test(stack);
            }

            @Override
            public boolean isValid(ItemType type) {
                return predicate.test(ItemStack.of(type, 1));
            }

            @Override
            public boolean isValid(ItemStackSnapshot stack) {
                return predicate.test(stack.createStack());
            }
        };
    }

    /**
     * Constructs a {@link ItemFilter} for the provided
     * {@link ItemStackSnapshot} predicate.
     *
     * @param predicate The predicate
     * @return The item filter
     */
    static ItemFilter ofSnapshotPredicate(Predicate<ItemStackSnapshot> predicate) {
        checkNotNull(predicate, "predicate");
        return new ItemFilter() {
            @Override
            public boolean isValid(ItemStack stack) {
                return predicate.test(LanternItemStackSnapshot.wrap(stack));
            }

            @Override
            public boolean isValid(ItemType type) {
                return predicate.test(LanternItemStackSnapshot.wrap(ItemStack.of(type, 1)));
            }

            @Override
            public boolean isValid(ItemStackSnapshot stack) {
                return predicate.test(stack);
            }
        };
    }

    /**
     * Constructs a {@link ItemFilter} for the provided
     * {@link ItemType} predicate.
     *
     * @param predicate The predicate
     * @return The item filter
     */
    static ItemFilter ofTypePredicate(Predicate<ItemType> predicate) {
        checkNotNull(predicate, "predicate");
        return new ItemFilter() {
            @Override
            public boolean isValid(ItemStack stack) {
                return predicate.test(stack.getType());
            }

            @Override
            public boolean isValid(ItemType type) {
                return predicate.test(type);
            }

            @Override
            public boolean isValid(ItemStackSnapshot stack) {
                return predicate.test(stack.getType());
            }
        };
    }
}
