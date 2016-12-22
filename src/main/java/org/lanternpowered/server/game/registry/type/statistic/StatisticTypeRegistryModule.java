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

import static org.lanternpowered.server.text.translation.TranslationHelper.tr;

import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.statistic.LanternStatisticType;
import org.spongepowered.api.statistic.StatisticType;
import org.spongepowered.api.statistic.StatisticTypes;

public final class StatisticTypeRegistryModule extends AdditionalPluginCatalogRegistryModule<StatisticType> {

    private static final StatisticTypeRegistryModule INSTANCE = new StatisticTypeRegistryModule();

    public static StatisticTypeRegistryModule get() {
        return INSTANCE;
    }

    private StatisticTypeRegistryModule() {
        super(StatisticTypes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternStatisticType("sponge", "basic", tr("Basic")));
        register(new LanternStatisticType("sponge", "blocks_broken", tr("Blocks Broken")));
        register(new LanternStatisticType("sponge", "entities_killed", tr("Entities Killed")));
        register(new LanternStatisticType("sponge", "items_broken", tr("Items Broken")));
        register(new LanternStatisticType("sponge", "items_crafted", tr("Items Crafted")));
        register(new LanternStatisticType("sponge", "items_dropped", tr("Items Dropped")));
        register(new LanternStatisticType("sponge", "items_picked_up", tr("Items Picked Up")));
        register(new LanternStatisticType("sponge", "items_used", tr("Items Used")));
        register(new LanternStatisticType("sponge", "killed_by_entity", tr("Killed By Entity")));
    }
}
