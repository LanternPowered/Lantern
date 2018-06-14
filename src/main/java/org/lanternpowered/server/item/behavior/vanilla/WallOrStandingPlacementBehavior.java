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
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.behavior.types.PlaceBlockBehavior;
import org.lanternpowered.server.block.property.SolidMaterialProperty;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.item.behavior.simple.InteractWithBlockItemBaseBehavior;
import org.lanternpowered.server.util.rotation.RotationHelper;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("ConstantConditions")
public class WallOrStandingPlacementBehavior extends InteractWithBlockItemBaseBehavior {

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
        final Location<World> loc = context.getContext(ContextKeys.BLOCK_LOCATION).get();
        final Direction face = context.getContext(ContextKeys.INTERACTION_FACE).orElse(Direction.UP);
        final Location<World> solidFaceLoc = loc.getBlockRelative(face.getOpposite());
        final SolidMaterialProperty solidMaterialProperty = solidFaceLoc.getProperty(SolidMaterialProperty.class).get();
        if (!solidMaterialProperty.getValue()) {
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
