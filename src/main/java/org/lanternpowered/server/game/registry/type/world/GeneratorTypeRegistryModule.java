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

import org.lanternpowered.api.NamespacedKeys;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.game.registry.type.block.BlockStateRegistryModule;
import org.lanternpowered.server.world.gen.DelegateGeneratorType;
import org.lanternpowered.server.world.gen.debug.DebugGeneratorType;
import org.lanternpowered.server.world.gen.flat.FlatNetherGeneratorType;
import org.lanternpowered.server.world.gen.flat.FlatOverworldGeneratorType;
import org.lanternpowered.server.world.gen.flat.FlatTheEndGeneratorType;
import org.lanternpowered.server.world.gen.thevoid.TheVoidGeneratorType;
import org.spongepowered.api.registry.RegistrationPhase;
import org.spongepowered.api.registry.util.CustomCatalogRegistration;
import org.spongepowered.api.registry.util.DelayedRegistration;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.world.gen.GeneratorType;
import org.spongepowered.api.world.gen.GeneratorTypes;

@RegistrationDependency({ BlockRegistryModule.class, BlockStateRegistryModule.class })
public final class GeneratorTypeRegistryModule extends AdditionalPluginCatalogRegistryModule<GeneratorType> {

    public GeneratorTypeRegistryModule() {
        super(GeneratorTypes.class);
    }

    @Override
    public void registerDefaults() {
        final FlatOverworldGeneratorType flat = new FlatOverworldGeneratorType(NamespacedKeys.minecraft("flat"));
        final FlatNetherGeneratorType flatNether = new FlatNetherGeneratorType(NamespacedKeys.lantern("flat_the_nether"));
        final FlatTheEndGeneratorType flatTheEnd = new FlatTheEndGeneratorType(NamespacedKeys.lantern("flat_the_end"));

        // Default inbuilt generator types
        register(flat);
        register(flatNether);
        register(flatTheEnd);
        register(new DebugGeneratorType(NamespacedKeys.minecraft("minecraft")));

        // Plugin provided generator types, these will fall back
        // to flat if missing
        register(new DelegateGeneratorType(NamespacedKeys.minecraft("default"), flat));
        register(new DelegateGeneratorType(NamespacedKeys.minecraft("overworld"), flat));
        register(new DelegateGeneratorType(NamespacedKeys.minecraft("large_biomes"), flat));
        register(new DelegateGeneratorType(NamespacedKeys.minecraft("amplified"), flat));
        register(new DelegateGeneratorType(NamespacedKeys.minecraft("the_nether"), flatNether));
        register(new DelegateGeneratorType(NamespacedKeys.minecraft("the_end"), flatTheEnd));

        // Sponge
        register(new TheVoidGeneratorType(NamespacedKeys.sponge("void")));
    }

    /**
     * Post initialize the {@link GeneratorType}s. All the default world generators
     * here be selected by scanning for 'default-world-gen.json' files.
     */
    /*
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
                                    pluginContainer, ResourceKey.resolve(entry.getValue().getAsString())));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        for (Map.Entry<String, Collection<DefaultEntry>> entry : entries.asMap().entrySet()) {
            final ResourceKey key = ResourceKey.resolve(entry.getKey());
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
        private final ResourceKey type;

        private DefaultEntry(PluginContainer pluginContainer, ResourceKey type) {
            this.pluginContainer = pluginContainer;
            this.type = type;
        }
    }
    */
}
