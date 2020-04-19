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
package org.lanternpowered.server.asset.json;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.lanternpowered.launch.LanternClassLoader;
import org.lanternpowered.server.asset.AssetRepository;
import org.lanternpowered.server.asset.ClassLoaderAssetRepository;
import org.lanternpowered.server.asset.DirectoryAssetRepository;
import org.lanternpowered.server.asset.MultiAssetRepository;
import org.lanternpowered.server.asset.PacksAssetRepository;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.plugin.LanternPluginManager;
import org.lanternpowered.server.util.PathUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.function.Consumer;

public class AssetRepositoryJsonDeserializer implements JsonDeserializer<AssetRepository> {

    private final LanternPluginManager pluginManager;

    public AssetRepositoryJsonDeserializer(LanternPluginManager pluginManager) {
        checkNotNull(pluginManager, "pluginManager");
        this.pluginManager = pluginManager;
    }

    @Override
    public AssetRepository deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final MultiAssetRepository repository = new MultiAssetRepository();
        // The class loader asset repository will always be present,
        // this cannot be overridden, but the assets themselves can
        // be overridden like the minecraft resource pack system
        final ClassLoaderAssetRepository classLoaderAssetRepository = new ClassLoaderAssetRepository(this.pluginManager);
        repository.add(classLoaderAssetRepository);
        final LanternClassLoader classLoader = LanternClassLoader.get();
        final Consumer<URL> consumer = url -> {
            final Path path = PathUtils.toPath(url);
            if (Files.isDirectory(path) && Files.exists(path.resolve("data-packs.info"))) {
                Lantern.getLogger().debug("Registered a data pack asset repository: " + path);
                repository.add(new PacksAssetRepository(this.pluginManager, path));
            } else {
                classLoaderAssetRepository.addRepository(path);
            }
        };
        classLoader.getBaseURLs().forEach(consumer);
        classLoader.addBaseURLTracker(consumer);
        final JsonArray array = json.getAsJsonArray();
        for (int i = 0; i < array.size(); i++) {
            final JsonObject obj = array.get(i).getAsJsonObject();
            final String type = obj.get("type").getAsString().toLowerCase(Locale.ENGLISH);
            Path path;
            switch (type) {
                // Currently only directory asset repositories
                case "dir":
                case "directory":
                    path = Paths.get(obj.get("path").getAsString());
                    Lantern.getLogger().debug("Registered a directory asset repository: " + path);
                    repository.add(new DirectoryAssetRepository(this.pluginManager, path));
                    break;
                // Also support a directory with data/asset packs
                case "packs":
                    path = Paths.get(obj.get("path").getAsString());
                    Lantern.getLogger().debug("Registered a data pack asset repository: " + path);
                    repository.add(new PacksAssetRepository(this.pluginManager, path));
                    break;
                default:
                    throw new JsonParseException("Unknown repository type: " + type);
            }
            if (!Files.exists(path)) {
                try {
                    Files.createDirectories(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return repository;
    }
}
