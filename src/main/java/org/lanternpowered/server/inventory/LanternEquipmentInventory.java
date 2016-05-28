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
package org.lanternpowered.server.inventory;

import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentInventory;
import org.spongepowered.api.text.translation.Translation;

import java.lang.ref.WeakReference;
import java.util.Optional;

import javax.annotation.Nullable;

public class LanternEquipmentInventory extends SimpleEquipmentInventory implements EquipmentInventory {

    @Nullable private final WeakReference<ArmorEquipable> armorEquipable;

    public LanternEquipmentInventory(@Nullable Inventory parent, @Nullable ArmorEquipable armorEquipable) {
        this(parent, null, armorEquipable);
    }

    public LanternEquipmentInventory(@Nullable Inventory parent, @Nullable Translation name,
            @Nullable ArmorEquipable armorEquipable) {
        super(parent, name);
        this.armorEquipable = armorEquipable == null ? null : new WeakReference<>(armorEquipable);
    }

    @Override
    public Optional<ArmorEquipable> getCarrier() {
        return this.armorEquipable == null ? Optional.empty() : Optional.ofNullable(this.armorEquipable.get());
    }
}
