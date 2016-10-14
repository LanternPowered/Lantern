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

import org.lanternpowered.server.block.type.BlockChest;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.Chest;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.Random;

public class LanternChest extends LanternContainer<TileChestInventory> implements Chest {

    private final Random random = new Random();

    @Override
    public void registerKeys() {
        super.registerKeys();
        this.registerKey(Keys.DISPLAY_NAME, null);
    }

    @Override
    protected TileChestInventory createInventory() {
        return new TileChestInventory(null, this);
    }

    @Override
    public Optional<Inventory> getDoubleChestInventory() {
        if (!this.isValid()) {
            return Optional.empty();
        }
        final Location<World> location = this.getLocation();
        for (Direction directionToCheck : BlockChest.HORIZONTAL_DIRECTIONS) {
            final Location<World> loc = location.getRelative(directionToCheck);
            if (loc.getBlock().getType() != this.getBlock().getType()) {
                continue;
            }
            final Optional<TileEntity> optTileEntity = location.getRelative(directionToCheck).getTileEntity();
            if (optTileEntity.isPresent() && optTileEntity.get() instanceof LanternChest) {
                if (directionToCheck != Direction.WEST && directionToCheck != Direction.NORTH) {
                    return Optional.of(new TileDoubleChestInventory(null, null,
                            this.inventory, ((LanternChest) optTileEntity.get()).inventory));
                } else {
                    return Optional.of(new TileDoubleChestInventory(null, null,
                            ((LanternChest) optTileEntity.get()).inventory, this.inventory));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    protected void playOpenSound(Location<World> location) {
        location.getExtent().playSound(SoundTypes.BLOCK_CHEST_OPEN, SoundCategories.BLOCK,
                location.getPosition().add(0.5, 0.5, 0.5), 0.5, this.random.nextDouble() * 0.1 + 0.9);
    }

    @Override
    protected void playCloseSound(Location<World> location) {
        location.getExtent().playSound(SoundTypes.BLOCK_CHEST_CLOSE, SoundCategories.BLOCK,
                location.getPosition().add(0.5, 0.5, 0.5), 0.5, this.random.nextDouble() * 0.1 + 0.9);
    }

    @Override
    public BlockState getBlock() {
        final BlockState block = this.getLocation().getBlock();
        return block.getType() == BlockTypes.CHEST || block.getType() == BlockTypes.TRAPPED_CHEST ? block : BlockTypes.CHEST.getDefaultState();
    }
}
