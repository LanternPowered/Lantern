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
package org.lanternpowered.server.item.behavior.vanilla;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.BlockSnapshotBuilder;
import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.trait.LanternEnumTraits;
import org.lanternpowered.server.item.behavior.types.InteractWithItemBehavior;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.EnumTrait;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.property.block.ReplaceableProperty;
import org.spongepowered.api.data.type.PortionType;
import org.spongepowered.api.data.type.PortionTypes;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings({"ConstantConditions", "unchecked"})
public class SlabItemInteractionBehavior<E extends Enum<E>> implements InteractWithItemBehavior {

    private final Supplier<BlockType> halfSlabType;
    private final Supplier<BlockType> doubleSlabType;
    private final EnumTrait<E> variantTrait;

    public SlabItemInteractionBehavior(EnumTrait<E> variantTrait,
            Supplier<BlockType> halfSlabType, Supplier<BlockType> doubleSlabType) {
        this.halfSlabType = halfSlabType;
        this.doubleSlabType = doubleSlabType;
        this.variantTrait = variantTrait;
    }

    @Override
    public BehaviorResult tryInteract(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final Optional<Location<World>> optLocation = context.getContext(ContextKeys.INTERACTION_LOCATION);
        if (!optLocation.isPresent()) {
            return BehaviorResult.CONTINUE;
        }

        final BlockType halfSlabType = this.halfSlabType.get();
        final BlockType doubleSlabType = this.doubleSlabType.get();

        Location<World> location = optLocation.get();
        final Direction blockFace = context.getContext(ContextKeys.INTERACTION_FACE).get().getOpposite();

        final LanternBlockType blockType = (LanternBlockType) context.getContext(ContextKeys.ITEM_TYPE).get().getBlock().get();
        if (blockType != halfSlabType) {
            return BehaviorResult.PASS;
        }

        BlockState state = location.getBlock();
        final BlockState.Builder stateBuilder = BlockState.builder();
        stateBuilder.blockType(blockType);
        context.getContext(ContextKeys.USED_ITEM_STACK).ifPresent(
                itemStack -> itemStack.getValues().forEach(value -> stateBuilder.add((Key) value.getKey(), value.get())));
        BlockState blockState = stateBuilder.build();
        BlockSnapshotBuilder snapshotBuilder = null;

        boolean success = false;
        if (state.getType() == blockType) {
            if (state.getTraitValue(this.variantTrait).get().equals(blockState.getTraitValue(this.variantTrait).get())) {
                final PortionType portionType = state.getTraitValue(LanternEnumTraits.PORTION_TYPE).get();
                if ((blockFace == Direction.DOWN && portionType == PortionTypes.BOTTOM) ||
                        (blockFace == Direction.UP && portionType == PortionTypes.TOP)) {
                    snapshotBuilder = BlockSnapshotBuilder.create().blockState(doubleSlabType.getDefaultState());
                    success = true;
                }
            }
        } else if (location.getProperty(ReplaceableProperty.class).get().getValue()) {
            success = true;
        }
        if (!success) {
            location = location.add(blockFace.getOpposite().asBlockOffset());
            state = location.getBlock();
            if (state.getType() == blockType) {
                if (state.getTraitValue(this.variantTrait).get().equals(blockState.getTraitValue(this.variantTrait).get())) {
                    final PortionType portionType = state.getTraitValue(LanternEnumTraits.PORTION_TYPE).get();
                    if ((blockFace == Direction.DOWN && portionType == PortionTypes.TOP) ||
                            (blockFace == Direction.UP && portionType == PortionTypes.BOTTOM)) {
                        snapshotBuilder = BlockSnapshotBuilder.create().blockState(doubleSlabType.getDefaultState());
                        success = true;
                    }
                }
            } else if (location.getProperty(ReplaceableProperty.class).get().getValue()) {
                success = true;
            }
        }
        if (success) {
            if (snapshotBuilder == null) {
                PortionType portionType;
                if (blockFace == Direction.UP) {
                    portionType = PortionTypes.TOP;
                } else if (blockFace == Direction.DOWN) {
                    portionType = PortionTypes.BOTTOM;
                } else {
                    final double y = location.getY() - location.getBlockY();
                    if (y >= 0.5) {
                        portionType = PortionTypes.TOP;
                    } else {
                        portionType = PortionTypes.BOTTOM;
                    }
                }
                snapshotBuilder = BlockSnapshotBuilder.create().blockState(halfSlabType.getDefaultState()).add(Keys.PORTION_TYPE, portionType);
            }
            final BlockSnapshotBuilder snapshotBuilder1 = snapshotBuilder;
            snapshotBuilder1.location(location);
            context.getContext(ContextKeys.USED_ITEM_STACK).ifPresent(
                    itemStack -> itemStack.getValues().forEach(value -> snapshotBuilder1.add((Key) value.getKey(), value.get())));
            context.addBlockChange(snapshotBuilder1.build());

            context.getContext(ContextKeys.PLAYER).ifPresent(player -> {
                if (!player.get(Keys.GAME_MODE).orElse(GameModes.NOT_SET).equals(GameModes.CREATIVE)) {
                    context.requireContext(ContextKeys.USED_SLOT).poll(1);
                }
            });
            return BehaviorResult.SUCCESS;
        }

        return BehaviorResult.FAIL;
    }
}
