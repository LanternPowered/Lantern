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

import org.lanternpowered.server.inventory.AbstractChildrenInventory;
import org.lanternpowered.server.inventory.AbstractGridInventory;
import org.lanternpowered.server.inventory.AbstractSlot;
import org.lanternpowered.server.inventory.ContainerProvidedWrappedInventory;
import org.lanternpowered.server.inventory.ICarriedInventory;
import org.lanternpowered.server.inventory.IInventory;
import org.lanternpowered.server.inventory.QueryInventoryAdder;
import org.lanternpowered.server.inventory.transformation.InventoryTransforms;
import org.lanternpowered.server.inventory.type.LanternArmorEquipableInventory;
import org.spongepowered.api.data.property.PropertyMatcher;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.InventoryProperties;
import org.spongepowered.api.item.inventory.InventoryTransformation;
import org.spongepowered.api.item.inventory.entity.UserInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.item.inventory.query.QueryOperation;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.type.ViewableInventory;

public abstract class AbstractUserInventory<T extends User> extends AbstractChildrenInventory implements UserInventory<T>, ICarriedInventory<T> {

    private static final class Holder {

        private static final QueryOperation<?> OFF_HAND_OPERATION =
                QueryOperationTypes.PROPERTY.of(PropertyMatcher.of(InventoryProperties.EQUIPMENT_TYPE, EquipmentTypes.OFF_HAND));
    }

    private LanternPrimaryPlayerInventory mainInventory;
    private LanternPlayerArmorInventory armorInventory;
    private LanternArmorEquipableInventory equipmentInventory;
    private AbstractSlot offhandSlot;

    private AbstractChildrenInventory priorityHotbar;

    AbstractUserInventory() {
    }

    @Override
    protected void init() {
        super.init();

        // Search the the inventories for the helper methods
        this.mainInventory = query(LanternPrimaryPlayerInventory.class).get();
        this.armorInventory = query(LanternPlayerArmorInventory.class).get();
        this.offhandSlot = (AbstractSlot) query(Holder.OFF_HAND_OPERATION).first();

        this.priorityHotbar = AbstractChildrenInventory.viewBuilder()
                .inventory(this.armorInventory)
                .inventory(this.offhandSlot)
                .inventory(this.mainInventory.transform(InventoryTransforms.PRIORITY_HOTBAR))
                .plugin(getPlugin())
                .build();
        this.equipmentInventory = AbstractChildrenInventory.viewBuilder()
                .inventory(this.armorInventory)
                .inventory(new LanternHotbarSelectedSlot(this.mainInventory.getHotbar()))
                .inventory(this.offhandSlot)
                .type(LanternArmorEquipableInventory.class)
                .withCarrier(getCarrier().orElse(null))
                .plugin(getPlugin())
                .build();
    }

    @Override
    public IInventory transform(InventoryTransformation transformation) {
        // Cache some transformations that will be used often
        if (transformation == InventoryTransforms.PRIORITY_HOTBAR) {
            return this.priorityHotbar;
        }
        return super.transform(transformation);
    }

    @Override
    protected void queryInventories(QueryInventoryAdder adder) throws QueryInventoryAdder.Stop {
        // Complete equipment has higher priority over armor
        if (this.equipmentInventory != null) {
            adder.add(this.equipmentInventory);
        }
        super.queryInventories(adder);
    }

    @Override
    public LanternPrimaryPlayerInventory getPrimary() {
        return this.mainInventory;
    }

    @Override
    public LanternArmorEquipableInventory getEquipment() {
        return this.equipmentInventory;
    }

    public LanternPlayerArmorInventory getArmor() {
        return this.armorInventory;
    }

    @Override
    public AbstractSlot getOffhand() {
        return this.offhandSlot;
    }

    @Override
    public LanternHotbarInventory getHotbar() {
        return getPrimary().getHotbar();
    }

    @Override
    public AbstractGridInventory getStorage() {
        return getPrimary().getStorage();
    }

    @Override
    protected ViewableInventory toViewable() {
        // This container can normally not be opened on the client,
        // so create a chest inventory which represents this inventory.
        return new ContainerProvidedWrappedInventory(this,
                viewer -> new ViewedPlayerInventory(viewer.getInventory(),
                        // Only view armor and off hand in top
                        AbstractChildrenInventory.viewBuilder()
                                .shiftClickBehavior(PlayerInventoryShiftClickBehavior.INSTANCE)
                                .inventory(this.armorInventory)
                                .inventory(this.offhandSlot)
                                .build()));
    }
}
