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

import org.lanternpowered.server.inventory.AbstractGridInventory;
import org.lanternpowered.server.inventory.AbstractOrderedInventory;
import org.lanternpowered.server.inventory.AbstractSlot;
import org.lanternpowered.server.inventory.CarrierReference;
import org.lanternpowered.server.inventory.IInventory;
import org.lanternpowered.server.inventory.transformation.InventoryTransforms;
import org.lanternpowered.server.inventory.type.LanternArmorEquipableInventory;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.InventoryTransformation;
import org.spongepowered.api.item.inventory.entity.UserInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.item.inventory.property.EquipmentSlotType;
import org.spongepowered.api.item.inventory.query.QueryOperation;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

import java.util.Optional;

public abstract class AbstractUserInventory<T extends User> extends AbstractOrderedInventory implements UserInventory<T> {

    private static final class Holder {

        private static final QueryOperation<?> MAIN_INVENTORY_OPERATION =
                QueryOperationTypes.INVENTORY_TYPE.of(LanternMainPlayerInventory.class);
        private static final QueryOperation<?> ARMOR_INVENTORY_OPERATION =
                QueryOperationTypes.INVENTORY_TYPE.of(LanternPlayerArmorInventory.class);
        private static final QueryOperation<?> OFF_HAND_OPERATION =
                QueryOperationTypes.INVENTORY_PROPERTY.of(EquipmentSlotType.of(EquipmentTypes.OFF_HAND));
    }

    private final CarrierReference<T> carrierReference;

    private LanternMainPlayerInventory mainInventory;
    private LanternPlayerArmorInventory armorInventory;
    private LanternArmorEquipableInventory equipmentInventory;
    private AbstractSlot offhandSlot;

    private AbstractOrderedInventory priorityHotbar;

    AbstractUserInventory(Class<T> userType) {
        this.carrierReference = CarrierReference.of(userType);
    }

    @Override
    public Optional<T> getCarrier() {
        return this.carrierReference.get();
    }

    @Override
    protected void setCarrier(Carrier carrier) {
        super.setCarrier(carrier);
        this.carrierReference.set(carrier);
    }

    @Override
    protected void init() {
        super.init();

        // Search the the inventories for the helper methods
        this.mainInventory = query(Holder.MAIN_INVENTORY_OPERATION).first();
        this.armorInventory = query(Holder.ARMOR_INVENTORY_OPERATION).first();
        this.offhandSlot = query(Holder.OFF_HAND_OPERATION).first();

        this.priorityHotbar = AbstractOrderedInventory.viewBuilder()
                .inventory(this.armorInventory)
                .inventory(this.offhandSlot)
                .inventory(this.mainInventory.transform(InventoryTransforms.PRIORITY_HOTBAR))
                .plugin(getPlugin())
                .build();
        this.equipmentInventory = AbstractOrderedInventory.viewBuilder()
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
    public LanternMainPlayerInventory getMain() {
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
        return getMain().getHotbar();
    }

    @Override
    public AbstractGridInventory getMainGrid() {
        return getMain().getGrid();
    }
}
