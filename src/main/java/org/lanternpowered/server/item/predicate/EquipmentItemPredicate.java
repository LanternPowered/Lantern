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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.inventory.equipment.LanternEquipmentType;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.property.item.EquipmentProperty;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.property.EquipmentSlotType;

import java.util.Optional;
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

            private boolean test(Optional<EquipmentProperty> optEquipmentProperty) {
                return optEquipmentProperty
                        .map(property -> {
                            final EquipmentType equipmentType = property.getValue();
                            if (equipmentType == null) {
                                // Equipment type is missing, fail
                                return false;
                            }
                            final Property.Operator operator = property.getOperator();
                            if (operator == Property.Operator.EQUAL) {
                                return predicate.test(equipmentType);
                            } else if (operator == Property.Operator.NOTEQUAL) {
                                return !predicate.test(equipmentType); // TODO: Is this right?
                            }
                            return false; // All the other operations aren't supported
                        })
                        .orElse(false);
            }

            @Override
            public boolean test(ItemStack stack) {
                return test(stack.getProperty(EquipmentProperty.class));
            }

            @Override
            public boolean test(ItemStackSnapshot stack) {
                return test(stack.getProperty(EquipmentProperty.class));
            }

            @Override
            public boolean test(ItemType type) {
                return test(type.getDefaultProperty(EquipmentProperty.class));
            }
        };
    }


    /**
     * Constructs a {@link ItemPredicate} for the provided
     * {@link EquipmentSlotType} property.
     *
     * @param equipmentSlotType The equipment slot type property
     * @return The equipment item filter
     */
    static EquipmentItemPredicate of(EquipmentSlotType equipmentSlotType) {
        checkNotNull(equipmentSlotType, "equipmentSlotType");
        final EquipmentType slotEquipmentType = equipmentSlotType.getValue();
        checkNotNull(slotEquipmentType, "value");
        final Property.Operator operator = equipmentSlotType.getOperator();
        checkArgument(operator == Property.Operator.EQUAL || operator == Property.Operator.NOTEQUAL,
                "Only the operators EQUAL and NOTEQUAL are supported, %s is not.", operator);
        return new EquipmentItemPredicate() {
            @Override
            public boolean test(EquipmentType equipmentType) {
                final boolean result = ((LanternEquipmentType) slotEquipmentType).isChild(equipmentType);
                return (operator == Property.Operator.EQUAL) == result;
            }

            private boolean test(Optional<EquipmentProperty> optEquipmentProperty) {
                return optEquipmentProperty
                        .map(property -> {
                            final EquipmentType equipmentType = property.getValue();
                            final boolean result = ((LanternEquipmentType) slotEquipmentType).isChild(equipmentType);
                            return (operator == Property.Operator.EQUAL) == result;
                        })
                        .orElse(false);
            }

            @Override
            public boolean test(ItemStack stack) {
                return test(stack.getProperty(EquipmentProperty.class));
            }

            @Override
            public boolean test(ItemStackSnapshot stack) {
                return test(stack.getProperty(EquipmentProperty.class));
            }

            @Override
            public boolean test(ItemType type) {
                return test(type.getDefaultProperty(EquipmentProperty.class));
            }
        };
    }
}
