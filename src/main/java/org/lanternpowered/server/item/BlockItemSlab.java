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
package org.lanternpowered.server.item;

import org.lanternpowered.server.block.type.BlockSlabBase;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.trait.EnumTrait;
import org.spongepowered.api.data.property.block.ReplaceableProperty;
import org.spongepowered.api.data.type.PortionType;
import org.spongepowered.api.data.type.PortionTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;

public class BlockItemSlab extends BlockItemType {

    public BlockItemSlab(String pluginId, String identifier, BlockSlabBase blockType) {
        super(pluginId, identifier, blockType);
    }

    @Override
    public ItemInteractionResult onInteractWithItemAt(@Nullable Player player, ItemStack itemStack,
            ItemInteractionType interactionType, Location<World> clickedLocation, Direction blockFace) {
        final BlockSlabBase slabBase = (BlockSlabBase) this.blockType;
        if (slabBase.isHalf()) {
            BlockState state = clickedLocation.getBlock();
            BlockState blockState = null;
            boolean success = false;
            if (state.getType() == this.blockType) {
                final EnumTrait variantTrait = (EnumTrait) slabBase.getVariantTrait();
                if (state.getTraitValue(variantTrait).get()
                        .equals(state.getType().getDefaultState().getTraitValue(variantTrait).get())) {
                    PortionType portionType = state.getTraitValue(BlockSlabBase.PORTION).get();
                    if ((blockFace == Direction.DOWN && portionType == PortionTypes.BOTTOM) ||
                            (blockFace == Direction.UP && portionType == PortionTypes.TOP)) {
                        blockState = slabBase.getDouble().getDefaultState().withTrait(variantTrait,
                                state.getTraitValue(variantTrait).get()).get();
                        success = true;
                    }
                }
            } else if (clickedLocation.getProperty(ReplaceableProperty.class).get().getValue() == Boolean.TRUE) {
                success = true;
            }
            if (!success) {
                clickedLocation = clickedLocation.add(blockFace.getOpposite().asBlockOffset());
                state = clickedLocation.getBlock();
                if (state.getType() == this.blockType) {
                    final EnumTrait variantTrait = (EnumTrait) slabBase.getVariantTrait();
                    if (state.getTraitValue(variantTrait).get()
                            .equals(state.getType().getDefaultState().getTraitValue(variantTrait).get())) {
                        PortionType portionType = state.getTraitValue(BlockSlabBase.PORTION).get();
                        if ((blockFace == Direction.DOWN && portionType == PortionTypes.TOP) ||
                                (blockFace == Direction.UP && portionType == PortionTypes.BOTTOM)) {
                            blockState = slabBase.getDouble().getDefaultState().withTrait(variantTrait,
                                    state.getTraitValue(variantTrait).get()).get();
                            success = true;
                        }
                    }
                } else if (clickedLocation.getProperty(ReplaceableProperty.class).get().getValue() == Boolean.TRUE) {
                    success = true;
                }
            }
            if (success) {
                if (blockState == null) {
                    PortionType portionType;
                    if (blockFace == Direction.UP) {
                        portionType = PortionTypes.TOP;
                    } else if (blockFace == Direction.DOWN) {
                        portionType = PortionTypes.BOTTOM;
                    } else {
                        final double y = clickedLocation.getY() - clickedLocation.getBlockY();
                        if (y >= 0.5) {
                            portionType = PortionTypes.TOP;
                        } else {
                            portionType = PortionTypes.BOTTOM;
                        }
                    }
                    blockState = slabBase.getDefaultState().withTrait(BlockSlabBase.PORTION, portionType).get();
                }
            } else {
                return ItemInteractionResult.pass();
            }
            itemStack = itemStack.copy();
            itemStack.setQuantity(itemStack.getQuantity() - 1);
            clickedLocation.setBlock(blockState, Cause.source(player == null ? itemStack : player).build());
            return ItemInteractionResult.builder()
                    .type(ItemInteractionResult.Type.SUCCESS)
                    .resultItem(itemStack.createSnapshot())
                    .build();
        } else {
            return super.onInteractWithItemAt(player, itemStack, interactionType, clickedLocation, blockFace);
        }
    }
}
