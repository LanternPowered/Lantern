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
package org.lanternpowered.server.inventory.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.spongepowered.api.item.ItemType;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

final class ClientItemStackSizes {

    private static final Object2IntMap<String> STACK_SIZES = new Object2IntOpenHashMap<>();

    static int getOriginalMaxSize(ItemType itemType) {
        final int size = STACK_SIZES.getInt(itemType.getKey().toString());
        return size == 0 ? itemType.getMaxStackQuantity() : size;
    }

    static {
        final Gson gson = new Gson();

        final InputStream is = ClientItemStackSizes.class.getResourceAsStream("/internal/registries/item.json");
        final JsonObject json = gson.fromJson(new BufferedReader(new InputStreamReader(is)), JsonObject.class);

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            final JsonElement element = entry.getValue();
            final String id;
            int maxStackSize = 64;
            if (element.isJsonPrimitive()) {
                id = element.getAsString();
            } else {
                final JsonObject object = element.getAsJsonObject();
                id = object.get("id").getAsString();
                if (object.has("max_stack_size")) {
                    maxStackSize = object.get("max_stack_size").getAsInt();
                }
            }
            STACK_SIZES.put(id, maxStackSize);
        }
    }
}
