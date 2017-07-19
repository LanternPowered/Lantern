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
package org.lanternpowered.server.inventory.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.inventory.AbstractChildrenInventory;
import org.lanternpowered.server.inventory.AbstractInventory;
import org.lanternpowered.server.inventory.AbstractMutableInventory;
import org.lanternpowered.server.inventory.LanternCraftingGridInventory;
import org.lanternpowered.server.inventory.LanternCraftingInventory;
import org.lanternpowered.server.inventory.LanternEquipmentInventory;
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

    private final Map<HumanInventoryView, AbstractMutableInventory> inventoryViews = new EnumMap<>(HumanInventoryView.class);

    public LanternPlayerInventory(@Nullable Inventory parent, @Nullable Translation name, @Nullable Player player) {
        super(parent, name);
        this.player = player == null ? null : new WeakReference<>(player);

        registerChild(new LanternCraftingInventory(this) {
            {
                registerSlot(new LanternCraftingOutput(this));
                registerChild(new LanternCraftingGridInventory(this) {
                    {
                        for (int y = 0; y < 2; y++) {
                            for (int x = 0; x < 2; x++) {
                                registerSlotAt(x, y, new LanternCraftingInput(this));
                            }
                        }
                        finalizeContent();
                    }
                });
                finalizeContent();
            }
        });
        this.equipmentInventory = registerChild(new LanternEquipmentInventory(this, player) {
            {
                registerSlot(new LanternEquipmentSlot(this, EquipmentTypes.HEADWEAR));
                registerSlot(new LanternEquipmentSlot(this, EquipmentTypes.CHESTPLATE));
                registerSlot(new LanternEquipmentSlot(this, EquipmentTypes.LEGGINGS));
                registerSlot(new LanternEquipmentSlot(this, EquipmentTypes.BOOTS));
                this.slots.forEach(slot -> slot.setMaxStackSize(1));
                finalizeContent();
            }
        });
        this.mainInventory = registerChild(new HumanMainInventory(this, null) {
            {
                for (int y = 0; y < 3; y++) {
                    for (int x = 0; x < 9; x++) {
                        registerSlotAt(x, y);
                    }
                }
                hotbar = registerRow(3, new LanternHotbar(this) {
                    {
                        for (int x = 0; x < 9; x++) {
                            registerSlotAt(x);
                        }
                        finalizeContent();
                    }
                });
                finalizeContent();
                prioritizeChild(hotbar);
            }
        });
        this.offHandSlot = registerChild(new OffHandSlot(this, null));

        // Generate inventory views
        this.inventoryViews.put(HumanInventoryView.MAIN,
                generateMainView(this, this.mainInventory));
        this.inventoryViews.put(HumanInventoryView.REVERSE_MAIN_AND_HOTBAR,
                generateReverseMainAndHotbarView(this, this.mainInventory));
        this.inventoryViews.put(HumanInventoryView.PRIORITY_MAIN_AND_HOTBAR,
                generatePriorityMainAndHotbarView(this, this.mainInventory));
        this.inventoryViews.put(HumanInventoryView.ALL_PRIORITY_MAIN,
                generateAllPriorityMainView(this.mainInventory));
        this.inventoryViews.put(HumanInventoryView.RAW_INVENTORY,
                generateRawInventoryView(this.mainInventory, this.equipmentInventory, this.offHandSlot));

        // This is the default views/inventories
        this.inventoryViews.put(HumanInventoryView.MAIN_AND_PRIORITY_HOTBAR, this.mainInventory);
        this.inventoryViews.put(HumanInventoryView.HOTBAR, this.hotbar);
    }

    public LanternOrderedInventory getRawInventoryView() {
        return (LanternOrderedInventory) this.inventoryViews.get(HumanInventoryView.RAW_INVENTORY);
    }

    private AbstractMutableInventory generateRawInventoryView(HumanMainInventory mainInventory,
            LanternEquipmentInventory equipmentInventory, OffHandSlot offHandSlot) {
        return new LanternOrderedInventory(null, null) {
            {
                registerChild(new HumanMainInventory(this, null) {
                    {
                        registerRow(0, hotbar);
                        for (int i = 0; i < 3; i++) {
                            registerRow(i + 1, mainInventory.getRow(i).get());
                        }
                    }
                });
                registerChild(equipmentInventory);
                registerChild(offHandSlot);
            }
        };
    }

    private AbstractMutableInventory generateMainView(@Nullable Inventory parent, HumanMainInventory mainInventory) {
        final List<AbstractInventory> children = new ArrayList<>(mainInventory.getChildren());
        children.removeAll(this.hotbar.getChildren());
        return new AbstractChildrenInventory(parent, null, children);
    }

    private AbstractMutableInventory generateReverseMainAndHotbarView(@Nullable Inventory parent, HumanMainInventory mainInventory) {
        final List<AbstractInventory> children = new ArrayList<>(mainInventory.getChildren());
        final List<AbstractInventory> hotbarSlots = new ArrayList<>(this.hotbar.getChildren());
        children.removeAll(hotbarSlots);
        Collections.reverse(children);
        Collections.reverse(hotbarSlots);
        children.addAll(0, hotbarSlots);
        return new AbstractChildrenInventory(parent, null, children);
    }

    private AbstractMutableInventory generatePriorityMainAndHotbarView(@Nullable Inventory parent, HumanMainInventory mainInventory) {
        final List<AbstractInventory> children = new ArrayList<>(mainInventory.getChildren());
        final List<AbstractInventory> hotbarSlots =  new ArrayList<>(this.hotbar.getChildren());
        children.removeAll(hotbarSlots);
        children.addAll(hotbarSlots);
        return new AbstractChildrenInventory(parent, null, children);
    }

    private AbstractMutableInventory generateAllPriorityMainView(HumanMainInventory mainInventory) {
        final List<AbstractInventory> children = new ArrayList<>(this.getChildren());
        return new AbstractChildrenInventory(null, null) {
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
    public AbstractMutableInventory getInventoryView(HumanInventoryView inventoryView) {
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
