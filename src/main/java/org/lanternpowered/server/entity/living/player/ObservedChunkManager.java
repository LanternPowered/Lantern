/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
import static org.lanternpowered.server.world.chunk.LanternChunk.CHUNK_SECTION_SIZE;
import static org.lanternpowered.server.world.chunk.LanternChunk.CHUNK_SECTION_VOLUME;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import gnu.trove.map.TShortShortMap;
import gnu.trove.map.hash.TShortShortHashMap;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBlockChange;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutChunkData;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutMultiBlockChange;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutUnloadChunk;
import org.lanternpowered.server.util.VariableValueArray;
import org.lanternpowered.server.world.LanternWorld;
import org.lanternpowered.server.world.chunk.LanternChunk;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.world.UnloadWorldEvent;
import org.spongepowered.api.event.world.chunk.LoadChunkEvent;
import org.spongepowered.api.event.world.chunk.PopulateChunkEvent;
import org.spongepowered.api.world.World;

import java.util.Arrays;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class ObservedChunkManager {

    /**
     * The {@link World} attached to the observed chunk manager.
     */
    private final LanternWorld world;

    /**
     * All the chunks that are being observed.
     */
    private final Map<Vector2i, ObservedChunk> observedChunks = Maps.newConcurrentMap();

    public ObservedChunkManager(LanternWorld world) {
        Sponge.getEventManager().registerListeners(Lantern.getMinecraftPlugin(), new Listeners());
        this.world = world;
    }

    public void pulse() {
        this.observedChunks.values().forEach(ObservedChunkManager.ObservedChunk::streamChanges);
    }

    public class Listeners {

        @Listener(order = Order.POST)
        public void onWorldUnload(UnloadWorldEvent event) {
            if (event.getTargetWorld() == world) {
                Sponge.getEventManager().unregisterListeners(this);
            }
        }

        @Listener(order = Order.POST)
        public void onChunkLoad(LoadChunkEvent event) {
            final LanternChunk chunk = (LanternChunk) event.getTargetChunk();
            final ObservedChunk observedChunk = observedChunks.get(chunk.getCoords());
            if (observedChunk != null) {
                observedChunk.streamChunk(chunk);
            }
        }

        @Listener(order = Order.POST)
        public void onBlockChange(ChangeBlockEvent event) {
            for (Transaction<BlockSnapshot> change : event.getTransactions()) {
                final Vector3i blockPos = change.getFinal().getPosition();
                final ObservedChunk observedChunk = observedChunks.get(new Vector2i(blockPos.getX() >> 4, blockPos.getZ() >> 4));
                if (observedChunk != null) {
                    observedChunk.addBlockChange(() -> new Vector3i(blockPos.getX() & 0xf, blockPos.getY(), blockPos.getZ()));
                }
            }
        }

        @Listener(order = Order.POST)
        public void onChunkPopulate(PopulateChunkEvent.Post event) {
            if (event.getAppliedPopulators().isEmpty()) {
                return;
            }
            final LanternChunk chunk = (LanternChunk) event.getTargetChunk();
            final ObservedChunk observedChunk = observedChunks.get(chunk.getCoords());
            if (observedChunk != null) {
                observedChunk.dirtyChunk = true;
            }
        }
    }

    public void addObserver(Vector2i coords, LanternPlayer observer) {
        final ObservedChunk observedChunk = this.observedChunks.computeIfAbsent(coords, ObservedChunk::new);
        observedChunk.addObserver(observer);
    }

    public void removeObserver(Vector2i coords, LanternPlayer observer, boolean updateClient) {
        final ObservedChunk observedChunk = this.observedChunks.get(coords);
        if (observedChunk != null) {
            observedChunk.removeObserver(observer, updateClient);
        }
    }

    private static final VariableValueArray EMPTY_SECTION_TYPES = new VariableValueArray(4, CHUNK_SECTION_VOLUME);
    private static final byte[] EMPTY_SECTION_LIGHT = new byte[CHUNK_SECTION_SIZE];
    private static final byte[] EMPTY_SECTION_SKY_LIGHT = new byte[CHUNK_SECTION_SIZE];

    static {
        Arrays.fill(EMPTY_SECTION_SKY_LIGHT, (byte) 255);
    }

    private static final MessagePlayOutChunkData.Section EMPTY_SECTION_SKYLIGHT = new MessagePlayOutChunkData.Section(
            EMPTY_SECTION_TYPES, new int[1], EMPTY_SECTION_LIGHT, EMPTY_SECTION_SKY_LIGHT);

    private static final MessagePlayOutChunkData.Section EMPTY_SECTION = new MessagePlayOutChunkData.Section(
            EMPTY_SECTION_TYPES, new int[1], EMPTY_SECTION_LIGHT, null);

    public class ObservedChunk {

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
         * Whether all the chunk sections are modified or whether the biomes are modified
         * and the client should be updated.
         *
         * TODO: Add a listener to change this state for biomes
         */
        private volatile boolean dirtyChunk;

        public ObservedChunk(Vector2i coords) {
            this.coords = coords;
        }

        void addBlockChange(Supplier<Vector3i> coords) {
            // There is not need to track the changes if no one wants to see them
            // dirtyBiomes will force the chunk to be completely resend
            if (!this.dirtyChunk && !this.observers.isEmpty() && this.clientObservers.isEmpty()) {
                this.dirtyBlocks.add(coords.get());
            }
        }

        void streamChanges() {
            LanternChunk chunk = world.getChunkManager().getChunkIfLoaded(this.coords);
            if (chunk == null || this.clientObservers.isEmpty()) {
                return;
            }

            if (this.dirtyChunk) {
                MessagePlayOutChunkData message = this.createLoadChunkMessage(chunk, ALL_SECTIONS_BIT_MASK, true);
                this.clientObservers.forEach(player -> player.getConnection().send(message));
                this.dirtyChunk = false;
                this.dirtyBlocks.clear();
                return;
            }

            if (!this.dirtyBlocks.isEmpty()) {
                // All the changes per coordinate
                Set<Vector3i> changes = Sets.newHashSet();

                // All the section which contain a block change
                int dirtySections = 0;

                // Get all the changes
                Vector3i dirtyBlock;
                while ((dirtyBlock = this.dirtyBlocks.poll()) != null) {
                    dirtySections |= 1 << (dirtyBlock.getY() >> 4);
                    changes.add(dirtyBlock);
                }

                int clumpingThreshold = world.getProperties().getConfig().getChunkClumpingThreshold();

                if (changes.size() >= clumpingThreshold) {
                    MessagePlayOutChunkData message = this.createLoadChunkMessage(chunk, dirtySections, false);
                    this.clientObservers.forEach(player -> player.getConnection().send(message));
                } else if (changes.size() > 1) {
                    MessagePlayOutMultiBlockChange message = new MessagePlayOutMultiBlockChange(
                            this.coords.getX(), this.coords.getY(), changes.stream().map(
                            c -> new MessagePlayOutBlockChange(c, chunk.getType(c))).collect(Collectors.toList()));
                    this.clientObservers.forEach(player -> player.getConnection().send(message));
                } else {
                    dirtyBlock = changes.iterator().next();
                    MessagePlayOutBlockChange message = new MessagePlayOutBlockChange(dirtyBlock, chunk.getType(dirtyBlock));
                    this.clientObservers.forEach(player -> player.getConnection().send(message));
                }
                // TODO: Also update tile entities
            }
        }

        /**
         * Sends a chunk load message to all the observers
         * of this chunk.
         *
         * @param chunk the chunk
         */
        void streamChunk(LanternChunk chunk) {
            MessagePlayOutChunkData message = null;
            for (LanternPlayer observer : this.observers) {
                if (this.clientObservers.add(observer)) {
                    if (message == null) {
                        message = this.createLoadChunkMessage(chunk, ALL_SECTIONS_BIT_MASK, true);
                    }
                    observer.getConnection().send(message);
                }
            }
            // TODO: Also send tile entities
        }

        private MessagePlayOutChunkData createLoadChunkMessage(LanternChunk chunk, int sectionsBitMask, boolean biomes) {
            // Whether we should send sky light
            boolean skyLight = world.getDimension().hasSky();

            LanternChunk.ChunkSectionSnapshot[] sections = chunk.getSectionSnapshots(skyLight, sectionsBitMask);
            MessagePlayOutChunkData.Section[] msgSections = new MessagePlayOutChunkData.Section[sections.length];

            for (int i = 0; i < sections.length; i++) {
                if (sections[i] != null) {
                    final LanternChunk.ChunkSectionSnapshot section = sections[i];
                    // The size of the palette
                    int paletteSize = section.typesCountMap.size();
                    // The amount of bits for every block state
                    int bitsPerValue = Integer.highestOneBit(paletteSize);
                    // The palette that will be send to the client
                    int[] palette;
                    // The lookup for global to local palette id
                    TShortShortMap globalToLocalPalette;
                    // TODO: How to fix this?
                    // There seems to be a weird issue, some blocks are not rendered
                    // on the client (bedrock with the flat generator) and it cannot
                    // be placed in creative
                    if (false && bitsPerValue <= 8) {
                        // The vanilla client/server will not go lower then 4 bits
                        if (bitsPerValue < 4) {
                            bitsPerValue = 4;
                        }
                        globalToLocalPalette = new TShortShortHashMap(paletteSize);
                        palette = new int[paletteSize];
                        short[] currentId = { 0 };
                        section.typesCountMap.forEachEntry((type, count) -> {
                            short id = currentId[0]++;
                            globalToLocalPalette.put(type, id);
                            palette[id] = type;
                            return true;
                        });
                    } else {
                        // int statesCount = Registries.getBlockRegistry().getBlockStatesCount();
                        // bitsPerValue = Integer.highestOneBit(statesCount);
                        // The value should be the amount of bits per value of
                        // the CLIENT palette, it will otherwise not work.
                        // This is sadly enough hardcoded in the client
                        bitsPerValue = 13;
                        globalToLocalPalette = null;
                        palette = null;
                    }
                    short[] types = section.types;
                    VariableValueArray array = new VariableValueArray(bitsPerValue, types.length);
                    if (globalToLocalPalette != null) {
                        for (int j = 0; j < types.length; j++) {
                            array.set(j, globalToLocalPalette.get(types[j]));
                        }
                    } else {
                        for (int j = 0; j < types.length; j++) {
                            array.set(j, types[j]);
                        }
                    }
                    msgSections[i] = new MessagePlayOutChunkData.Section(array, palette, section.lightFromBlock, section.lightFromSky);
                // The insert entry setting is used to send a "null" chunk
                // after the chunk is already send to the client
                // TODO: Better way to do this?
                } else if (!biomes && ((1 << i) & sectionsBitMask) != 0) {
                    msgSections[i] = skyLight ? EMPTY_SECTION_SKYLIGHT : EMPTY_SECTION;
                }
            }

            byte[] biomesArray = null;
            if (biomes) {
                short[] biomesArray0 = chunk.getBiomes();
                biomesArray = new byte[biomesArray0.length];
                for (int i = 0; i < biomesArray0.length; i++) {
                    // TODO: Only allow non-custom biome types to be send and maybe the ones supported by forge mods?
                    biomesArray[i] = (byte) (biomesArray0[i] & 0xff);
                }
            }

            return new MessagePlayOutChunkData(this.coords.getX(), this.coords.getY(), skyLight, msgSections, biomesArray);
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
                    observer.getConnection().send(this.createLoadChunkMessage(chunk, ALL_SECTIONS_BIT_MASK, true));
                }
                // Otherwise we will wait for the LoadChunkEvent to be called and
                // send the messages at that point
            }
        }
    }
}
