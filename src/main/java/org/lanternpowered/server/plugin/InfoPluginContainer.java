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
package org.lanternpowered.server.plugin;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.spongepowered.plugin.meta.McModInfo;
import org.spongepowered.plugin.meta.PluginMetadata;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public class InfoPluginContainer extends AbstractPluginContainer {

    private final ImmutableList<String> authors;
    @Nullable private final String description;
    @Nullable private final String url;

    public InfoPluginContainer(String id, PluginMetadata pluginMetadata) {
        super(id, pluginMetadata.getName(), pluginMetadata.getVersion());
        this.authors = ImmutableList.copyOf(pluginMetadata.getAuthors());
        this.description = pluginMetadata.getDescription();
        this.url = pluginMetadata.getUrl();
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.ofNullable(this.description);
    }

    @Override
    public Optional<String> getUrl() {
        return Optional.ofNullable(this.url);
    }

    @Override
    public List<String> getAuthors() {
        return this.authors;
    }

    private static final Gson gson = new Gson();

    /**
     * Reads a {code plugin.info} file as {@link PluginMetadata} from the
     * specified plugin id and resource {@link URL}.
     *
     * @param pluginId The plugin id
     * @param url The resource url
     * @return The parsed plugin metadata
     */
    public static PluginMetadata readPluginInfo(String pluginId, URL url) throws IOException {
        final String json = Resources.toString(url, StandardCharsets.UTF_8);
        JsonElement element = gson.fromJson(json, JsonElement.class);
        final JsonObject object;
        if (element.isJsonArray()) {
            // Just get the first element from the array
            object = element.getAsJsonArray().get(0).getAsJsonObject();
        } else {
            // The info is directly serialized as an object, get this object
            // and put it into an array, this is what the McModInfo library
            // supports
            object = element.getAsJsonObject();
            final JsonArray array = new JsonArray();
            array.add(object);
            element = array;
        }
        // Add the 'modid', it is required for the McModInfo
        object.addProperty("modid", pluginId);
        // Convert the element back to json and parse it with the McModInfo library
        final StringReader reader = new StringReader(gson.toJson(element));
        return McModInfo.builder().build().read(reader).get(0);
    }
}
