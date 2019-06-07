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
package org.lanternpowered.server.entity.living.player;

import static org.lanternpowered.server.world.chunk.LanternChunk.ALL_SECTIONS_BIT_MASK;
import static org.lanternpowered.server.world.chunk.LanternChunk.CHUNK_SECTION_VOLUME;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import org.lanternpowered.server.block.action.BlockAction;
import org.lanternpowered.server.block.entity.LanternBlockEntity;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.network.block.AbstractBlockEntityProtocol;
import org.lanternpowered.server.network.block.BlockEntityChunkProtocolData;
import org.lanternpowered.server.network.block.BlockEntityProtocolHelper;
import org.lanternpowered.server.network.block.BlockEntityProtocolUpdateContext;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBlockAction;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBlockChange;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBlockEntity;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutChunkData;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutMultiBlockChange;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutUnloadChunk;
import org.lanternpowered.server.util.collect.array.VariableValueArray;
import org.lanternpowered.server.util.palette.Palette;
import org.lanternpowered.server.util.palette.PaletteType;
import org.lanternpowered.server.world.LanternWorld;
import org.lanternpowered.server.world.WorldEventListener;
import org.lanternpowered.server.world.chunk.ChunkBlockStateArray;
import org.lanternpowered.server.world.chunk.LanternChunk;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.chunk.Chunk;
import org.spongepowered.math.vector.Vector2i;
import org.spongepowered.math.vector.Vector3i;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class ObservedChunkManager implements WorldEventListener {

    /**
     * The {@link World} attached to the observed chunk manager.
     */
    private final LanternWorld world;

    /**
     * All the chunks that are being observed.
     */
    private final Map<Long, ObservedChunk> observedChunks = new ConcurrentHashMap<>();

    public ObservedChunkManager(LanternWorld world) {
        this.world = world;
    }

    public void pulse() {
        this.observedChunks.values().forEach(ObservedChunkManager.ObservedChunk::streamChanges);
    }

    @Override
    public void onLoadChunk(LanternChunk chunk) {
        final ObservedChunk observedChunk = this.observedChunks.get(chunk.getKey());
        if (observedChunk != null) {
            observedChunk.streamChunkLoad(chunk);
        }
    }

    @Override
    public void onUnloadChunk(LanternChunk chunk) {
        final ObservedChunk observedChunk = this.observedChunks.get(chunk.getKey());
        if (observedChunk != null) {
            observedChunk.streamChunkUnload(chunk);
        }
    }

    @Override
    public void onPopulateChunk(LanternChunk chunk) {
        final ObservedChunk observedChunk = this.observedChunks.get(chunk.getKey());
        if (observedChunk != null) {
            observedChunk.dirtyChunk = true;
        }
    }

    @Override
    public void onBlockChange(int x, int y, int z, BlockState oldBlockState, BlockState newBlockState) {
        final long key = LanternChunk.key(x >> 4, z >> 4);
        final ObservedChunk observedChunk = this.observedChunks.get(key);
        if (observedChunk != null) {
            observedChunk.addBlockChange(() -> new Vector3i(x, y, z));
            if (oldBlockState.getType() != newBlockState.getType()) {
                observedChunk.removeBlockAction(new Vector3i(x, y, z));
            }
        }
    }

    @Override
    public void onBlockAction(int x, int y, int z, BlockType blockType, BlockAction blockAction) {
        final long key = LanternChunk.key(x >> 4, z >> 4);
        final ObservedChunk observedChunk = this.observedChunks.get(key);
        if (observedChunk != null) {
            observedChunk.addBlockAction(new Vector3i(x, y, z), blockType, blockAction);
        }
    }

    void addObserver(Vector2i coords, LanternPlayer observer) {
        final long key = LanternChunk.key(coords.getX(), coords.getY());
        final ObservedChunk observedChunk = this.observedChunks.computeIfAbsent(key, key1 -> new ObservedChunk(coords));
        observedChunk.addObserver(observer);
    }

    void removeObserver(Vector2i coords, LanternPlayer observer, boolean updateClient) {
        final long key = LanternChunk.key(coords.getX(), coords.getY());
        final ObservedChunk observedChunk = this.observedChunks.get(key);
        if (observedChunk != null) {
            observedChunk.removeObserver(observer, updateClient);
            if (observedChunk.observers.isEmpty()) {
                this.observedChunks.remove(key);
            }
        }
    }

    private static final VariableValueArray EMPTY_SECTION_TYPES = new VariableValueArray(4, CHUNK_SECTION_VOLUME);
    /*
    private static final byte[] EMPTY_SECTION_LIGHT = new byte[CHUNK_SECTION_SIZE];
    private static final byte[] EMPTY_SECTION_SKY_LIGHT = new byte[CHUNK_SECTION_SIZE];

    static {
        Arrays.fill(EMPTY_SECTION_SKY_LIGHT, (byte) 255);
    }*/

    private static final MessagePlayOutChunkData.Section EMPTY_SECTION = new MessagePlayOutChunkData.Section(
            EMPTY_SECTION_TYPES, new int[1], 0, new Short2ObjectOpenHashMap<>());

    private static Map<Vector3i, LanternBlockEntity> getMappedTileEntities(LanternChunk chunk) {
        return chunk.getBlockEntities().stream().collect(Collectors.toMap(
                blockEntity -> blockEntity.getLocation().getBlockPosition(),
                blockEntity -> (LanternBlockEntity) blockEntity));
    }

    private static int getStateId(Chunk chunk, Vector3i coords) {
        final BlockState blockState = chunk.getBlock(coords);
        return BlockRegistryModule.get().getStateInternalId(blockState);
    }

    private class ObservedChunk {

        private final class QueuedBlockAction {

            private final BlockAction blockAction;
            private final MessagePlayOutBlockAction blockActionData;

            private QueuedBlockAction(BlockAction blockAction, MessagePlayOutBlockAction blockActionData) {
                this.blockAction = blockAction;
                this.blockActionData = blockActionData;
            }
        }

        /**
         * The coordinates of this chunk.
         */
        private final Vector2i coords;

        /**
         * All the observers of the chunk.
         */
        private final Set<LanternPlayer> observers = Sets.newConcurrentHashSet();

        /**
         * All the observers that already know this chunk on the client.
         */
        private final Set<LanternPlayer> clientObservers = Sets.newConcurrentHashSet();

        /**
         * All the block changes that should be send to the observers.
         */
        private final Queue<Vector3i> dirtyBlocks = new ConcurrentLinkedQueue<>();

        /**
         * All the block events that should be send to the observers.
         */
        private final Map<Vector3i, QueuedBlockAction> addedBlockActions = new ConcurrentHashMap<>();
        private final Map<Vector3i, QueuedBlockAction> activeBlockActions = new ConcurrentHashMap<>();

        /**
         * Whether all the chunk sections are modified or whether the biomes are modified
         * and the client should be updated.
         *
         * TODO: Add a listener to change this state for biomes
         */
        private volatile boolean dirtyChunk;

        ObservedChunk(Vector2i coords) {
            this.coords = coords;
        }

        void removeBlockAction(Vector3i coords) {
            this.addedBlockActions.remove(coords);
            this.activeBlockActions.remove(coords);
        }

        void addBlockAction(Vector3i coords, BlockType blockType, BlockAction blockAction) {
            final BlockAction.Type type = blockAction.type();
            // Don't store single events if there are no observers
            if (type == BlockAction.Type.SINGLE &&
                    this.observers.isEmpty() && this.clientObservers.isEmpty()) {
                return;
            }
            // Create the message
            final MessagePlayOutBlockAction blockActionData = new MessagePlayOutBlockAction(coords,
                    BlockRegistryModule.get().getStateInternalId(blockType.getDefaultState()));
            blockAction.fill(blockActionData);
            this.addedBlockActions.put(coords, new QueuedBlockAction(blockAction, blockActionData));
        }

        void addBlockChange(Supplier<Vector3i> coords) {
            // There is not need to track the changes if no one wants to see them
            // dirtyBiomes will force the chunk to be completely resend
            if (!this.dirtyChunk && !this.clientObservers.isEmpty()) {
                this.dirtyBlocks.add(coords.get());
            }
        }

        void streamChanges() {
            final LanternChunk chunk = world.getChunkManager().getChunkIfLoaded(this.coords);
            if (chunk == null || this.clientObservers.isEmpty()) {
                return;
            }

            if (this.dirtyChunk) {
                final List<Message> messages = createLoadChunkMessages(chunk, ALL_SECTIONS_BIT_MASK, true);
                this.clientObservers.forEach(player -> player.getConnection().send(messages));
                this.dirtyChunk = false;
                this.dirtyBlocks.clear();
                return;
            }

            final Map<Vector3i, LanternBlockEntity> mappedTileEntities;

            final List<Message> messages;
            if (!this.dirtyBlocks.isEmpty()) {
                // All the changes per coordinate
                final Set<Vector3i> changes = new HashSet<>();

                // All the section which contain a block change
                int dirtySections = 0;

                // Get all the changes
                Vector3i dirtyBlock;
                while ((dirtyBlock = this.dirtyBlocks.poll()) != null) {
                    dirtySections |= 1 << (dirtyBlock.getY() >> 4);
                    changes.add(dirtyBlock);
                }

                final int clumpingThreshold = world.getProperties().getConfig().getChunkClumpingThreshold();
                if (changes.size() >= clumpingThreshold) {
                    messages = createLoadChunkMessages(chunk, dirtySections, false);
                    this.clientObservers.forEach(player -> player.getConnection().send(messages));
                    mappedTileEntities = Collections.emptyMap();
                } else {
                    messages = new ArrayList<>();
                    if (changes.size() > 1) {
                        messages.add(new MessagePlayOutMultiBlockChange(
                                this.coords.getX(), this.coords.getY(), changes.stream().map(coords -> {
                            final int x = coords.getX() & 0xf;
                            final int z = coords.getZ() & 0xf;
                            return new MessagePlayOutBlockChange(new Vector3i(x, coords.getY(), z), getStateId(chunk, coords));
                        }).collect(Collectors.toList())));
                    } else {
                        dirtyBlock = changes.iterator().next();
                        messages.add(new MessagePlayOutBlockChange(dirtyBlock, getStateId(chunk, dirtyBlock)));
                    }
                    final BlockEntityUpdateContext initContext = new BlockEntityUpdateContext(messages);
                    mappedTileEntities = getMappedTileEntities(chunk);
                    for (Vector3i pos : changes) {
                        final LanternBlockEntity tileEntity = mappedTileEntities.remove(pos);
                        if (tileEntity != null) {
                            final AbstractBlockEntityProtocol protocol = tileEntity.getProtocol();
                            if (protocol != null) {
                                BlockEntityProtocolHelper.init(protocol, initContext);
                            }
                        }
                    }
                }
            } else {
                messages = new ArrayList<>();
                mappedTileEntities = getMappedTileEntities(chunk);
            }

            final BlockEntityUpdateContext updateContext = new BlockEntityUpdateContext(messages);
            for (Map.Entry<Vector3i, LanternBlockEntity> entry : mappedTileEntities.entrySet()) {
                final AbstractBlockEntityProtocol protocol = entry.getValue().getProtocol();
                if (protocol != null) {
                    BlockEntityProtocolHelper.update(protocol, updateContext, 1);
                }
            }

            if (!this.addedBlockActions.isEmpty()) {
                for (Map.Entry<Vector3i, QueuedBlockAction> entry : this.addedBlockActions.entrySet()) {
                    final QueuedBlockAction blockAction = entry.getValue();
                    messages.add(blockAction.blockActionData);
                    if (blockAction.blockAction.type() == BlockAction.Type.CONTINUOUS) {
                        this.activeBlockActions.put(entry.getKey(), blockAction);
                    } else {
                        this.activeBlockActions.remove(entry.getKey());
                    }
                }
                this.addedBlockActions.clear();
            }

            this.clientObservers.forEach(player -> player.getConnection().send(messages));
        }

        private List<Message> createChunkLoadMessages(LanternChunk chunk) {
            final List<Message> messages = createLoadChunkMessages(chunk, ALL_SECTIONS_BIT_MASK, true);
            if (!this.activeBlockActions.isEmpty()) {
                this.activeBlockActions.values().forEach(queuedBlockAction -> messages.add(queuedBlockAction.blockActionData));
            }
            return messages;
        }

        /**
         * Sends a chunk load message to all the observers
         * of this chunk.
         *
         * @param chunk The chunk
         */
        void streamChunkLoad(LanternChunk chunk) {
            List<Message> messages = null;
            for (LanternPlayer observer : this.observers) {
                if (this.clientObservers.add(observer)) {
                    if (messages == null) {
                        messages = createChunkLoadMessages(chunk);
                    }
                    observer.getConnection().send(messages);
                }
            }
            // TODO: Also send blockEntity entities
        }

        void streamChunkUnload(LanternChunk chunk) {
            Message message = null;
            for (LanternPlayer observer : this.observers) {
                if (this.clientObservers.remove(observer)) {
                    if (message == null) {
                        message = new MessagePlayOutUnloadChunk(this.coords.getX(), this.coords.getY());
                    }
                    observer.getConnection().send(message);
                }
            }
        }

        private List<Message> createLoadChunkMessages(LanternChunk chunk, int sectionsBitMask, boolean biomes) {
            final LanternChunk.ChunkSectionSnapshot[] sections = chunk.getSectionSnapshots(sectionsBitMask);
            final MessagePlayOutChunkData.Section[] msgSections = new MessagePlayOutChunkData.Section[sections.length];

            final List<Message> messages = new ArrayList<>();

            for (int i = 0; i < sections.length; i++) {
                if (sections[i] != null) {
                    final LanternChunk.ChunkSectionSnapshot section = sections[i];
                    final ChunkBlockStateArray blockStates = section.blockStates;
                    final Palette<BlockState> palette = blockStates.getPalette();
                    int[] intPalette = null;
                    if (palette.getType() != PaletteType.GLOBAL) {
                        intPalette = palette.getEntries().stream()
                                .mapToInt(state -> BlockRegistryModule.get().getStateInternalId(state))
                                .toArray();
                    }

                    final Short2ObjectMap<DataView> tileEntityInitData = new Short2ObjectOpenHashMap<>();
                    final BlockEntityChunkInitContext initContext = new BlockEntityChunkInitContext(
                            messages, tileEntityInitData, chunk.getX(), chunk.getZ(), i);
                    // Serialize the blockEntity entities
                    for (Short2ObjectMap.Entry<LanternBlockEntity> tileEntityEntry : section.blockEntities.short2ObjectEntrySet()) {
                        if (!tileEntityEntry.getValue().isValid()) { // Ignore invalid blockEntity entities
                            continue;
                        }
                        initContext.currentIndex = tileEntityEntry.getShortKey();
                        final AbstractBlockEntityProtocol protocol = tileEntityEntry.getValue().getProtocol();
                        if (protocol != null) {
                            BlockEntityProtocolHelper.init(protocol, initContext);
                        }
                    }
                    msgSections[i] = new MessagePlayOutChunkData.Section(
                            blockStates.getBacking(), intPalette, section.nonAirBlockCount, tileEntityInitData);
                // The insert entry setting is used to send a "null" chunk
                // after the chunk is already send to the client
                // TODO: Better way to do this?
                } else if (!biomes && ((1 << i) & sectionsBitMask) != 0) {
                    msgSections[i] = EMPTY_SECTION;
                }
            }

            int[] biomesArray = null;
            if (biomes) {
                biomesArray = chunk.getBiomes();
                // TODO: Only allow non-custom biome types to be send and maybe the ones supported by forge mods?
            }

            messages.add(0, new MessagePlayOutChunkData(this.coords.getX(), this.coords.getY(), msgSections, biomesArray));
            return messages;
        }

        /**
         * Removes the observer from this chunk.
         *
         * @param observer the observer
         * @param updateClient whether the client should be notified, this may be {@code false} when
         *                     the player is respawning on the client in a different dimension, causing
         *                     all the chunks to be unloaded, making it unneeded for this method to do it
         *                     again.
         */
        public void removeObserver(LanternPlayer observer, boolean updateClient) {
            if (this.observers.remove(observer) &&
                    this.clientObservers.remove(observer) && updateClient) {
                observer.getConnection().send(new MessagePlayOutUnloadChunk(this.coords.getX(), this.coords.getY()));
            }
            // Clear the dirty states, since no one will still want to see them
            if (this.clientObservers.isEmpty()) {
                this.dirtyBlocks.clear();
                this.dirtyChunk = false;
            }
        }

        /**
         * Adds the observer to this chunk, this may trigger the chunk to
         * be loaded on the client if the chunk is already loaded.
         *
         * @param observer the observer
         */
        public void addObserver(LanternPlayer observer) {
            if (this.observers.add(observer)) {
                LanternChunk chunk = world.getChunkManager().getChunkIfLoaded(this.coords);
                // The chunk is already loaded, we can directly send the messages
                // to the player
                if (chunk != null) {
                    this.clientObservers.add(observer);
                    observer.getConnection().send(createChunkLoadMessages(chunk));
                }
                // Otherwise we will wait for the LoadChunkEvent to be called and
                // send the messages at that point
            }
        }
    }

    static class BlockEntityUpdateContext implements BlockEntityProtocolUpdateContext, BlockEntityChunkProtocolData {

        private final List<Message> messages;

        BlockEntityUpdateContext(List<Message> messages) {
            this.messages = messages;
        }

        @Override
        public Short2ObjectMap<DataView> getInitData() {
            throw new IllegalStateException();
        }

        @Override
        public List<Message> getMessages() {
            return this.messages;
        }

        @Override
        public void send(Message message) {
            this.messages.add(message);
        }
    }

    static class BlockEntityChunkInitContext extends BlockEntityUpdateContext {

        private final Short2ObjectMap<DataView> initData;
        private final int chunkX;
        private final int chunkZ;
        private final int section;

        int currentIndex;

        BlockEntityChunkInitContext(List<Message> messages, Short2ObjectMap<DataView> initData, int chunkX, int chunkZ, int section) {
            super(messages);
            this.initData = initData;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            this.section = section;
        }

        @Override
        public void send(Message message) {
            if (message instanceof MessagePlayOutBlockEntity) {
                final MessagePlayOutBlockEntity tileUpdateMessage = (MessagePlayOutBlockEntity) message;
                final Vector3i pos = tileUpdateMessage.getPosition();
                final int chunkX = pos.getX() >> 4;
                final int chunkY = pos.getY() >> 4;
                final int chunkZ = pos.getZ() >> 4;
                if (chunkX == this.chunkX && chunkY == this.section && chunkZ == this.chunkZ) {
                    final short index = (short) LanternChunk.ChunkSection.index(
                            pos.getX() & 0xf, pos.getY() & 0xf, pos.getZ() & 0xf);
                    if (index == this.currentIndex &&
                            this.initData.putIfAbsent(index, tileUpdateMessage.getTileData()) == null) {
                        return;
                    }
                }
            }
            super.send(message);
        }

        @Override
        public Short2ObjectMap<DataView> getInitData() {
            return this.initData;
        }
    }
}
