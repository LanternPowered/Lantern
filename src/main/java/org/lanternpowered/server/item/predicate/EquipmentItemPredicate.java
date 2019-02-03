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

import org.spongepowered.api.data.property.Properties;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;

import java.util.function.Predicate;

public interface EquipmentItemPredicate extends ItemPredicate {

    /**
     * Tests whether the provided {@link EquipmentType} is valid.
     *
     * @param equipmentType The equipment type
     * @return Whether the equipment type is valid
     */
    boolean test(EquipmentType equipmentType);

    @Override
    default EquipmentItemPredicate andThen(ItemPredicate itemPredicate) {
        final EquipmentItemPredicate thisPredicate = this;
        return new EquipmentItemPredicate() {
            @Override
            public boolean test(ItemStack stack) {
                return thisPredicate.test(stack) && itemPredicate.test(stack);
            }

            @Override
            public boolean test(ItemType type) {
                return thisPredicate.test(type) && itemPredicate.test(type);
            }

            @Override
            public boolean test(ItemStackSnapshot stack) {
                return thisPredicate.test(stack) && itemPredicate.test(stack);
            }

            @Override
            public boolean test(EquipmentType equipmentType) {
                return thisPredicate.test(equipmentType) && (!(itemPredicate instanceof EquipmentItemPredicate) ||
                        ((EquipmentItemPredicate) itemPredicate).test(equipmentType));
            }
        };
    }

    @Override
    default EquipmentItemPredicate invert() {
        final EquipmentItemPredicate thisPredicate = this;
        return new EquipmentItemPredicate() {
            @Override
            public boolean test(EquipmentType equipmentType) {
                return !thisPredicate.test(equipmentType);
            }

            @Override
            public boolean test(ItemStack stack) {
                return !thisPredicate.test(stack);
            }

            @Override
            public boolean test(ItemType type) {
                return !thisPredicate.test(type);
            }

            @Override
            public boolean test(ItemStackSnapshot stack) {
                return !thisPredicate.test(stack);
            }
        };
    }

    /**
     * Constructs a {@link ItemPredicate} for the provided
     * {@link EquipmentType} predicate.
     *
     * @param predicate The predicate
     * @return The equipment item filter
     */
    static EquipmentItemPredicate of(Predicate<EquipmentType> predicate) {
        checkNotNull(predicate, "predicate");
        return new EquipmentItemPredicate() {
            @Override
            public boolean test(EquipmentType equipmentType) {
                return predicate.test(equipmentType);
            }

            @Override
            public boolean test(ItemStack stack) {
                return stack.getProperty(Properties.EQUIPMENT_TYPE).map(this::test).orElse(false);
            }

            @Override
            public boolean test(ItemStackSnapshot stack) {
                return stack.getProperty(Properties.EQUIPMENT_TYPE).map(this::test).orElse(false);
            }

            @Override
            public boolean test(ItemType type) {
                return type.getProperty(Properties.EQUIPMENT_TYPE).map(this::test).orElse(false);
            }
        };
    }

    /**
     * Constructs a {@link ItemPredicate} for the provided
     * {@link EquipmentType} predicate.
     *
     * @param equipmentType The equipment type
     * @return The equipment item filter
     */
    static EquipmentItemPredicate of(EquipmentType equipmentType) {
        checkNotNull(equipmentType, "equipmentType");
        return of(equipmentType::includes);
    }
}
