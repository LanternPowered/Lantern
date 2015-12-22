/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
package org.lanternpowered.server.world.chunk;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.lanternpowered.server.data.io.nbt.NbtDataContainerInputStream;
import org.lanternpowered.server.data.io.nbt.NbtDataContainerOutputStream;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.world.chunk.LanternEntityLoadingTicket.EntityReference;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.ChunkTicketManager.EntityLoadingTicket;
import org.spongepowered.api.world.ChunkTicketManager.PlayerLoadingTicket;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * This class can be used to serialize/deserialize loading tickets,
 * they are saved in the same format as forge to allow maximum
 * compatibility.
 */
public class LanternLoadingTicketIO {

    private static final String TICKETS_FILE = "forcedchunks.dat";

    private static final DataQuery HOLDER_LIST = DataQuery.of("TicketList");
    private static final DataQuery HOLDER_NAME = DataQuery.of("Owner");
    private static final DataQuery TICKETS = DataQuery.of("Tickets");
    private static final DataQuery TICKET_TYPE = DataQuery.of("Type");
    private static final DataQuery CHUNK_LIST_DEPTH = DataQuery.of("ChunkListDepth");
    // Lantern property
    private static final DataQuery CHUNK_NUMBER = DataQuery.of("ChunksNum");
    private static final DataQuery CHUNK_X = DataQuery.of("chunkX");
    private static final DataQuery CHUNK_Z = DataQuery.of("chunkZ");
    private static final DataQuery MOD_ID = DataQuery.of("ModId");
    private static final DataQuery MOD_DATA = DataQuery.of("ModData");
    private static final DataQuery PLAYER_UUID = DataQuery.of("Player");
    private static final DataQuery ENTITY_UUID_MOST = DataQuery.of("PersistentIDMSB");
    private static final DataQuery ENTITY_UUID_LEAST = DataQuery.of("PersistentIDLSB");

    private static final byte TYPE_NORMAL = 0;
    private static final byte TYPE_ENTITY = 1;

    static void save(File worldFolder, Set<LanternLoadingTicket> tickets) throws IOException {
        File file = new File(worldFolder, TICKETS_FILE);
        if (!file.exists()) {
            file.createNewFile();
        }

        Multimap<String, LanternLoadingTicket> sortedByPlugin = HashMultimap.create();
        for (LanternLoadingTicket ticket : tickets) {
            sortedByPlugin.put(ticket.getPlugin(), ticket);
        }

        List<DataView> ticketHolders = Lists.newArrayList();
        for (Entry<String, Collection<LanternLoadingTicket>> entry : sortedByPlugin.asMap().entrySet()) {
            Collection<LanternLoadingTicket> tickets0 = entry.getValue();

            List<DataView> ticketEntries = Lists.newArrayList();
            for (LanternLoadingTicket ticket0 : tickets0) {
                DataContainer ticketData = new MemoryDataContainer();
                ticketData.set(TICKET_TYPE, ticket0 instanceof EntityLoadingTicket ?
                        TYPE_ENTITY : TYPE_NORMAL);
                int numChunks = ticket0.getNumChunks();
                // Store the list depth for backwards compatible or something,
                // the current forge version doesn't use it either
                ticketData.set(CHUNK_LIST_DEPTH, (byte) Math.min(numChunks, 127));
                // Storing the chunks number, this number is added by us
                ticketData.set(CHUNK_NUMBER, numChunks);
                if (ticket0 instanceof PlayerLoadingTicket) {
                    PlayerLoadingTicket ticket1 = (PlayerLoadingTicket) ticket0;
                    // This is a bit strange, since it already added,
                    // but if forge uses it...
                    ticketData.set(MOD_ID, entry.getKey());
                    ticketData.set(PLAYER_UUID, ticket1.getPlayerUniqueId().toString());
                }
                if (ticket0.extraData != null) {
                    ticketData.set(MOD_DATA, ticket0.extraData);
                }
                if (ticket0 instanceof EntityLoadingTicket) {
                    EntityLoadingTicket ticket1 = (EntityLoadingTicket) ticket0;
                    Entity entity = ticket1.getBoundEntity();
                    if (entity != null) {
                        Vector3i position = LanternGame.get().getServer().getChunkLayout().toChunk(
                                entity.getLocation().getBlockPosition()).get();
                        UUID uniqueId = entity.getUniqueId();
                        ticketData.set(CHUNK_X, position.getX());
                        ticketData.set(CHUNK_Z, position.getZ());
                        ticketData.set(ENTITY_UUID_MOST, uniqueId.getMostSignificantBits());
                        ticketData.set(ENTITY_UUID_LEAST, uniqueId.getLeastSignificantBits());
                    }
                }
                ticketEntries.add(ticketData);
            }

            ticketHolders.add(new MemoryDataContainer()
                    .set(HOLDER_NAME, entry.getKey())
                    .set(TICKETS, ticketEntries));
        }

        DataContainer dataContainer = new MemoryDataContainer()
                .set(HOLDER_LIST, ticketHolders);

        NbtDataContainerOutputStream os = new NbtDataContainerOutputStream(
                new DataOutputStream(new GZIPOutputStream(new FileOutputStream(file))));
        os.write(dataContainer);
        os.flush();
        os.close();
    }

    static Multimap<String, LanternLoadingTicket> load(File worldFolder, LanternChunkManager chunkManager, LanternChunkTicketManager service) throws IOException {
        Multimap<String, LanternLoadingTicket> tickets = HashMultimap.create();

        File file = new File(worldFolder, TICKETS_FILE);
        if (!file.exists()) {
            return tickets;
        }

        NbtDataContainerInputStream is = new NbtDataContainerInputStream(
                new DataInputStream(new GZIPInputStream(new FileInputStream(file))));
        DataContainer dataContainer = is.read();
        is.close();

        Set<String> callbacks = service.getCallbacks().keySet();

        List<DataView> ticketHolders = dataContainer.getViewList(HOLDER_LIST).get();
        for (DataView ticketHolder : ticketHolders) {
            String holderName = ticketHolder.getString(HOLDER_NAME).get();

            if (!LanternGame.get().getPluginManager().isLoaded(holderName)) {
                LanternGame.log().warn("Found chunk loading data for plugin {} which is currently not available or active"
                        + " - it will be removed from the world save", holderName);
                continue;
            }

            if (!callbacks.contains(holderName)) {
                LanternGame.log().warn("The plugin {} has registered persistent chunk loading data but doesn't seem"
                        + " to want to be called back with it - it will be removed from the world save", holderName);
                continue;
            }

            final int maxNumChunks = chunkManager.getMaxChunksForPluginTicket(holderName);

            List<DataView> ticketEntries = ticketHolder.getViewList(TICKETS).get();
            for (DataView ticketEntry : ticketEntries) {
                int type = ticketEntry.getInt(TICKET_TYPE).get();

                UUID playerUUID = null;
                if (ticketEntry.contains(PLAYER_UUID)) {
                    playerUUID = UUID.fromString(ticketEntry.getString(PLAYER_UUID).get());
                }

                int numChunks = maxNumChunks;
                if (ticketEntry.contains(CHUNK_NUMBER)) {
                    numChunks = ticketEntry.getInt(CHUNK_NUMBER).get();
                } else if (ticketEntry.contains(CHUNK_LIST_DEPTH)) {
                    numChunks = ticketEntry.getInt(CHUNK_LIST_DEPTH).get();
                }

                LanternLoadingTicket ticket;
                if (type == TYPE_NORMAL) {
                    if (playerUUID != null) {
                        ticket = new LanternPlayerLoadingTicket(holderName, chunkManager, playerUUID, maxNumChunks, numChunks);
                    } else {
                        ticket = new LanternLoadingTicket(holderName, chunkManager, maxNumChunks, numChunks);
                    }
                } else if (type == TYPE_ENTITY) {
                    LanternEntityLoadingTicket ticket0;
                    if (playerUUID != null) {
                        ticket0 = new LanternPlayerEntityLoadingTicket(holderName, chunkManager, playerUUID, maxNumChunks, numChunks);
                    } else {
                        ticket0 = new LanternEntityLoadingTicket(holderName, chunkManager, maxNumChunks, numChunks);
                    }
                    int chunkX = ticketEntry.getInt(CHUNK_X).get();
                    int chunkZ = ticketEntry.getInt(CHUNK_Z).get();
                    long uuidMost = ticketEntry.getLong(ENTITY_UUID_MOST).get();
                    long uuidLeast = ticketEntry.getLong(ENTITY_UUID_LEAST).get();
                    Vector2i chunkCoords = new Vector2i(chunkX, chunkZ);
                    UUID uuid = new UUID(uuidMost, uuidLeast);
                    ticket0.entityRef = new EntityReference(chunkCoords, uuid);
                    ticket = ticket0;
                } else {
                    LanternGame.log().warn("Unknown ticket entry type {} for {}, skipping...", type, holderName);
                    continue;
                }
                if (ticketEntry.contains(MOD_DATA)) {
                    ticket.extraData = ticketEntry.getView(MOD_DATA).get().copy();
                }
                tickets.put(holderName, ticket);
            }
        }

        return tickets;
    }
}
