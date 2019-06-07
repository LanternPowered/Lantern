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

import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.inventory.AbstractChildrenInventory;
import org.lanternpowered.server.inventory.CarrierBasedTransformer;
import org.lanternpowered.server.inventory.LanternInventoryArchetype;
import org.lanternpowered.server.inventory.type.LanternChildrenInventory;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.animal.Donkey;
import org.spongepowered.api.entity.living.animal.Horse;
import org.spongepowered.api.entity.living.animal.Mule;
import org.spongepowered.api.item.inventory.Carrier;

import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;

public class HorseCarrierBasedTransformer implements CarrierBasedTransformer<LanternChildrenInventory, LanternChildrenInventory.Builder<LanternChildrenInventory>> {

    @Nullable private LanternInventoryArchetype<LanternChildrenInventory> horse;
    @Nullable private LanternInventoryArchetype<LanternChildrenInventory> donkeyMule;
    @Nullable private LanternInventoryArchetype<LanternChildrenInventory> donkeyMuleChested;

    @Nullable
    @Override
    public LanternInventoryArchetype<LanternChildrenInventory> apply(Carrier carrier,
            Supplier<AbstractChildrenInventory.Builder<LanternChildrenInventory>> builderSupplier) {
        if (carrier instanceof Donkey ||
                carrier instanceof Mule) {
            return applyDonkeyMule(builderSupplier,
                    ((Entity) carrier).get(LanternKeys.HAS_CHEST).orElse(false));
        } else if (carrier instanceof Horse) {
            return applyHorse(builderSupplier);
        }
        return null;
    }

    @Nullable
    @Override
    public LanternInventoryArchetype<LanternChildrenInventory> apply(Class<? extends Carrier> carrierType,
            Supplier<AbstractChildrenInventory.Builder<LanternChildrenInventory>> builderSupplier) {
        if (Donkey.class.isAssignableFrom(carrierType) ||
                Mule.class.isAssignableFrom(carrierType)) {
            return applyDonkeyMule(builderSupplier, false);
        } else if (Horse.class.isAssignableFrom(carrierType)) {
            return applyHorse(builderSupplier);
        }
        return null;
    }

    private LanternInventoryArchetype<LanternChildrenInventory> applyHorse(
            Supplier<AbstractChildrenInventory.Builder<LanternChildrenInventory>> builderSupplier) {
        if (this.horse == null) {
            this.horse = builderSupplier.get()
                    .addLast(VanillaInventoryArchetypes.SADDLE_SLOT)
                    .addLast(VanillaInventoryArchetypes.HORSE_ARMOR_SLOT)
                    .buildArchetype("minecraft", "real_horse"); // Better naming?
        }
        return this.horse;
    }

    @SuppressWarnings("ConstantConditions")
    private LanternInventoryArchetype<LanternChildrenInventory> applyDonkeyMule(
            Supplier<AbstractChildrenInventory.Builder<LanternChildrenInventory>> builderSupplier, boolean chest) {
        if (this.donkeyMule == null) {
            final AbstractChildrenInventory.Builder<LanternChildrenInventory> builder = builderSupplier.get()
                    .addLast(VanillaInventoryArchetypes.SADDLE_SLOT) // The slot exists, but isn't visible
                    .addLast(VanillaInventoryArchetypes.NULL_SLOT);
            this.donkeyMule = builder
                    .buildArchetype("minecraft", "donkey_mule"); // Better naming?
            this.donkeyMuleChested = builder
                    .addLast(VanillaInventoryArchetypes.DONKEY_MULE_CHEST)
                    .buildArchetype("minecraft", "donkey_mule_chested");
        }
        if (chest) {
            return this.donkeyMuleChested;
        } else {
            return this.donkeyMule;
        }
    }
}
