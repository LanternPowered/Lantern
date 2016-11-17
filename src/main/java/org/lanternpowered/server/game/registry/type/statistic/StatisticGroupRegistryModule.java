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
import org.lanternpowered.server.statistic.LanternStatisticGroup;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.statistic.StatisticFormats;
import org.spongepowered.api.statistic.StatisticGroup;
import org.spongepowered.api.statistic.StatisticGroups;

@RegistrationDependency(StatisticFormatRegistryModule.class)
public final class StatisticGroupRegistryModule extends AdditionalPluginCatalogRegistryModule<StatisticGroup> {

    private static final StatisticGroupRegistryModule INSTANCE = new StatisticGroupRegistryModule();

    public static StatisticGroupRegistryModule get() {
        return INSTANCE;
    }

    private StatisticGroupRegistryModule() {
        super(StatisticGroups.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternStatisticGroup("sponge", "break_item", tr("stat.depleted"), StatisticFormats.COUNT));
        register(new LanternStatisticGroup("sponge", "craft_block", tr("stat.crafted"), StatisticFormats.COUNT));
        register(new LanternStatisticGroup("sponge", "craft_item", tr("stat.crafted"), StatisticFormats.COUNT));
        register(new LanternStatisticGroup("sponge", "general", tr("stat.generalButton"), StatisticFormats.COUNT));
        register(new LanternStatisticGroup("sponge", "has_killed_entity", tr("stat.entityKills"), StatisticFormats.COUNT));
        register(new LanternStatisticGroup("sponge", "has_killed_team", tr("stat.teamKills"), StatisticFormats.COUNT));
        register(new LanternStatisticGroup("sponge", "hidden", tr("stats.tooltip.type.statistic"), StatisticFormats.COUNT));
        register(new LanternStatisticGroup("sponge", "killed_by_entity", tr("stat.entityKilledBy"), StatisticFormats.COUNT));
        register(new LanternStatisticGroup("sponge", "killed_by_team", tr("stat.teamKilledBy"), StatisticFormats.COUNT));
        register(new LanternStatisticGroup("sponge", "mine_block", tr("stat.mined"), StatisticFormats.COUNT));
        register(new LanternStatisticGroup("sponge", "use_block", tr("stat.used"), StatisticFormats.COUNT));
        register(new LanternStatisticGroup("sponge", "use_item", tr("stat.used"), StatisticFormats.COUNT));
        // No group fields??
        register(new LanternStatisticGroup("sponge", "pick_up_item", tr("stat.pickup"), StatisticFormats.COUNT));
        register(new LanternStatisticGroup("sponge", "drop_block", tr("stat.drop"), StatisticFormats.COUNT));
        register(new LanternStatisticGroup("sponge", "drop_item", tr("stat.drop"), StatisticFormats.COUNT));
        register(new LanternStatisticGroup("sponge", "travelled_distance", tr("stat.distance"), StatisticFormats.COUNT));
    }
}
