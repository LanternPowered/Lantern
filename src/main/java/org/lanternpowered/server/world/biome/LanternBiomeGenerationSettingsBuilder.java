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

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.world.biome.BiomeGenerationSettings;
import org.spongepowered.api.world.biome.BiomeGenerationSettings.Builder;
import org.spongepowered.api.world.biome.GroundCoverLayer;
import org.spongepowered.api.world.gen.GenerationPopulator;
import org.spongepowered.api.world.gen.Populator;

import java.util.ArrayList;
import java.util.List;

public class LanternBiomeGenerationSettingsBuilder implements BiomeGenerationSettings.Builder {

    private float min = 0;
    private float max = 0;
    private final List<GroundCoverLayer> groundCover = new ArrayList<>();
    private final List<Populator> populators = new ArrayList<>();
    private final List<GenerationPopulator> generationPopulators = new ArrayList<>();

    @Override
    public Builder from(BiomeGenerationSettings value) {
        this.min = value.getMinHeight();
        this.max = value.getMaxHeight();
        this.groundCover.clear();
        this.groundCover.addAll(value.getGroundCoverLayers());
        this.generationPopulators.clear();
        this.generationPopulators.addAll(value.getGenerationPopulators());
        this.populators.clear();
        this.populators.addAll(value.getPopulators());
        return this;
    }

    @Override
    public Builder reset() {
        this.min = 0;
        this.max = 0;
        this.groundCover.clear();
        this.populators.clear();
        this.generationPopulators.clear();
        return this;
    }

    @Override
    public Builder minHeight(float height) {
        this.min = height;
        return this;
    }

    @Override
    public Builder maxHeight(float height) {
        this.max = height;
        return this;
    }

    @Override
    public Builder groundCover(GroundCoverLayer... coverLayers) {
        checkNotNull(coverLayers, "coverLayers");
        this.groundCover.clear();
        for (GroundCoverLayer layer : coverLayers) {
            this.groundCover.add(checkNotNull(layer, "layer"));
        }
        return this;
    }

    @Override
    public Builder groundCover(Iterable<GroundCoverLayer> coverLayers) {
        checkNotNull(coverLayers, "coverLayers");
        this.groundCover.clear();
        for (GroundCoverLayer layer : coverLayers) {
            this.groundCover.add(checkNotNull(layer, "layer"));
        }
        return this;
    }

    @Override
    public Builder generationPopulators(GenerationPopulator... populators) {
        checkNotNull(populators, "populators");
        this.generationPopulators.clear();
        for (GenerationPopulator populator : populators) {
            this.generationPopulators.add(checkNotNull(populator, "populator"));
        }
        return this;
    }

    @Override
    public Builder generationPopulators(Iterable<GenerationPopulator> populators) {
        checkNotNull(populators, "populators");
        this.generationPopulators.clear();
        for (GenerationPopulator populator : populators) {
            this.generationPopulators.add(checkNotNull(populator, "populator"));
        }
        return this;
    }

    @Override
    public Builder populators(Populator... populators) {
        checkNotNull(populators, "populators");
        this.populators.clear();
        for (Populator pop : populators) {
            this.populators.add(checkNotNull(pop, "pop"));
        }
        return this;
    }

    @Override
    public Builder populators(Iterable<Populator> populators) {
        checkNotNull(populators, "populators");
        this.populators.clear();
        for (Populator pop : populators) {
            this.populators.add(checkNotNull(pop, "pop"));
        }
        return this;
    }

    @Override
    public BiomeGenerationSettings build() throws IllegalStateException {
        final LanternBiomeGenerationSettings settings = new LanternBiomeGenerationSettings();
        settings.setMinHeight(this.min);
        settings.setMaxHeight(this.max);
        settings.getGroundCoverLayers().addAll(this.groundCover);
        settings.getPopulators().addAll(this.populators);
        settings.getGenerationPopulators().addAll(this.generationPopulators);
        return settings;
    }

}