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
package org.lanternpowered.server.inventory.slot;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.data.property.PropertyKeySetter;
import org.lanternpowered.server.inventory.equipment.LanternEquipmentType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypeWorn;
import org.spongepowered.api.item.inventory.property.ArmorSlotType;
import org.spongepowered.api.item.inventory.property.EquipmentSlotType;
import org.spongepowered.api.item.inventory.slot.EquipmentSlot;
import org.spongepowered.api.text.translation.Translation;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

public class LanternEquipmentSlot extends LanternFilteringSlot implements EquipmentSlot {

    private static class SimpleEquipmentItemFilter implements EquipmentItemFilter {

        private final Set<EquipmentType> equipmentTypes;

        private SimpleEquipmentItemFilter(Set<EquipmentType> equipmentTypes) {
            this.equipmentTypes = equipmentTypes;
        }

        @Override
        public boolean isValidItem(EquipmentType type) {
            for (EquipmentType equipmentType : this.equipmentTypes) {
                if (((LanternEquipmentType) type).isChild(equipmentType)) {
                    return true;
                }
            }
            return false;
        }
    }

    public LanternEquipmentSlot(@Nullable Inventory parent, EquipmentType... equipmentTypes) {
        super(parent, null, new SimpleEquipmentItemFilter(ImmutableSet.copyOf(Arrays.asList(equipmentTypes))));
        registerContent();
    }

    public LanternEquipmentSlot(@Nullable Inventory parent, Translation name, EquipmentType... equipmentTypes) {
        super(parent, name, new SimpleEquipmentItemFilter(ImmutableSet.copyOf(Arrays.asList(equipmentTypes))));
        registerContent();
    }

    public LanternEquipmentSlot(@Nullable Inventory parent, @Nullable Translation name, @Nullable EquipmentItemFilter itemFilter) {
        super(parent, name, itemFilter);
        registerContent();
    }

    private void registerContent() {
        if (this.itemFilter instanceof SimpleEquipmentItemFilter) {
            for (EquipmentType equipmentType : ((SimpleEquipmentItemFilter) this.itemFilter).equipmentTypes) {
                if (equipmentType instanceof EquipmentTypeWorn) {
                    final ArmorSlotType slotType = new ArmorSlotType((EquipmentTypeWorn) equipmentType);
                    PropertyKeySetter.setKey(slotType, equipmentType.getId());
                    registerProperty(slotType);
                }
                final EquipmentSlotType slotType = new EquipmentSlotType(equipmentType);
                PropertyKeySetter.setKey(slotType, equipmentType.getId());
                registerProperty(slotType);
            }
        }
    }

    @Override
    public boolean isValidItem(EquipmentType type) {
        checkNotNull(type, "type");
        if (this.itemFilter != null && !((EquipmentItemFilter) this.itemFilter).isValidItem(type)) {
            return false;
        }
        return this.doesAllowEquipmentType(() -> Optional.of(type));
    }

    @Override
    public boolean isReverseShiftClickOfferOrder() {
        return false;
    }
}
