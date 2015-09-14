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
