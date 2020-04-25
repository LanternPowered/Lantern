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
package org.lanternpowered.server.registry.type.block

import org.lanternpowered.api.block.BlockState
import org.lanternpowered.api.block.BlockTypes
import org.lanternpowered.server.registry.mutableInternalCatalogTypeRegistry
import org.lanternpowered.server.util.palette.asPalette

val BlockStateRegistry = mutableInternalCatalogTypeRegistry<BlockState>()

/**
 * The global [BlockState] palette.
 */
val GlobalBlockStatePalette = BlockStateRegistry.asPalette { BlockTypes.AIR.get().defaultState }
