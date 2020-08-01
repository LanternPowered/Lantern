/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.block.entity.vanilla;

import static org.lanternpowered.server.text.translation.TranslationHelper.tr;

import org.lanternpowered.server.block.state.BlockStateProperties;
import org.lanternpowered.server.data.type.LanternChestAttachmentType;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.AbstractGridInventory;
import org.lanternpowered.server.inventory.behavior.SimpleContainerShiftClickBehavior;
import org.lanternpowered.server.inventory.vanilla.VanillaInventoryArchetypes;
import org.lanternpowered.server.inventory.vanilla.block.ChestInventory;
import org.lanternpowered.server.network.block.BlockEntityProtocolTypes;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.entity.BlockEntity;
import org.spongepowered.api.block.entity.carrier.chest.Chest;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperties;
import org.spongepowered.api.item.inventory.gui.GuiIds;
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
        final LanternChestAttachmentType connection = blockState.getStateProperty(BlockStateProperties.CHEST_ATTACHMENT_TYPE).get();
        if (connection == LanternChestAttachmentType.SINGLE) {
            return Direction.NONE;
        }
        final Direction direction = blockState.getStateProperty(BlockStateProperties.HORIZONTAL_FACING).get();
        boolean left = connection == LanternChestAttachmentType.LEFT;
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
        setProtocolType(BlockEntityProtocolTypes.DEFAULT);
    }

    @Override
    public void registerKeys() {
        super.registerKeys();
        getKeyRegistry().register(Keys.DISPLAY_NAME);
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
        final LanternChestAttachmentType connection = blockState.getStateProperty(BlockStateProperties.CHEST_ATTACHMENT_TYPE).get();
        if (connection == LanternChestAttachmentType.SINGLE) {
            return Optional.empty();
        }
        final Direction direction = getConnectedDirection(blockState);
        final Optional<BlockEntity> optTileEntity = location.relativeTo(direction).getBlockEntity();
        if (optTileEntity.isPresent() && optTileEntity.get() instanceof LanternChest) {
            final LanternChest otherChest = (LanternChest) optTileEntity.get();
            final AbstractGridInventory.RowsViewBuilder<DoubleChestInventory> doubleChestBuilder = AbstractGridInventory.rowsViewBuilder()
                    .shiftClickBehavior(SimpleContainerShiftClickBehavior.INSTANCE)
                    .title(tr("container.chestDouble"))
                    .property(InventoryProperties.GUI_ID, GuiIds.CHEST)
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
        final LanternChestAttachmentType connection = blockState.getStateProperty(
                BlockStateProperties.CHEST_ATTACHMENT_TYPE).get();
        if (connection == LanternChestAttachmentType.SINGLE) {
            return Optional.empty();
        }
        final Direction direction = getConnectedDirection(blockState);
        return (Optional) location.relativeTo(direction).getBlockEntity()
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
