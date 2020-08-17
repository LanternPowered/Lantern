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
package org.lanternpowered.api.world.dimension

typealias DimensionType = org.spongepowered.api.world.dimension.DimensionType
typealias DimensionTypes = org.spongepowered.api.world.dimension.DimensionTypes

/**
 * Whether player respawns are allowed in this dimension type.
 */
inline val DimensionType.allowsPlayerRespawns: Boolean
    get() = (this as ExtendedDimensionType).allowsPlayerRespawns

/**
 * Whether the there is skylight in this dimension type.
 */
inline val DimensionType.hasSkylight: Boolean
    get() = (this as ExtendedDimensionType).hasSkylight()

/**
 * Whether the water evaporates in this dimension type.
 */
inline val DimensionType.doesWaterEvaporate: Boolean
    get() = (this as ExtendedDimensionType).doesWaterEvaporate

/**
 * Whether the logic for cave worlds will apply to this dimension type.
 *
 * In vanilla minecraft, a {@code true} value means:
 * - Lava updates 3 times slower.
 * - Maps are half the size.
 * - Thunder storms will not occur.
 * - The height of the world is 128 instead of the default 256.
 */
inline val DimensionType.isCaveWorld: Boolean
    get() = (this as ExtendedDimensionType).isCaveWorld

/**
 * Whether the logic for surface worlds will apply to this dimension type.
 *
 * In vanilla minecraft, a {@code true} value means:
 * - Players can sleep here.
 * - Zombie Pigmen will not spawn around a nether portal.
 * - Client will render clouds.
 */
inline val DimensionType.isSurfaceWorld: Boolean
    get() = (this as ExtendedDimensionType).isSurfaceWorld

/**
 * An extended version of [DimensionType].
 */
interface ExtendedDimensionType : DimensionType {

    /**
     * Whether player respawns are allowed in this dimension type.
     */
    val allowsPlayerRespawns: Boolean

    /**
     * Whether the water evaporates in this dimension type.
     */
    val doesWaterEvaporate: Boolean

    /**
     * Whether the logic for cave worlds will apply to this dimension type.
     *
     * In vanilla minecraft, a {@code true} value means:
     * - Lava updates 3 times slower.
     * - Maps are half the size.
     * - Thunder storms will not occur.
     * - The height of the world is 128 instead of the default 256.
     */
    val isCaveWorld: Boolean

    /**
     * Whether the logic for surface worlds will apply to this dimension type.
     *
     * In vanilla minecraft, a {@code true} value means:
     * - Players can sleep here.
     * - Zombie Pigmen will not spawn around a nether portal.
     * - Client will render clouds.
     */
    val isSurfaceWorld: Boolean
}
