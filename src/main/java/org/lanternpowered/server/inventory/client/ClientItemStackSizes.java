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
        final int size = STACK_SIZES.getInt(itemType.getId());
        return size == 0 ? itemType.getMaxStackQuantity() : size;
    }

    static {
        final Gson gson = new Gson();

        final InputStream is = ClientItemStackSizes.class.getResourceAsStream("/internal/max_stack_sizes.json");
        final JsonObject json = gson.fromJson(new BufferedReader(new InputStreamReader(is)), JsonObject.class);

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            STACK_SIZES.put(entry.getKey(), entry.getValue().getAsInt());
        }
    }

    /*
    public static void save() {
        final Gson gson = new Gson();
        final JsonObject jsonObject = new JsonObject();

        for (ItemType itemType : ItemRegistryModule.get().getAll()) {
            jsonObject.addProperty(itemType.getId(), itemType.getMaxStackQuantity());
        }

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("max_stack_sizes.json"))) {
            gson.toJson(jsonObject, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */
}
