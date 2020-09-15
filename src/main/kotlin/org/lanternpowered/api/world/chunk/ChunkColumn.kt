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
package org.lanternpowered.api.world.chunk

import org.lanternpowered.api.world.World
import org.spongepowered.api.world.volume.biome.MutableBiomeVolume
import org.spongepowered.api.world.volume.block.MutableBlockVolume
import org.spongepowered.api.world.volume.block.entity.MutableBlockEntityVolume
import org.spongepowered.api.world.volume.entity.ReadableEntityVolume
import org.spongepowered.api.world.volume.game.HeightAwareVolume
import org.spongepowered.api.world.volume.game.UpdatableVolume

/**
 * Represents a column of [Chunk]s.
 */
interface ChunkColumn :
        MutableBlockVolume<ChunkColumn>,
        MutableBlockEntityVolume<ChunkColumn>,
        MutableBiomeVolume<ChunkColumn>,
        ReadableEntityVolume,
        UpdatableVolume,
        HeightAwareVolume {

    /**
     * The world this chunk column is located in.
     */
    val world: World

    /**
     * The position of the column
     */
    val position: ChunkColumnPosition
}
