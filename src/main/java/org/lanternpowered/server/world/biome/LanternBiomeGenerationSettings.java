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
package org.lanternpowered.server.world.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.lanternpowered.server.util.collect.Lists2;
import org.spongepowered.api.world.biome.BiomeGenerationSettings;
import org.spongepowered.api.world.biome.GroundCoverLayer;
import org.spongepowered.api.world.gen.GenerationPopulator;
import org.spongepowered.api.world.gen.Populator;

import java.util.List;

public final class LanternBiomeGenerationSettings implements BiomeGenerationSettings {

    // Using concurrent lists, we have no idea what plugin devs will do with them...
    private final List<GroundCoverLayer> groundCoverLayers = Lists2.nonNullOf(Lists.newCopyOnWriteArrayList());
    private final List<GenerationPopulator> generationPopulators = Lists2.nonNullOf(Lists.newCopyOnWriteArrayList());
    private final List<Populator> populators = Lists2.nonNullOf(Lists.newCopyOnWriteArrayList());

    private float minHeight;
    private float maxHeight;

    public LanternBiomeGenerationSettings() {
    }

    @Override
    public LanternBiomeGenerationSettings copy() {
        final LanternBiomeGenerationSettings copy = new LanternBiomeGenerationSettings();
        copy.maxHeight = this.maxHeight;
        copy.minHeight = this.minHeight;
        copy.groundCoverLayers.addAll(this.groundCoverLayers);
        copy.generationPopulators.addAll(this.generationPopulators);
        copy.populators.addAll(this.populators);
        return copy;
    }

    @Override
    public float getMinHeight() {
        return this.minHeight;
    }

    @Override
    public void setMinHeight(float height) {
        this.minHeight = height;
    }

    @Override
    public float getMaxHeight() {
        return this.maxHeight;
    }

    @Override
    public void setMaxHeight(float height) {
        this.maxHeight = height;
    }

    @Override
    public List<GroundCoverLayer> getGroundCoverLayers() {
        return this.groundCoverLayers;
    }

    @Override
    public List<GenerationPopulator> getGenerationPopulators() {
        return this.generationPopulators;
    }

    @Override
    public List<GenerationPopulator> getGenerationPopulators(Class<? extends GenerationPopulator> type) {
        return this.generationPopulators.stream().filter(type::isInstance).collect(ImmutableList.toImmutableList());
    }

    @Override
    public List<Populator> getPopulators() {
        return this.populators;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Populator> List<T> getPopulators(Class<T> type) {
        return (List<T>) this.populators.stream().filter(type::isInstance).collect(ImmutableList.toImmutableList());
    }
}
