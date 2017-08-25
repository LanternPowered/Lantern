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
package org.lanternpowered.server.inventory.vanilla;

import org.lanternpowered.server.inventory.AbstractInventoryRow;
import org.lanternpowered.server.inventory.AbstractSlot;
import org.lanternpowered.server.inventory.ISlot;
import org.lanternpowered.server.inventory.behavior.HotbarBehavior;
import org.lanternpowered.server.inventory.behavior.VanillaHotbarBehavior;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.item.inventory.property.EquipmentSlotType;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public class LanternHotbarInventory extends AbstractInventoryRow implements Hotbar {

    private final HotbarBehavior hotbarBehavior = new VanillaHotbarBehavior();

    /**
     * Gets the {@link ISlot} that is currently selected.
     *
     * @return The selected slot
     */
    public AbstractSlot getSelectedSlot() {
        final int slotIndex = this.hotbarBehavior.getSelectedSlotIndex();
        return (AbstractSlot) getSlot(slotIndex).orElseThrow(() -> new IllegalStateException("No slot at index: " + slotIndex));
    }

    /**
     * Gets the {@link HotbarBehavior}.
     *
     * @return The hotbar behavior
     */
    public HotbarBehavior getHotbarBehavior() {
        return this.hotbarBehavior;
    }

    @Override
    public int getSelectedSlotIndex() {
        return this.hotbarBehavior.getSelectedSlotIndex();
    }

    @Override
    public void setSelectedSlotIndex(int index) {
        this.hotbarBehavior.setSelectedSlotIndex(index);
    }

    // The EquipmentSlotType off the selected hotbar slot is dynamic,
    // so you can't assign it directly to a slot instance

    @Override
    protected <T extends InventoryProperty<?, ?>> Optional<T> tryGetProperty(Inventory child, Class<T> property, @Nullable Object key) {
        if (EquipmentSlotType.class.isAssignableFrom(property) && child == getSelectedSlot()) {
            return Optional.of((T) new EquipmentSlotType(EquipmentTypes.MAIN_HAND));
        }
        return Optional.empty();
    }

    @Override
    protected <T extends InventoryProperty<?, ?>> List<T> tryGetProperties(Inventory child, Class<T> property) {
        final List<T> properties = super.tryGetProperties(child, property);
        if (EquipmentSlotType.class.isAssignableFrom(property) && child == getSelectedSlot()) {
            properties.add((T) new EquipmentSlotType(EquipmentTypes.MAIN_HAND));
        }
        return properties;
    }
}
