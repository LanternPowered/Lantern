package org.lanternpowered.api.behavior.basic.block.place

import org.lanternpowered.api.behavior.BehaviorContext
import org.lanternpowered.api.behavior.BehaviorType
import org.lanternpowered.api.behavior.basic.PlaceBlockBehaviorBase
import org.lanternpowered.api.block.BlockSnapshotBuilder
import org.lanternpowered.api.ext.*
import org.lanternpowered.server.behavior.BehaviorResult
import org.lanternpowered.server.block.LanternBlockType
import org.lanternpowered.server.ext.*
import org.lanternpowered.server.world.extent.IExtent
import org.spongepowered.api.entity.ExperienceOrb
import org.spongepowered.api.entity.Item

class PlaceCollisonDetectionBehavior : PlaceBlockBehaviorBase {

    override fun apply(type: BehaviorType, ctx: BehaviorContext, placed: MutableList<BlockSnapshotBuilder>): Boolean {
        for (snapshot in placed) {
            val optLoc = snapshot.location
            val blockState = snapshot.getState()
            val collisionBoxesProvider = (blockState.getType() as LanternBlockType).collisionBoxesProvider
            if (collisionBoxesProvider != null) {
                val collisionBoxes = collisionBoxesProvider.get(blockState, null, null)
                for (collisionBox in collisionBoxes) {
                    if ((loc.getExtent() as IExtent).hasIntersectingEntities(collisionBox.offset(loc.getBlockPosition())
                            ) { entity -> !(entity is Item || entity is ExperienceOrb) }) { // TODO: Configure this filter somewhere?
                        return BehaviorResult.FAIL
                    }
                }
            }
        }
        return BehaviorResult.CONTINUE
    }
}
