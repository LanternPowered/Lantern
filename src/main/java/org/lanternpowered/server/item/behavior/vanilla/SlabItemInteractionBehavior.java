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
package org.lanternpowered.server.item.behavior.vanilla;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.BlockSnapshotBuilder;
import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.state.BlockStateProperties;
import org.lanternpowered.server.data.type.LanternSlabPortion;
import org.lanternpowered.server.item.behavior.types.InteractWithItemBehavior;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.property.block.ReplaceableProperty;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings({"ConstantConditions", "unchecked"})
public class SlabItemInteractionBehavior implements InteractWithItemBehavior {

    @Nullable
    private BlockSnapshotBuilder tryPlaceSlabAt(Location location, Direction blockFace,
            LanternBlockType blockType) {
        BlockState state = location.getBlock();
        // There is already a half/double slab placed
        if (state.getType() == blockType) {
            final LanternSlabPortion type = state.getTraitValue(BlockStateProperties.SLAB_PORTION).get();
            // Already a full block
            if (type == LanternSlabPortion.DOUBLE) {
                return null;
            }
            final double y = location.getY() - location.getBlockY();
            boolean success = false;
            if (blockFace == Direction.UP) {
                success = type == LanternSlabPortion.TOP || y >= 0.5;
            } else if (blockFace == Direction.DOWN) {
                success = type == LanternSlabPortion.BOTTOM || y < 0.5;
            } else if ((y < 0.5 && type == LanternSlabPortion.TOP) ||
                    (y >= 0.5 && type == LanternSlabPortion.BOTTOM)) {
                success = true;
            }
            if (!success) {
                return null;
            }
            return BlockSnapshotBuilder.create().blockState(state
                    .withTrait(BlockStateProperties.SLAB_PORTION, LanternSlabPortion.DOUBLE).get());
        } else if (!location.getProperty(ReplaceableProperty.class).get().getValue()) {
            return null;
        } else {
            // We can replace the block
            final double y = location.getY() - location.getBlockY();
            final LanternSlabPortion type;
            if (blockFace == Direction.UP) {
                type = LanternSlabPortion.BOTTOM;
            } else if (blockFace == Direction.DOWN) {
                type = LanternSlabPortion.TOP;
            } else if (y >= 0.5) {
                type = LanternSlabPortion.TOP;
            } else {
                type = LanternSlabPortion.BOTTOM;
            }
            return BlockSnapshotBuilder.create().blockState(blockType.getDefaultState()
                    .withTrait(BlockStateProperties.SLAB_PORTION, type).get());
        }
    }

    @Override
    public BehaviorResult tryInteract(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final Optional<Location> optLocation = context.getContext(ContextKeys.INTERACTION_LOCATION);
        if (!optLocation.isPresent()) {
            return BehaviorResult.CONTINUE;
        }

        Location location = optLocation.get();

        final Direction blockFace = context.getContext(ContextKeys.INTERACTION_FACE).get();
        final LanternBlockType blockType = (LanternBlockType) context.getContext(ContextKeys.ITEM_TYPE).get().getBlock().get();

        BlockSnapshotBuilder builder = tryPlaceSlabAt(location, blockFace, blockType);
        if (builder == null) {
            location = location.add(blockFace.asOffset());
            builder = tryPlaceSlabAt(location, blockFace, blockType);
            if (builder == null) {
                return BehaviorResult.FAIL;
            }
        }

        context.addBlockChange(builder.location(location).build());
        context.getContext(ContextKeys.PLAYER).ifPresent(player -> {
            if (!player.get(Keys.GAME_MODE).orElse(GameModes.NOT_SET).equals(GameModes.CREATIVE)) {
                context.requireContext(ContextKeys.USED_SLOT).poll(1);
            }
        });
        return BehaviorResult.SUCCESS;
    }
}
