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
package org.lanternpowered.server.inventory.vanilla;

import org.lanternpowered.server.inventory.AbstractChildrenInventory;
import org.lanternpowered.server.inventory.AbstractInventoryRow;
import org.lanternpowered.server.inventory.AbstractSlot;
import org.lanternpowered.server.inventory.IInventory;
import org.lanternpowered.server.inventory.ISlot;
import org.lanternpowered.server.inventory.behavior.HotbarBehavior;
import org.lanternpowered.server.inventory.behavior.VanillaHotbarBehavior;
import org.lanternpowered.server.inventory.transformation.InventoryTransforms;
import org.spongepowered.api.data.property.Property;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperties;
import org.spongepowered.api.item.inventory.InventoryTransformation;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;

import java.util.Optional;

@SuppressWarnings("unchecked")
public class LanternHotbarInventory extends AbstractInventoryRow implements Hotbar {

    private final HotbarBehavior hotbarBehavior = new VanillaHotbarBehavior();

    private AbstractChildrenInventory prioritySelected;

    @Override
    protected void init() {
        super.init();

        this.prioritySelected = AbstractChildrenInventory.viewBuilder()
                .inventory(new LanternHotbarSelectedSlot(this))
                .inventories(slots())
                .build();
    }

    /**
     * Gets the {@link ISlot} that is currently selected.
     *
     * @return The selected slot
     */
    public AbstractSlot getSelectedSlot() {
        final int slotIndex = this.hotbarBehavior.selectedSlotIndex;
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
    public IInventory transform(InventoryTransformation transformation) {
        if (transformation == InventoryTransforms.PRIORITY_SELECTED_SLOT_AND_HOTBAR) {
            return this.prioritySelected;
        } else if (transformation == InventoryTransforms.PRIORITY_HOTBAR) {
            return this;
        }
        return super.transform(transformation);
    }

    @Override
    public int getSelectedSlotIndex() {
        return this.hotbarBehavior.selectedSlotIndex;
    }

    @Override
    public void setSelectedSlotIndex(int index) {
        this.hotbarBehavior.selectedSlotIndex = index;
    }

    // The EquipmentSlotType off the selected hotbar slot is dynamic,
    // so you can't assign it directly to a slot instance


    @Override
    protected <V> Optional<V> tryGetProperty(Inventory child, Property<V> property) {
        if (property == InventoryProperties.EQUIPMENT_TYPE && child == getSelectedSlot()) {
            return Optional.of((V) EquipmentTypes.MAIN_HAND);
        }
        return super.tryGetProperty(child, property);
    }
}
