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
package org.lanternpowered.server.network.item;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.lanternpowered.server.game.registry.InternalIDRegistries;
import org.lanternpowered.server.item.LanternItemType;
import org.lanternpowered.server.item.appearance.ItemAppearance;
import org.spongepowered.api.item.ItemType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public final class NetworkItemTypeRegistry {

    static final Object2IntMap<String> normalToNetworkId;
    static final Int2ObjectMap<String> networkIdToNormal;
    static final Int2ObjectMap<ItemType> networkIdToItemType = new Int2ObjectOpenHashMap<>();
    static final Int2ObjectMap<ItemType> internalIdToItemType = new Int2ObjectOpenHashMap<>();
    static final Map<ItemType, int[]> itemTypeToInternalAndNetworkId = new HashMap<>();
    static final Map<String, String> serverModdedToClientId = new HashMap<>();

    private static int internalIdCounter = 0;

    static {
        final Gson gson = new Gson();

        final Object2IntMap<String> normalToNetwork = new Object2IntOpenHashMap<>();
        normalToNetwork.defaultReturnValue(-1);
        final Int2ObjectMap<String> networkToNormal = new Int2ObjectOpenHashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(InternalIDRegistries.class
                .getResourceAsStream("/internal/registries/item.json")))) {
            final JsonArray jsonArray = gson.fromJson(reader, JsonArray.class);
            for (int i = 0; i < jsonArray.size(); i++) {
                final JsonElement element = jsonArray.get(i);
                final String id = element.isJsonPrimitive() ? element.getAsString() : element.getAsJsonObject().get("id").getAsString();
                normalToNetwork.put(id, i);
                networkToNormal.put(i, id);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        normalToNetworkId = Object2IntMaps.unmodifiable(normalToNetwork);
        networkIdToNormal = Int2ObjectMaps.unmodifiable(networkToNormal);
    }

    public static void register(ItemType itemType) {
        final LanternItemType itemType1 = (LanternItemType) itemType;
        if (internalIdToItemType.containsValue(itemType1)) {
            return;
        }
        final String serverId = itemType.getKey().toString();
        final String id = itemType1.getAppearance().map(ItemAppearance::getItemTypeId).orElse(serverId);
        serverModdedToClientId.put(serverId, id);
        final int networkId = normalToNetworkId.getInt(id);
        checkArgument(networkId != -1, "No network id was for the vanilla/modded item type id: " + id);
        final int internalId = internalIdCounter++;
        itemTypeToInternalAndNetworkId.put(itemType, new int[] { internalId, networkId });
        internalIdToItemType.put(internalId, itemType);
        if (!itemType1.getAppearance().isPresent()) {
            networkIdToItemType.put(networkId, itemType1);
        }
    }
}
