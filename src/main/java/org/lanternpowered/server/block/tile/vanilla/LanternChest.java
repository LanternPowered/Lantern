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

import org.lanternpowered.server.block.trait.LanternEnumTraits;
import org.lanternpowered.server.data.type.LanternChestAttachment;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.AbstractGridInventory;
import org.lanternpowered.server.inventory.behavior.SimpleContainerShiftClickBehavior;
import org.lanternpowered.server.inventory.vanilla.VanillaInventoryArchetypes;
import org.lanternpowered.server.inventory.vanilla.block.ChestInventory;
import org.lanternpowered.server.network.tile.TileEntityProtocolTypes;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.Chest;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.property.GuiIdProperty;
import org.spongepowered.api.item.inventory.property.GuiIds;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class LanternChest extends ContainerBlockEntity<ChestInventory> implements Chest {

    public static final class DoubleChestInventory extends ChestInventory {

        @Override
        public InventoryArchetype getArchetype() {
            return VanillaInventoryArchetypes.DOUBLE_CHEST;
        }
    }

    public static Direction getConnectedDirection(BlockState blockState) {
        final LanternChestAttachment connection = blockState.getTraitValue(LanternEnumTraits.CHEST_ATTACHMENT).get();
        if (connection == LanternChestAttachment.SINGLE) {
            return Direction.NONE;
        }
        final Direction direction = blockState.getTraitValue(LanternEnumTraits.HORIZONTAL_FACING).get();
        boolean left = connection == LanternChestAttachment.LEFT;
        switch (direction) {
            case NORTH:
                return left ? Direction.EAST : Direction.WEST;
            case SOUTH:
                return left ? Direction.WEST : Direction.EAST;
            case EAST:
                return left ? Direction.SOUTH : Direction.NORTH;
            case WEST:
                return left ? Direction.NORTH : Direction.SOUTH;
            default:
                throw new IllegalStateException();
        }
    }

    public LanternChest() {
        setProtocolType(TileEntityProtocolTypes.DEFAULT);
    }

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
        final Location location =  getLocation();
        final BlockState blockState = location.getBlock();
        final LanternChestAttachment connection = blockState.getTraitValue(LanternEnumTraits.CHEST_ATTACHMENT).get();
        if (connection == LanternChestAttachment.SINGLE) {
            return Optional.empty();
        }
        final Direction direction = getConnectedDirection(blockState);
        final Optional<TileEntity> optTileEntity = location.getRelative(direction).getTileEntity();
        if (optTileEntity.isPresent() && optTileEntity.get() instanceof LanternChest) {
            final LanternChest otherChest = (LanternChest) optTileEntity.get();
            final AbstractGridInventory.RowsViewBuilder<DoubleChestInventory> doubleChestBuilder = AbstractGridInventory.rowsViewBuilder()
                    .shiftClickBehavior(SimpleContainerShiftClickBehavior.INSTANCE)
                    .title(tr("container.chestDouble"))
                    .property(GuiIdProperty.builder().value(GuiIds.CHEST).build())
                    .type(DoubleChestInventory.class)
                    .withCarrier(new DoubleChestBlockCarrier(Arrays.asList(this, otherChest)));
            if (direction != Direction.WEST && direction != Direction.NORTH) {
                doubleChestBuilder
                        .grid(0, this.inventory)
                        .grid(3, otherChest.inventory);
            } else {
                doubleChestBuilder
                        .grid(0, otherChest.inventory)
                        .grid(3, this.inventory);
            }
            final DoubleChestInventory doubleChestInventory = doubleChestBuilder.build();
            doubleChestInventory.addViewListener(this);
            doubleChestInventory.addViewListener(otherChest);
            return Optional.of(doubleChestInventory);
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private Optional<LanternChest> getConnectedChest() {
        if (!isValid()) {
            return Optional.empty();
        }
        final Location location =  getLocation();
        final BlockState blockState = location.getBlock();
        final LanternChestAttachment connection = blockState.getTraitValue(
                LanternEnumTraits.CHEST_ATTACHMENT).get();
        if (connection == LanternChestAttachment.SINGLE) {
            return Optional.empty();
        }
        final Direction direction = getConnectedDirection(blockState);
        return (Optional) location.getRelative(direction).getTileEntity()
                .filter(LanternChest.class::isInstance);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Chest> getConnectedChests() {
        return (Set) getConnectedChest().map(Collections::singleton).orElse(Collections.emptySet());
    }

    @Override
    protected void playOpenSound(Location location) {
        location.getWorld().playSound(SoundTypes.BLOCK_CHEST_OPEN, SoundCategories.BLOCK,
                location.getPosition().add(0.5, 0.5, 0.5), 0.5, ThreadLocalRandom.current().nextDouble() * 0.1 + 0.9);
    }

    @Override
    protected void playCloseSound(Location location) {
        location.getWorld().playSound(SoundTypes.BLOCK_CHEST_CLOSE, SoundCategories.BLOCK,
                location.getPosition().add(0.5, 0.5, 0.5), 0.5, ThreadLocalRandom.current().nextDouble() * 0.1 + 0.9);
    }
}
