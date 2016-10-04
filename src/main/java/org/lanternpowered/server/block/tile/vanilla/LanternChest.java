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
package org.lanternpowered.server.block.tile.vanilla;

import org.lanternpowered.server.block.tile.LanternTileEntity;
import org.lanternpowered.server.block.type.BlockChest;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.inventory.IViewerListener;
import org.lanternpowered.server.inventory.LanternContainer;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBlockAction;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.Chest;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.Random;

public class LanternChest extends LanternTileEntity implements Chest {

    private static final int OPEN_SOUND_DELAY = 5;
    private static final int CLOSE_SOUND_DELAY = 10;

    private final TileChestInventory chestInventory = new TileChestInventory(null, this);
    private final Random random = new Random();

    {
        this.chestInventory.add(new IViewerListener() {
            @Override
            public void onViewerAdded(Viewer viewer, LanternContainer container) {
                if (playersCount++ == 0) {
                    soundDelay = OPEN_SOUND_DELAY;
                }
            }

            @Override
            public void onViewerRemoved(Viewer viewer, LanternContainer container) {
                if (--playersCount == 0) {
                    soundDelay = CLOSE_SOUND_DELAY;
                }
            }
        });
    }

    private int playersCount = 0;
    private int counter = 0;

    private int soundDelay;

    @Override
    public void registerKeys() {
        super.registerKeys();
        this.registerKey(Keys.DISPLAY_NAME, null);
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
                            this.chestInventory, ((LanternChest) optTileEntity.get()).chestInventory));
                } else {
                    return Optional.of(new TileDoubleChestInventory(null, null,
                            ((LanternChest) optTileEntity.get()).chestInventory, this.chestInventory));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public TileChestInventory getInventory() {
        return this.chestInventory;
    }

    @Override
    public BlockState getBlock() {
        final BlockState block = this.getLocation().getBlock();
        return block.getType() == BlockTypes.CHEST || block.getType() == BlockTypes.TRAPPED_CHEST ? block : BlockTypes.CHEST.getDefaultState();
    }

    @Override
    public void pulse() {
        super.pulse();

        if (this.counter++ % 15 == 0) {
            final Location<World> location = this.getLocation();
            final LanternWorld world = (LanternWorld) location.getExtent();
            world.broadcast(() -> new MessagePlayOutBlockAction(location.getBlockPosition(),
                    BlockRegistryModule.get().getStateInternalId(location.getBlock()), 1, this.playersCount));
        }

        if (this.soundDelay > 0 && --this.soundDelay == 0) {
            final SoundType soundType;
            if (this.playersCount > 0) {
                soundType = SoundTypes.BLOCK_CHEST_OPEN;
            } else {
                soundType = SoundTypes.BLOCK_CHEST_CLOSE;
            }
            final Location<World> location = this.getLocation();
            //noinspection ConstantConditions
            location.getExtent().playSound(soundType, SoundCategories.BLOCK,
                    location.getPosition().add(0.5, 0.5, 0.5), 0.5, this.random.nextDouble() * 0.1 + 0.9);
        }
    }
}
