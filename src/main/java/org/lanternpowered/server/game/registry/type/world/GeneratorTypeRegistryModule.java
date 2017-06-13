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
package org.lanternpowered.server.game.registry.type.world;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.game.registry.type.block.BlockStateRegistryModule;
import org.lanternpowered.server.world.gen.DelegateGeneratorType;
import org.lanternpowered.server.world.gen.debug.DebugGeneratorType;
import org.lanternpowered.server.world.gen.flat.FlatOverworldGeneratorType;
import org.lanternpowered.server.world.gen.flat.FlatNetherGeneratorType;
import org.lanternpowered.server.world.gen.flat.FlatTheEndGeneratorType;
import org.lanternpowered.server.world.gen.skylands.SkylandsGeneratorType;
import org.lanternpowered.server.world.gen.thevoid.TheVoidGeneratorType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.registry.RegistrationPhase;
import org.spongepowered.api.registry.util.CustomCatalogRegistration;
import org.spongepowered.api.registry.util.DelayedRegistration;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.GeneratorTypes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RegistrationDependency({ BlockRegistryModule.class, BlockStateRegistryModule.class })
public final class GeneratorTypeRegistryModule extends AdditionalPluginCatalogRegistryModule<GeneratorType> {

    public GeneratorTypeRegistryModule() {
        super(GeneratorTypes.class);
    }

    @Override
    public void registerDefaults() {
        final FlatOverworldGeneratorType flat = new FlatOverworldGeneratorType("minecraft", "flat");
        final FlatNetherGeneratorType flatNether = new FlatNetherGeneratorType("lantern", "flat_nether");
        final FlatTheEndGeneratorType flatTheEnd = new FlatTheEndGeneratorType("lantern", "flat_the_end");

        // Default inbuilt generator types
        register(flat);
        register(flatNether);
        register(flatTheEnd);
        register(new DebugGeneratorType("minecraft", "debug"));

        // Plugin provided generator types, these will fall back
        // to flat if missing
        register(new DelegateGeneratorType("minecraft", "default", flat));
        register(new DelegateGeneratorType("minecraft", "overworld", flat));
        register(new DelegateGeneratorType("minecraft", "large_biomes", flat));
        register(new DelegateGeneratorType("minecraft", "amplified", flat));
        register(new DelegateGeneratorType("minecraft", "nether", flatNether));
        register(new DelegateGeneratorType("minecraft", "the_end", flatTheEnd));

        // Sponge
        register(new SkylandsGeneratorType("sponge", "skylands"));
        register(new TheVoidGeneratorType("sponge", "void"));
    }

    /**
     * Post initialize the {@link GeneratorType}s. All the default world generators
     * here be selected by scanning for 'default-world-gen.json' files.
     */
    @CustomCatalogRegistration
    @DelayedRegistration(RegistrationPhase.POST_INIT)
    public void postInit() {
        final Multimap<String, DefaultEntry> entries = HashMultimap.create();
        final Gson gson = new Gson();
        // Scan every plugin
        for (PluginContainer pluginContainer : Sponge.getPluginManager().getPlugins()) {
            final Optional<Asset> optAsset = pluginContainer.getAsset("default-world-gen.json");
            if (optAsset.isPresent()) {
                try {
                    final InputStream is = optAsset.get().getUrl().openStream();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                        final JsonObject json = gson.fromJson(reader, JsonObject.class);
                        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                            entries.put(entry.getKey(), new DefaultEntry(pluginContainer, entry.getValue().getAsString()));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        for (Map.Entry<String, Collection<DefaultEntry>> entry : entries.asMap().entrySet()) {
            final String id = entry.getKey();
            if (!getById(id).map(type -> type instanceof DelegateGeneratorType).orElse(false)) {
                Lantern.getLogger().warn("The plugin(s) ({}) attempted to map an unknown id: {}",
                        Arrays.toString(entry.getValue().stream().map(e -> e.pluginContainer.getId()).toArray()), id);
                continue;
            }
            final List<DefaultEntry> possibleEntries = new ArrayList<>();
            for (DefaultEntry entry1 : entry.getValue()) {
                final Optional<GeneratorType> generatorType = getById(entry1.type);
                if (generatorType.isPresent()) {
                    possibleEntries.add(entry1);
                } else {
                    Lantern.getLogger().warn("The plugin {} attempted to map a missing generator type {} for {}",
                            entry1.pluginContainer.getId(), entry1.type, id);
                }
            }
            if (!possibleEntries.isEmpty()) {
                final DefaultEntry defaultEntry = possibleEntries.get(0);
                if (possibleEntries.size() > 1) {
                    Lantern.getLogger().warn("Multiple plugins are mapping {}: {}", id,
                            Arrays.toString(entry.getValue().stream().map(e -> "\n" + e.pluginContainer.getId() + ": " + e.type).toArray()));
                    Lantern.getLogger().warn("The first one will be used.");
                }
                ((DelegateGeneratorType) getById(id).get()).setGeneratorType(getById(defaultEntry.type).get());
                Lantern.getLogger().warn("Successfully registered a generator type mapping: {} from {} for {}",
                        defaultEntry.type, defaultEntry.pluginContainer.getId(), id);
            }
        }
    }

    private static final class DefaultEntry {

        private final PluginContainer pluginContainer;
        private final String type;

        private DefaultEntry(PluginContainer pluginContainer, String type) {
            this.pluginContainer = pluginContainer;
            this.type = type;
        }
    }
}
