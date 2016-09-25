/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.inventory.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.inventory.ChildrenInventoryBase;
import org.lanternpowered.server.inventory.InventoryBase;
import org.lanternpowered.server.inventory.LanternCraftingInventory;
import org.lanternpowered.server.inventory.LanternEquipmentInventory;
import org.lanternpowered.server.inventory.LanternGridInventory;
import org.lanternpowered.server.inventory.LanternOrderedInventory;
import org.lanternpowered.server.inventory.slot.LanternCraftingInput;
import org.lanternpowered.server.inventory.slot.LanternCraftingOutput;
import org.lanternpowered.server.inventory.slot.LanternEquipmentSlot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.text.translation.Translation;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

public class LanternPlayerInventory extends LanternOrderedInventory implements PlayerInventory {

    @Nullable private final WeakReference<Player> player;

    private LanternHotbar hotbar;
    private final LanternEquipmentInventory equipmentInventory;
    private final HumanMainInventory mainInventory;
    private final OffHandSlot offHandSlot;

    private final Map<HumanInventoryView, InventoryBase> inventoryViews = new EnumMap<>(HumanInventoryView.class);

    public LanternPlayerInventory(@Nullable Inventory parent, @Nullable Translation name, @Nullable Player player) {
        super(parent, name);
        this.player = player == null ? null : new WeakReference<>(player);

        this.registerChild(new LanternCraftingInventory(this) {
            {
                this.registerSlot(new LanternCraftingOutput(this));
                this.registerChild(new LanternGridInventory(this) {
                    {
                        for (int y = 0; y < 2; y++) {
                            for (int x = 0; x < 2; x++) {
                                this.registerSlotAt(x, y, new LanternCraftingInput(this));
                            }
                        }
                        this.finalizeContent();
                    }
                });
                this.finalizeContent();
            }
        });
        this.equipmentInventory = this.registerChild(new LanternEquipmentInventory(this, player) {
            {
                this.registerSlot(new LanternEquipmentSlot(this, EquipmentTypes.HEADWEAR));
                this.registerSlot(new LanternEquipmentSlot(this, EquipmentTypes.CHESTPLATE));
                this.registerSlot(new LanternEquipmentSlot(this, EquipmentTypes.LEGGINGS));
                this.registerSlot(new LanternEquipmentSlot(this, EquipmentTypes.BOOTS));
                this.finalizeContent();
            }
        });
        this.mainInventory = this.registerChild(new HumanMainInventory(this, null) {
            {
                for (int y = 0; y < 3; y++) {
                    for (int x = 0; x < 9; x++) {
                        this.registerSlotAt(x, y);
                    }
                }
                hotbar = this.registerRow(3, new LanternHotbar(this) {
                    {
                        for (int x = 0; x < 9; x++) {
                            this.registerSlotAt(x);
                        }
                        this.finalizeContent();
                    }
                });
                this.finalizeContent();
                this.prioritizeChild(hotbar);
            }
        });
        this.offHandSlot = this.registerChild(new OffHandSlot(this, null));

        // Generate inventory views
        this.inventoryViews.put(HumanInventoryView.MAIN,
                this.generateMainView(this, this.mainInventory));
        this.inventoryViews.put(HumanInventoryView.REVERSE_MAIN_AND_HOTBAR,
                this.generateReverseMainAndHotbarView(this, this.mainInventory));
        final InventoryBase priorityMainAndHotbarView = this.generatePriorityMainAndHotbarView(this, this.mainInventory);
        this.inventoryViews.put(HumanInventoryView.PRIORITY_MAIN_AND_HOTBAR,
                priorityMainAndHotbarView);
        this.inventoryViews.put(HumanInventoryView.ALL_PRIORITY_MAIN,
                this.generateAllPriorityMainView(this.mainInventory));
        this.inventoryViews.put(HumanInventoryView.RAW_INVENTORY,
                this.generateRawInventoryView(this.mainInventory, this.equipmentInventory, this.offHandSlot));

        // This is the default views/inventories
        this.inventoryViews.put(HumanInventoryView.MAIN_AND_PRIORITY_HOTBAR, this.mainInventory);
        this.inventoryViews.put(HumanInventoryView.HOTBAR, this.hotbar);
    }

    private InventoryBase generateRawInventoryView(HumanMainInventory mainInventory,
            LanternEquipmentInventory equipmentInventory, OffHandSlot offHandSlot) {
        return new LanternOrderedInventory(null, null) {
            {
                this.registerChild(new HumanMainInventory(this, null) {
                    {
                        this.registerRow(0, hotbar);
                        for (int i = 0; i < 3; i++) {
                            this.registerRow(i + 1, mainInventory.getRow(i).get());
                        }
                    }
                });
                this.registerChild(equipmentInventory);
                this.registerChild(offHandSlot);
            }
        };
    }

    private InventoryBase generateMainView(@Nullable Inventory parent, HumanMainInventory mainInventory) {
        final List<InventoryBase> children = new ArrayList<>(mainInventory.getChildren());
        children.removeAll(this.hotbar.getChildren());
        return new ChildrenInventoryBase(parent, null, children);
    }

    private InventoryBase generateReverseMainAndHotbarView(@Nullable Inventory parent, HumanMainInventory mainInventory) {
        final List<InventoryBase> children = new ArrayList<>(mainInventory.getChildren());
        final List<InventoryBase> hotbarSlots = new ArrayList<>(this.hotbar.getChildren());
        children.removeAll(hotbarSlots);
        Collections.reverse(children);
        Collections.reverse(hotbarSlots);
        children.addAll(0, hotbarSlots);
        return new ChildrenInventoryBase(parent, null, children);
    }

    private InventoryBase generatePriorityMainAndHotbarView(@Nullable Inventory parent, HumanMainInventory mainInventory) {
        final List<InventoryBase> children = new ArrayList<>(mainInventory.getChildren());
        final List<InventoryBase> hotbarSlots =  new ArrayList<>(this.hotbar.getChildren());
        children.removeAll(hotbarSlots);
        children.addAll(hotbarSlots);
        return new ChildrenInventoryBase(parent, null, children);
    }

    private InventoryBase generateAllPriorityMainView(HumanMainInventory mainInventory) {
        final List<InventoryBase> children = new ArrayList<>(this.getChildren());
        return new ChildrenInventoryBase(null, null) {
            {
                children.set(children.indexOf(mainInventory), generatePriorityMainAndHotbarView(this, mainInventory));
                children.forEach(this::registerChild);
            }
        };
    }

    /**
     * Gets the {@link Inventory} for the specified
     * {@link HumanInventoryView}.
     *
     * @param inventoryView The inventory view
     * @return The inventory
     */
    public InventoryBase getInventoryView(HumanInventoryView inventoryView) {
        return this.inventoryViews.get(checkNotNull(inventoryView, "inventoryView"));
    }

    @Override
    public LanternHotbar getHotbar() {
        return this.hotbar;
    }

    @Override
    public HumanMainInventory getMain() {
        return this.mainInventory;
    }

    @Override
    public LanternEquipmentInventory getEquipment() {
        return this.equipmentInventory;
    }

    @Override
    public OffHandSlot getOffhand() {
        return this.offHandSlot;
    }

    @Override
    public Optional<Player> getCarrier() {
        return this.player == null ? Optional.empty() : Optional.ofNullable(this.player.get());
    }
}
