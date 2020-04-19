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
package org.lanternpowered.server.game.registry.type.block;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.registry.CatalogRegistryModule;

import java.util.Optional;

public interface BlockRegistry extends CatalogRegistryModule<BlockType> {

    /**
     * Registers a new catalog type in the registry.
     *
     * @param blockType the block type
     */
    <A extends BlockType> A register(A blockType);

    /**
     * Gets the block state by using it's internal id.
     * 
     * @param internalId the internal id
     * @return the block state
     */
    Optional<BlockState> getStateByInternalId(int internalId);

    /**
     * Gets the internal id of the specified block state.
     *
     * @param blockState the block state
     * @return the internal id
     */
    int getStateInternalId(BlockState blockState);

}
