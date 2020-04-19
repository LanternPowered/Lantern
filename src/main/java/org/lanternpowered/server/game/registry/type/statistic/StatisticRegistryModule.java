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
package org.lanternpowered.server.game.registry.type.statistic;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.statistic.LanternStatistic;
import org.lanternpowered.server.statistic.StatisticCategoryRegistry;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.statistic.Statistic;
import org.spongepowered.api.statistic.Statistics;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RegistrationDependency(StatisticCategoryRegistry.class)
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
    protected void doRegistration(Statistic catalogType, boolean disallowInbuiltPluginIds) {
        super.doRegistration(catalogType, disallowInbuiltPluginIds);
        this.byInternalId.put(((LanternStatistic) catalogType).getInternalId(), catalogType);
    }

    @Override
    public void registerDefaults() {
    }

    public Optional<Statistic> getByInternalId(String internalId) {
        return Optional.ofNullable(this.byInternalId.get(checkNotNull(internalId, "internalId")));
    }
}
