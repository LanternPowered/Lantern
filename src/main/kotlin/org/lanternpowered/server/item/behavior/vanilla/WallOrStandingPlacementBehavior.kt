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
package org.lanternpowered.server.item.behavior.vanilla

import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.server.behavior.Behavior
import org.lanternpowered.server.behavior.BehaviorContext
import org.lanternpowered.server.behavior.ContextKeys
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline
import org.lanternpowered.server.block.LanternBlockType
import org.lanternpowered.server.block.behavior.types.PlaceBlockBehavior
import org.lanternpowered.server.data.key.LanternKeys
import org.lanternpowered.server.item.behavior.simple.InteractWithBlockItemBaseBehavior
import org.lanternpowered.server.util.rotation.RotationHelper
import org.lanternpowered.server.util.wrapDegRot
import org.spongepowered.api.block.BlockState
import org.spongepowered.api.block.BlockType
import org.spongepowered.api.data.Keys
import org.spongepowered.api.util.Direction
import kotlin.math.roundToInt

class WallOrStandingPlacementBehavior private constructor(
        wallTypeSupplier: () -> BlockState,
        standingTypeSupplier: () -> BlockState
) : InteractWithBlockItemBaseBehavior() {

    private val wallType by lazy(wallTypeSupplier)
    private val standingType by lazy(standingTypeSupplier)

    override fun place(pipeline: BehaviorPipeline<Behavior>, context: BehaviorContext): Boolean {
        val loc = context.getContext(ContextKeys.BLOCK_LOCATION).get()
        val face = context.getContext(ContextKeys.INTERACTION_FACE).orElse(Direction.UP)
        val solidFaceLoc = loc.relativeToBlock(face.opposite)
        val isSolidMaterial = solidFaceLoc.get(Keys.IS_SOLID).get()
        if (!isSolidMaterial) {
            return false
        }
        var blockState: BlockState
        if (face == Direction.UP) {
            blockState = this.standingType
            val player = context.getContext(ContextKeys.PLAYER).orNull()
            if (player != null) {
                val rot = (player.rotation.y - 180.0).wrapDegRot()
                val rotValue = (rot / 360.0 * 16.0).roundToInt() % 16
                blockState = blockState.with(LanternKeys.FINE_ROTATION, rotValue).get()
            }
        } else if (face != Direction.DOWN) {
            blockState = this.wallType
            blockState = blockState.with(Keys.DIRECTION, face).get()
        } else {
            return false
        }
        val blockType = blockState.type as LanternBlockType
        context.addContext(ContextKeys.BLOCK_TYPE, blockType)
        context.addContext(ContextKeys.USED_BLOCK_STATE, blockState)
        return context.process(blockType.pipeline.pipeline(PlaceBlockBehavior::class.java)) {
            ctx, bh: PlaceBlockBehavior -> bh.tryPlace(pipeline, ctx)
        }.isSuccess
    }

    companion object {

        fun ofTypes(wallTypeSupplier: () -> BlockType, standingTypeSupplier: () -> BlockType): WallOrStandingPlacementBehavior =
                WallOrStandingPlacementBehavior({ wallTypeSupplier().defaultState }, { standingTypeSupplier().defaultState })

        fun ofStates(wallTypeSupplier: () -> BlockState, standingTypeSupplier: () -> BlockState): WallOrStandingPlacementBehavior =
                WallOrStandingPlacementBehavior(wallTypeSupplier, standingTypeSupplier)
    }
}
