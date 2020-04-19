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

import kotlin.Lazy;
import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.BlockProperties;
import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.behavior.types.PlaceBlockBehavior;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.item.behavior.simple.InteractWithBlockItemBaseBehavior;
import org.lanternpowered.server.util.rotation.RotationHelper;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;

import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("ConstantConditions")
public class WallOrStandingPlacementBehavior extends InteractWithBlockItemBaseBehavior {

    public static WallOrStandingPlacementBehavior ofTypes(
            Lazy<BlockType> wallTypeSupplier,
            Lazy<BlockType> standingTypeSupplier) {
        return new WallOrStandingPlacementBehavior(
                () -> wallTypeSupplier.getValue().getDefaultState(),
                () -> standingTypeSupplier.getValue().getDefaultState());
    }

    public static WallOrStandingPlacementBehavior ofTypes(
            Supplier<BlockType> wallTypeSupplier,
            Supplier<BlockType> standingTypeSupplier) {
        return new WallOrStandingPlacementBehavior(
                () -> wallTypeSupplier.get().getDefaultState(),
                () -> standingTypeSupplier.get().getDefaultState());
    }

    public static WallOrStandingPlacementBehavior ofStates(
            Supplier<BlockState> wallTypeSupplier,
            Supplier<BlockState> standingTypeSupplier) {
        return new WallOrStandingPlacementBehavior(wallTypeSupplier, standingTypeSupplier);
    }

    private final Supplier<BlockState> wallTypeSupplier;
    private final Supplier<BlockState> standingTypeSupplier;

    private WallOrStandingPlacementBehavior(
            Supplier<BlockState> wallTypeSupplier,
            Supplier<BlockState> standingTypeSupplier) {
        this.wallTypeSupplier = wallTypeSupplier;
        this.standingTypeSupplier = standingTypeSupplier;
    }

    @Override
    protected boolean place(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final Location loc = context.getContext(ContextKeys.BLOCK_LOCATION).get();
        final Direction face = context.getContext(ContextKeys.INTERACTION_FACE).orElse(Direction.UP);
        final Location solidFaceLoc = loc.relativeToBlock(face.getOpposite());
        final boolean isSolidMaterial = solidFaceLoc.getProperty(BlockProperties.IS_SOLID_MATERIAL).get();
        if (!isSolidMaterial) {
            return false;
        }
        BlockState blockState;
        if (face == Direction.UP) {
            blockState = this.standingTypeSupplier.get();
            final Optional<Player> optPlayer = context.getContext(ContextKeys.PLAYER);
            if (optPlayer.isPresent()) {
                final double rot = RotationHelper.wrapDegRotation(optPlayer.get().getRotation().getY() - 180.0);
                final int rotValue = (int) Math.round((rot / 360.0) * 16.0) % 16;
                blockState = blockState.with(LanternKeys.FINE_ROTATION, rotValue).get();
            }
        } else if (face != Direction.DOWN) {
            blockState = this.wallTypeSupplier.get();
            blockState = blockState.with(Keys.DIRECTION, face).get();
        } else {
            return false;
        }
        final LanternBlockType blockType = (LanternBlockType) blockState.getType();
        context.addContext(ContextKeys.BLOCK_TYPE, blockType);
        context.addContext(ContextKeys.USED_BLOCK_STATE, blockState);
        return context.process(blockType.getPipeline().pipeline(PlaceBlockBehavior.class),
                (context1, behavior1) -> behavior1.tryPlace(pipeline, context1)).isSuccess();
    }
}
