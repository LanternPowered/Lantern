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
package org.lanternpowered.server.asset.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.lanternpowered.server.asset.AssetRepository;
import org.lanternpowered.server.asset.ClassLoaderAssetRepository;
import org.lanternpowered.server.asset.DirectoryAssetRepository;
import org.lanternpowered.server.asset.MultiAssetRepository;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class AssetRepositoryJsonDeserializer implements JsonDeserializer<AssetRepository> {

    @Override
    public AssetRepository deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final MultiAssetRepository repository = new MultiAssetRepository();
        // The class loader asset repository will always be present,
        // this cannot be overridden, but the assets themselves can
        // be overridden like the minecraft resource pack system
        repository.add(new ClassLoaderAssetRepository());
        final JsonArray array = json.getAsJsonArray();
        for (int i = 0; i < array.size(); i++) {
            final JsonObject obj = array.get(i).getAsJsonObject();
            final String type = obj.get("type").getAsString().toLowerCase(Locale.ENGLISH);
            switch (type) {
                // Currently only directory asset repositories
                case "directory":
                    final Path path = Paths.get(obj.get("path").getAsString());
                    // Using Path#toFile().isDirectory() here, because Files#isDirectory expects
                    // the directory to exist, and we want to check if the provided path is valid
                    if (!path.toFile().isDirectory()) {
                        throw new JsonParseException("The directory repository path isn't a directory: " + path.toString());
                    }
                    final DirectoryAssetRepository repo = new DirectoryAssetRepository(path);
                    if (!Files.exists(path)) {
                        try {
                            Files.createDirectories(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    repository.add(repo);
                    break;
                default:
                    throw new JsonParseException("Unknown repository type: " + type);
            }
        }
        return repository;
    }
}
