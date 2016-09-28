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
package org.lanternpowered.server.block.type;

import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.PropertyProviders;
import org.lanternpowered.server.block.tile.LanternTileEntityType;
import org.lanternpowered.server.block.trait.LanternEnumTrait;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.item.ItemInteractionResult;
import org.lanternpowered.server.item.ItemInteractionType;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.TileEntityTypes;
import org.spongepowered.api.block.tileentity.carrier.Chest;
import org.spongepowered.api.block.trait.EnumTrait;
import org.spongepowered.api.data.key.Keys;
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

public class BlockChest extends LanternBlockType implements IBlockContainer {

    public static final EnumTrait<Direction> FACING = LanternEnumTrait.of("facing", Keys.DIRECTION,
            Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);

    public BlockChest(String pluginId, String identifier,
            @Nullable Function<BlockType, ItemType> itemTypeBuilder) {
        super(pluginId, identifier, itemTypeBuilder, FACING);
        this.modifyDefaultState(state -> state.withTrait(FACING, Direction.NORTH).get());
        this.modifyPropertyProviders(builder -> {
            builder.add(PropertyProviders.hardness(2.5));
            builder.add(PropertyProviders.blastResistance(12.5));
        });
    }

    @Override
    public ItemInteractionResult onInteractWithItemAt(@Nullable Player player, @Nullable ItemStack itemStack,
            ItemInteractionType interactionType, Location<World> clickedLocation, Direction blockFace) {
        if (player != null) {
            final TileEntity tileEntity = clickedLocation.getTileEntity().orElse(null);
            if (tileEntity instanceof Chest) {
                player.openInventory(((Chest) tileEntity).getDoubleChestInventory().orElse(
                        ((Chest) tileEntity).getInventory()), Cause.source(player).build());
                return ItemInteractionResult.success();
            }
        }
        return ItemInteractionResult.pass();
    }

    public static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST };

    @Override
    public Optional<BlockState> placeBlockAt(@Nullable Player player, ItemStack itemStack,
            ItemInteractionType interactionType, Location<World> location, Direction blockFace) {
        final Optional<BlockState> state = super.placeBlockAt(player, itemStack, interactionType, location, blockFace);
        if (!state.isPresent()) {
            return state;
        }

        Location<World> otherChestLoc = null;
        // Check whether the chest already a double chest is,
        // and fail if this is the case
        for (Direction directionToCheck : HORIZONTAL_DIRECTIONS) {
            otherChestLoc = location.getRelative(directionToCheck);
            // We found a chest
            if (otherChestLoc.getBlock().getType() == this) {
                // Check if it isn't already double
                for (Direction directionToCheck1 : HORIZONTAL_DIRECTIONS) {
                    final Location<World> loc1 = otherChestLoc.getRelative(directionToCheck1);
                    if (loc1.getBlock().getType() == this) {
                        return Optional.empty();
                    }
                }
            } else {
                otherChestLoc = null;
            }
        }

        // Get the direction the chest should face
        final Direction facing;
        if (player != null) {
            facing = ((LanternEntity) player).getHorizontalDirection(Direction.Division.CARDINAL).getOpposite();
        } else {
            facing = Direction.NORTH;
        }

        // Rotate the other chest in the same one as we placed
        if (otherChestLoc != null) {
            otherChestLoc.setBlock(otherChestLoc.getBlock().withTrait(FACING, facing).get(),
                    Cause.source(player == null ? itemStack : player).build());
        }

        return Optional.of(state.get().withTrait(FACING, facing).get());
    }

    @Override
    public TileEntity createTile(BlockState blockState) {
        return ((LanternTileEntityType) TileEntityTypes.CHEST).getTileEntityConstructor().get();
    }
}
