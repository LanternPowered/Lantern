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

import org.lanternpowered.server.inventory.LanternCraftingInventory;
import org.lanternpowered.server.inventory.LanternEquipmentInventory;
import org.lanternpowered.server.inventory.LanternGridInventory;
import org.lanternpowered.server.inventory.LanternOrderedInventory;
import org.lanternpowered.server.inventory.slot.LanternCraftingOutput;
import org.lanternpowered.server.inventory.slot.LanternEquipmentSlot;
import org.spongepowered.api.entity.living.Humanoid;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.entity.HumanInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.text.translation.Translation;

import java.lang.ref.WeakReference;
import java.util.Optional;

import javax.annotation.Nullable;

public class LanternHumanInventory extends LanternOrderedInventory implements HumanInventory {

    @Nullable private final WeakReference<Humanoid> humanoid;
    private Hotbar hotbar;

    public LanternHumanInventory(@Nullable Inventory parent, @Nullable Translation name, @Nullable Humanoid humanoid) {
        super(parent, name);
        this.humanoid = humanoid == null ? null : new WeakReference<>(humanoid);

        this.registerChild(new LanternCraftingInventory(this) {
            {
                this.registerSlot(new LanternCraftingOutput(this));
                this.registerChild(new LanternGridInventory(this) {
                    {
                        for (int y = 0; y < 2; y++) {
                            for (int x = 0; x < 2; x++) {
                                this.registerSlotAt(x, y);
                            }
                        }
                        this.finalizeContent();
                    }
                });
                this.finalizeContent();
            }
        });
        this.registerChild(new LanternEquipmentInventory(this, humanoid) {
            {
                this.registerSlot(new LanternEquipmentSlot(this, EquipmentTypes.HEADWEAR));
                this.registerSlot(new LanternEquipmentSlot(this, EquipmentTypes.CHESTPLATE));
                this.registerSlot(new LanternEquipmentSlot(this, EquipmentTypes.LEGGINGS));
                this.registerSlot(new LanternEquipmentSlot(this, EquipmentTypes.BOOTS));
                this.finalizeContent();
            }
        });
        this.registerChild(new HumanMainInventory(this, null) {
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
            }
        });
        this.registerChild(new OffHandSlot(this, null));
    }

    @Override
    public Hotbar getHotbar() {
        return this.hotbar;
    }

    @Override
    public Optional<Humanoid> getCarrier() {
        return this.humanoid == null ? Optional.empty() : Optional.ofNullable(this.humanoid.get());
    }
}
