/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
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
package org.lanternpowered.server.block;

import java.util.function.Function;

import javax.annotation.Nullable;

import org.lanternpowered.server.catalog.CatalogTypeRegistry;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;

public interface BlockRegistry extends CatalogTypeRegistry<BlockType> {

    /**
     * Registers a new catalog type in the registry with a predefined internal id.
     * 
     * @param internalId the internal id
     * @param blockType the block type
     * @param dataValueGenerator the data value generator used to transform the block state into a data value
     */
    void register(int internalId, BlockType blockType, Function<BlockState, Byte> dataValueGenerator);

    /**
     * Registers a new catalog type in the registry with a predefined internal id.
     * 
     * <p>This method should only be used for block types that have
     * only one block state. (No attached block traits.) If this is the case,
     * use the {@link #register(BlockType, Function)} method</p>
     * 
     * @param internalId the internal id
     * @param blockType the block type
     */
    void register(int internalId, BlockType blockType);

    /**
     * Registers a new catalog type in the registry.
     * 
     * <p>This method should only be used for custom block types that have
     * only one block state. (No attached block traits.) If this is the case,
     * use the {@link #register(BlockType, Function)} method</p>
     * 
     * @param blockType the block type
     */
    @Override
    void register(BlockType blockType);

    /**
     * Registers a new catalog type in the registry.
     * 
     * @param blockType the block type
     * @param dataValueGenerator the data value generator used to transform the block state into a data value
     */
    void register(BlockType blockType, Function<BlockState, Byte> dataValueGenerator);

    /**
     * Gets the block state by using it's internal id.
     * 
     * @param internalId the internal id
     * @return the block state
     */
    @Nullable
    BlockState getStateByInternalId(int internalId);

    /**
     * Gets the internal id of the block state.
     * 
     * @param blockState the block state
     * @return the internal id
     */
    @Nullable
    Short getInternalStateId(BlockState blockState);

    /**
     * Gets the internal id of the default state of the block type.
     * 
     * @param blockType the block type
     * @return the internal id
     */
    @Nullable
    Short getInternalStateId(BlockType blockType);

    /**
     * Gets the block type by using it's internal id.
     * 
     * @param internalId the internal id
     * @return the block type
     */
    @Nullable
    BlockType getTypeByInternalId(int internalId);

    /**
     * Gets the internal id of the block type.
     * 
     * @param blockType the block type
     * @return the internal id
     */
    @Nullable
    Short getInternalTypeId(BlockType blockType);

    /**
     * Gets the block state by using it's internal id and data value.
     * 
     * @param internalId the internal id
     * @param data the data value
     * @return the block state
     */
    @Nullable
    BlockState getStateByInternalIdAndData(int internalId, byte data);

    /**
     * Gets the block state by using it's block type and data value.
     * 
     * @param blockType the block type
     * @param data the data value
     * @return the block state
     */
    @Nullable
    BlockState getStateByTypeAndData(BlockType blockType, byte data);

    /**
     * Gets the data value of the block state.
     * 
     * @param blockState the block state
     * @return the data value
     */
    @Nullable
    Byte getStateData(BlockState blockState);
}
