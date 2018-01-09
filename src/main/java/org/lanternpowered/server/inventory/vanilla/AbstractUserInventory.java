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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Lists;
import org.lanternpowered.server.inventory.AbstractGridInventory;
import org.lanternpowered.server.inventory.AbstractInventory;
import org.lanternpowered.server.inventory.AbstractOrderedInventory;
import org.lanternpowered.server.inventory.AbstractSlot;
import org.lanternpowered.server.inventory.CarrierReference;
import org.lanternpowered.server.inventory.type.LanternOrderedInventory;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.entity.UserInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.item.inventory.property.EquipmentSlotType;
import org.spongepowered.api.item.inventory.query.QueryOperation;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

public abstract class AbstractUserInventory<T extends User> extends AbstractOrderedInventory implements UserInventory<T> {

    private static final class Holder {

        private static final QueryOperation<?> MAIN_INVENTORY_OPERATION =
                QueryOperationTypes.INVENTORY_TYPE.of(LanternMainPlayerInventory.class);
        private static final QueryOperation<?> EQUIPMENT_INVENTORY_OPERATION =
                QueryOperationTypes.INVENTORY_TYPE.of(LanternPlayerEquipmentInventory.class);
        private static final QueryOperation<?> OFF_HAND_OPERATION =
                QueryOperationTypes.INVENTORY_PROPERTY.of(EquipmentSlotType.of(EquipmentTypes.OFF_HAND));
    }

    private final CarrierReference<T> carrierReference;

    private LanternMainPlayerInventory mainInventory;
    private LanternPlayerEquipmentInventory equipmentInventory;
    private AbstractSlot offhandSlot;

    private final EnumMap<View, AbstractInventory> views = new EnumMap<>(View.class);

    protected AbstractUserInventory(Class<T> userType) {
        this.carrierReference = CarrierReference.of(userType);
    }

    /**
     * Gets the specified inventory view.
     *
     * @param view The view type
     * @return The inventory view
     */
    public AbstractInventory getView(View view) {
        checkNotNull(view, "view");
        if (view == View.HOTBAR) {
            return this.mainInventory.getHotbar();
        } else if (view == View.MAIN) {
            return this.mainInventory.getGrid();
        } else if (view == View.MAIN_AND_PRIORITY_HOTBAR) {
            return this.mainInventory;
        }
        return this.views.get(view);
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
        this.equipmentInventory = query(Holder.EQUIPMENT_INVENTORY_OPERATION).first();
        this.offhandSlot = query(Holder.OFF_HAND_OPERATION).first();

        // Construct the inventory views

        final LanternOrderedInventory priorityMainAndHotbar = AbstractOrderedInventory.viewBuilder()
                .inventory(this.mainInventory.getGrid())
                .inventory(this.mainInventory.getHotbar())
                .build();
        this.views.put(View.PRIORITY_MAIN_AND_HOTBAR, priorityMainAndHotbar);

        final List<AbstractInventory> inventories = new ArrayList<>();
        inventories.addAll(getChildren());
        inventories.set(inventories.indexOf(this.mainInventory), priorityMainAndHotbar);
        this.views.put(View.ALL_PRIORITY_MAIN, AbstractOrderedInventory.viewBuilder()
                .inventories(inventories)
                .build());

        this.views.put(View.REVERSE_MAIN_AND_HOTBAR, AbstractOrderedInventory.viewBuilder()
                .inventories(Lists.reverse(Lists.newArrayList(this.mainInventory.orderedSlots())))
                .build());
    }

    @Override
    public LanternMainPlayerInventory getMain() {
        return this.mainInventory;
    }

    @Override
    public LanternPlayerEquipmentInventory getEquipment() {
        return this.equipmentInventory;
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

    /**
     * The different kind of {@link Inventory} views that can be
     * used for the {@link AbstractUserInventory}. This mainly
     * modifies the insertion/poll order of item stacks. And the
     * which sub {@link Inventory}s are available.
     */
    public enum View {
        /**
         * The hotbar inventory view. Contains only the hotbar.
         */
        HOTBAR,
        /**
         * The main inventory view. Contains only the main inventory,
         * excludes the hotbar.
         */
        MAIN,
        /**
         * The main and hotbar inventory.
         */
        MAIN_AND_PRIORITY_HOTBAR,
        /**
         * The main and hotbar inventory, but the main inventory
         * has priority for offer/poll functions.
         */
        PRIORITY_MAIN_AND_HOTBAR,
        /**
         * The reverse order for the main and hotbar inventory. Starting
         * from the bottom right corner, then going left until the row
         * is finished and doing this for every row until the most
         * upper one is reached.
         */
        REVERSE_MAIN_AND_HOTBAR,
        /**
         * All the inventories but the main inventory has priority over
         * the hotbar.
         */
        ALL_PRIORITY_MAIN,
    }
}
