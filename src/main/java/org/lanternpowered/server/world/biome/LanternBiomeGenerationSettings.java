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
package org.lanternpowered.server.world.biome;

import com.google.common.collect.Lists;
import org.lanternpowered.server.util.collect.Lists2;
import org.spongepowered.api.util.GuavaCollectors;
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

    /**
     * Creates a copy of this biome generation settings.
     * 
     * @return the copy
     */
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
        return this.generationPopulators.stream().filter(type::isInstance).collect(GuavaCollectors.toImmutableList());
    }

    @Override
    public List<Populator> getPopulators() {
        return this.populators;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Populator> List<T> getPopulators(Class<T> type) {
        return (List<T>) this.populators.stream().filter(type::isInstance).collect(GuavaCollectors.toImmutableList());
    }

}
