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
package org.lanternpowered.server.block.tile.vanilla;

import org.lanternpowered.server.data.ValueCollection;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.InventorySnapshot;
import org.lanternpowered.server.inventory.vanilla.VanillaInventoryArchetypes;
import org.lanternpowered.server.inventory.vanilla.block.ChestInventory;
import org.lanternpowered.server.network.tile.TileEntityProtocolTypes;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class LanternShulkerBox extends LanternContainerTile<ChestInventory> {

    public LanternShulkerBox() {
        setProtocolType(TileEntityProtocolTypes.DEFAULT);
    }

    @Override
    public void registerKeys() {
        super.registerKeys();
        final ValueCollection c = getValueCollection();
        c.register(Keys.DISPLAY_NAME, null);
        c.registerProcessor(LanternKeys.INVENTORY_SNAPSHOT).add(builder -> builder
                .offerHandler(((key, valueContainer, inventorySnapshot) -> {
                    this.inventory.clear();
                    inventorySnapshot.offerTo(this.inventory);
                    return DataTransactionResult.successNoData();
                }))
                .failAlwaysRemoveHandler()
                .retrieveHandler((key, valueContainer) -> Optional.of(InventorySnapshot.ofInventory(this.inventory))));
    }

    @Override
    protected ChestInventory createInventory() {
        return VanillaInventoryArchetypes.SHULKER_BOX.builder().withCarrier(this).build(Lantern.getMinecraftPlugin());
    }

    @Override
    protected void playOpenSound(Location location) {
        location.getWorld().playSound(SoundTypes.BLOCK_SHULKER_BOX_OPEN, SoundCategories.BLOCK,
                location.getPosition().add(0.5, 0.5, 0.5), 0.5, ThreadLocalRandom.current().nextDouble() * 0.1 + 0.9);
    }

    @Override
    protected void playCloseSound(Location location) {
        location.getWorld().playSound(SoundTypes.BLOCK_SHULKER_BOX_CLOSE, SoundCategories.BLOCK,
                location.getPosition().add(0.5, 0.5, 0.5), 0.5, ThreadLocalRandom.current().nextDouble() * 0.1 + 0.9);
    }
}
