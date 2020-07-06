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
package org.lanternpowered.server.game.registry.type.world;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.lanternpowered.api.catalog.CatalogKeys;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.game.registry.type.block.BlockStateRegistryModule;
import org.lanternpowered.server.world.gen.DelegateGeneratorType;
import org.lanternpowered.server.world.gen.debug.DebugGeneratorType;
import org.lanternpowered.server.world.gen.flat.FlatNetherGeneratorType;
import org.lanternpowered.server.world.gen.flat.FlatOverworldGeneratorType;
import org.lanternpowered.server.world.gen.flat.FlatTheEndGeneratorType;
import org.lanternpowered.server.world.gen.thevoid.TheVoidGeneratorType;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.api.registry.RegistrationPhase;
import org.spongepowered.api.registry.util.CustomCatalogRegistration;
import org.spongepowered.api.registry.util.DelayedRegistration;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.world.gen.GeneratorType;
import org.spongepowered.api.world.gen.GeneratorTypes;

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
        final FlatOverworldGeneratorType flat = new FlatOverworldGeneratorType(CatalogKeys.minecraft("flat"));
        final FlatNetherGeneratorType flatNether = new FlatNetherGeneratorType(CatalogKeys.lantern("flat_nether"));
        final FlatTheEndGeneratorType flatTheEnd = new FlatTheEndGeneratorType(CatalogKeys.lantern("flat_the_end"));

        // Default inbuilt generator types
        register(flat);
        register(flatNether);
        register(flatTheEnd);
        register(new DebugGeneratorType(CatalogKeys.minecraft("minecraft")));

        // Plugin provided generator types, these will fall back
        // to flat if missing
        register(new DelegateGeneratorType(CatalogKeys.minecraft("default"), flat));
        register(new DelegateGeneratorType(CatalogKeys.minecraft("overworld"), flat));
        register(new DelegateGeneratorType(CatalogKeys.minecraft("large_biomes"), flat));
        register(new DelegateGeneratorType(CatalogKeys.minecraft("amplified"), flat));
        register(new DelegateGeneratorType(CatalogKeys.minecraft("nether"), flatNether));
        register(new DelegateGeneratorType(CatalogKeys.minecraft("the_end"), flatTheEnd));

        // Sponge
        register(new TheVoidGeneratorType(CatalogKeys.sponge("void")));
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
                            entries.put(entry.getKey(), new DefaultEntry(
                                    pluginContainer, CatalogKey.resolve(entry.getValue().getAsString())));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        for (Map.Entry<String, Collection<DefaultEntry>> entry : entries.asMap().entrySet()) {
            final CatalogKey key = CatalogKey.resolve(entry.getKey());
            if (!get(key).map(type -> type instanceof DelegateGeneratorType).orElse(false)) {
                Lantern.getLogger().warn("The plugin(s) ({}) attempted to map an unknown id: {}",
                        Arrays.toString(entry.getValue().stream().map(e -> e.pluginContainer.getId()).toArray()), key);
                continue;
            }
            final List<DefaultEntry> possibleEntries = new ArrayList<>();
            for (DefaultEntry entry1 : entry.getValue()) {
                final Optional<GeneratorType> generatorType = get(entry1.type);
                if (generatorType.isPresent()) {
                    possibleEntries.add(entry1);
                } else {
                    Lantern.getLogger().warn("The plugin {} attempted to map a missing generator type {} for {}",
                            entry1.pluginContainer.getId(), entry1.type, key);
                }
            }
            if (!possibleEntries.isEmpty()) {
                final DefaultEntry defaultEntry = possibleEntries.get(0);
                if (possibleEntries.size() > 1) {
                    Lantern.getLogger().warn("Multiple plugins are mapping {}: {}", key,
                            Arrays.toString(entry.getValue().stream().map(e -> "\n" + e.pluginContainer.getId() + ": " + e.type).toArray()));
                    Lantern.getLogger().warn("The first one will be used.");
                }
                ((DelegateGeneratorType) get(key).get()).setGeneratorType(get(defaultEntry.type).get());
                Lantern.getLogger().warn("Successfully registered a generator type mapping: {} from {} for {}",
                        defaultEntry.type, defaultEntry.pluginContainer.getId(), key);
            }
        }
    }

    private static final class DefaultEntry {

        private final PluginContainer pluginContainer;
        private final CatalogKey type;

        private DefaultEntry(PluginContainer pluginContainer, CatalogKey type) {
            this.pluginContainer = pluginContainer;
            this.type = type;
        }
    }
}
