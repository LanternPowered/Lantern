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
package org.lanternpowered.server.block.type;

import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.PropertyProviders;
import org.lanternpowered.server.block.tile.LanternTileEntityType;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.inventory.AbstractInventory;
import org.lanternpowered.server.inventory.ContainerViewListener;
import org.lanternpowered.server.item.ItemInteractionResult;
import org.lanternpowered.server.item.ItemInteractionType;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.tileentity.EnderChest;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.TileEntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nullable;

public class BlockEnderChest extends LanternBlockType implements IBlockContainer {

    public BlockEnderChest(String pluginId, String identifier,
            @Nullable Function<BlockType, ItemType> itemTypeBuilder) {
        super(pluginId, identifier, itemTypeBuilder, BlockChest.FACING);
        modifyDefaultState(state -> state.withTrait(BlockChest.FACING, Direction.NORTH).get());
        modifyPropertyProviders(builder -> {
            builder.add(PropertyProviders.hardness(22.5));
            builder.add(PropertyProviders.blastResistance(1000.0));
        });
    }

    @Override
    public ItemInteractionResult onInteractWithItemAt(@Nullable Player player, @Nullable ItemStack itemStack,
            ItemInteractionType interactionType, Location<World> location, Direction blockFace) {
        if (player != null) {
            final TileEntity tileEntity = location.getTileEntity().orElse(null);
            if (tileEntity instanceof EnderChest) {
                final AbstractInventory inventory = (AbstractInventory) player.getEnderChestInventory();
                inventory.add((ContainerViewListener) tileEntity);
                player.openInventory(player.getEnderChestInventory(), Cause.source(player).build());
                return ItemInteractionResult.success();
            }
        }
        return ItemInteractionResult.pass();
    }

    @Override
    public Optional<BlockState> placeBlockAt(@Nullable Player player, ItemStack itemStack,
            ItemInteractionType interactionType, Location<World> location, Direction blockFace) {
        final BlockState state = super.placeBlockAt(player, itemStack, interactionType, location, blockFace).get();
        final Direction facing;
        if (player != null) {
            facing = ((LanternEntity) player).getHorizontalDirection(Direction.Division.CARDINAL).getOpposite();
        } else {
            facing = Direction.NORTH;
        }
        return Optional.of(state.withTrait(BlockChest.FACING, facing).get());
    }

    @Override
    public TileEntity createTile(BlockState blockState) {
        return ((LanternTileEntityType) TileEntityTypes.ENDER_CHEST).getTileEntityConstructor().get();
    }
}
