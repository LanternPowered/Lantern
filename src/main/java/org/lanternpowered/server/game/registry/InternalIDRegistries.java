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
package org.lanternpowered.server.game.registry;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.spongepowered.api.block.BlockType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public final class InternalIDRegistries {

    /**
     * A map with all the network ids for the {@link BlockType}s. Multiple
     * ids may be reserved depending on the amount of states of the block type.
     */
    public static final Object2IntMap<String> BLOCK_TYPE_IDS;
    public static final int BLOCK_STATES_ASSIGNED;

    static {
        final Gson gson = new Gson();

        final Object2IntMap<String> blockTypeIds = new Object2IntOpenHashMap<>();
        blockTypeIds.defaultReturnValue(-1);
        int blockStates = 0;
        try (BufferedReader reader = openReader("internal/block/block_type_indexes.json")) {
            final JsonArray jsonArray = gson.fromJson(reader, JsonArray.class);
            for (int i = 0; i < jsonArray.size(); i++) {
                String entry = jsonArray.get(i).getAsString();
                int states = 1;
                final int index = entry.indexOf('*');
                if (index != -1) {
                    states = Integer.parseInt(entry.substring(index + 1));
                    entry = entry.substring(0, index);
                }
                blockTypeIds.put(entry, blockStates);
                blockStates += states;
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        BLOCK_STATES_ASSIGNED = blockStates;
        BLOCK_TYPE_IDS = Object2IntMaps.unmodifiable(blockTypeIds);
    }

    private static BufferedReader openReader(String path) {
        return new BufferedReader(new InputStreamReader(InternalIDRegistries.class.getResourceAsStream("/" + path)));
    }
}
