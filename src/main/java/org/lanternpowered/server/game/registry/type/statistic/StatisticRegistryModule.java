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
package org.lanternpowered.server.game.registry.type.statistic;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.statistic.LanternStatistic;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.statistic.Statistic;
import org.spongepowered.api.statistic.Statistics;
import org.spongepowered.api.statistic.achievement.Achievement;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RegistrationDependency(StatisticTypeRegistryModule.class)
public final class StatisticRegistryModule extends AdditionalPluginCatalogRegistryModule<Statistic> {

    private static final StatisticRegistryModule INSTANCE = new StatisticRegistryModule();

    public static StatisticRegistryModule get() {
        return INSTANCE;
    }

    private final Map<String, Statistic> byInternalId = new HashMap<>();

    private StatisticRegistryModule() {
        super(Statistics.class);
    }

    @Override
    protected void register(Statistic catalogType) {
        super.register(catalogType);
    }

    @Override
    protected void register(Statistic catalogType, boolean disallowInbuiltPluginIds) {
        internalRegister(catalogType, disallowInbuiltPluginIds);
        if (catalogType instanceof Achievement) {
            AchievementRegistryModule.get().internalRegister((Achievement) catalogType, disallowInbuiltPluginIds);
        }
    }

    void internalRegister(Statistic catalogType, boolean disallowInbuiltPluginIds) {
        super.register(catalogType, disallowInbuiltPluginIds);
        this.byInternalId.put(((LanternStatistic) catalogType).getInternalId(), catalogType);
    }

    @Override
    public void registerDefaults() {
    }

    public Optional<Statistic> getByInternalId(String internalId) {
        return Optional.ofNullable(this.byInternalId.get(checkNotNull(internalId, "internalId")));
    }
}
