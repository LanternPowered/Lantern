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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.spongepowered.api.block.BlockType;

public final class InternalIDRegistries {

    /**
     * A map with all the network ids for the {@link BlockType}s. Multiple
     * ids may be reserved depending on the amount of states of the block type.
     */
    public static final Object2IntMap<String> BLOCK_STATE_START_IDS;
    public static final Object2IntMap<String> BLOCK_TYPE_IDS;
    public static final int BLOCK_STATES_ASSIGNED;

    static {
        final Object2IntMap<String> blockStateStartIds = new Object2IntOpenHashMap<>();
        blockStateStartIds.defaultReturnValue(-1);
        final Object2IntMap<String> blockTypeIds = new Object2IntOpenHashMap<>();
        blockTypeIds.defaultReturnValue(-1);
        int blockStates = 0;
        int blockTypes = 0;
        final JsonArray jsonArray = InternalRegistries.INSTANCE.readJsonArray("block");
        for (int i = 0; i < jsonArray.size(); i++) {
            final JsonElement element = jsonArray.get(i);
            String id;
            int states = 1;
            if (element.isJsonPrimitive()) {
                id = element.getAsString();
            } else {
                final JsonObject object = element.getAsJsonObject();
                id = object.get("id").getAsString();
                if (object.has("states")) {
                    states = object.get("states").getAsInt();
                }
            }
            blockStateStartIds.put(id, blockStates);
            blockStates += states;
            blockTypeIds.put(id, blockTypes++);
        }
        BLOCK_STATES_ASSIGNED = blockStates;
        BLOCK_STATE_START_IDS = Object2IntMaps.unmodifiable(blockStateStartIds);
        BLOCK_TYPE_IDS = Object2IntMaps.unmodifiable(blockTypeIds);
    }
}
