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

import static org.lanternpowered.server.text.translation.TranslationHelper.tr;

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.AbstractGridInventory;
import org.lanternpowered.server.inventory.behavior.SimpleContainerShiftClickBehavior;
import org.lanternpowered.server.inventory.vanilla.VanillaInventoryArchetypes;
import org.lanternpowered.server.inventory.vanilla.block.ChestInventory;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.Chest;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.property.GuiIdProperty;
import org.spongepowered.api.item.inventory.property.GuiIds;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class LanternChest extends LanternContainerTile<ChestInventory> implements Chest {

    private static final Direction[] HORIZONTAL_DIRECTIONS = { Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST };

    private final Random random = new Random();

    @Override
    public void registerKeys() {
        super.registerKeys();
        getValueCollection().register(Keys.DISPLAY_NAME, null);
    }

    @Override
    protected ChestInventory createInventory() {
        return VanillaInventoryArchetypes.CHEST.builder()
                .withCarrier(this)
                .build(Lantern.getMinecraftPlugin());
    }

    @Override
    public Inventory getInventory(Direction from) {
        return getDoubleChestInventory().orElse(super.getInventory(from));
    }

    @Override
    public Optional<Inventory> getDoubleChestInventory() {
        if (!isValid()) {
            return Optional.empty();
        }
        final Location<World> location = getLocation();
        for (Direction directionToCheck : HORIZONTAL_DIRECTIONS) {
            final Location<World> loc = location.getRelative(directionToCheck);
            if (loc.getBlock().getType() != getBlock().getType()) {
                continue;
            }
            final Optional<TileEntity> optTileEntity = location.getRelative(directionToCheck).getTileEntity();
            if (optTileEntity.isPresent() && optTileEntity.get() instanceof LanternChest) {
                final LanternChest otherChest = (LanternChest) optTileEntity.get();
                final AbstractGridInventory.RowsViewBuilder<DoubleChestInventory> doubleChestBuilder = AbstractGridInventory.rowsViewBuilder()
                        .shiftClickBehavior(SimpleContainerShiftClickBehavior.INSTANCE)
                        .title(tr("container.chestDouble"))
                        .property(new GuiIdProperty(GuiIds.CHEST))
                        .type(DoubleChestInventory.class)
                        .withCarrier(new DoubleChestBlockCarrier(Arrays.asList(this, otherChest)));
                if (directionToCheck != Direction.WEST && directionToCheck != Direction.NORTH) {
                    doubleChestBuilder
                            .grid(0, this.inventory)
                            .grid(3, otherChest.inventory)
                            .withCarrier(new DoubleChestBlockCarrier(Arrays.asList(this, otherChest)));
                } else {
                    doubleChestBuilder
                            .grid(0, otherChest.inventory)
                            .grid(3, this.inventory)
                            .withCarrier(new DoubleChestBlockCarrier(Arrays.asList(otherChest, this)));
                }
                final DoubleChestInventory doubleChestInventory = doubleChestBuilder.build();
                doubleChestInventory.addViewListener(this);
                doubleChestInventory.addViewListener(otherChest);
                return Optional.of(doubleChestInventory);
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<Chest> getConnectedChests() {
        if (!isValid()) {
            return Collections.emptySet();
        }
        final Location<World> location = getLocation();
        final Set<Chest> chests = new HashSet<>();
        for (Direction directionToCheck : HORIZONTAL_DIRECTIONS) {
            final Location<World> loc = location.getRelative(directionToCheck);
            if (loc.getBlock().getType() != getBlock().getType()) {
                continue;
            }
            final Optional<TileEntity> optTileEntity = location.getRelative(directionToCheck).getTileEntity();
            if (optTileEntity.isPresent() && optTileEntity.get() instanceof LanternChest) {
                chests.add((Chest) optTileEntity.get());
            }
        }
        return Collections.unmodifiableSet(chests);
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
        final BlockState block = getLocation().getBlock();
        return block.getType() == BlockTypes.CHEST || block.getType() == BlockTypes.TRAPPED_CHEST ? block : BlockTypes.CHEST.getDefaultState();
    }
}
