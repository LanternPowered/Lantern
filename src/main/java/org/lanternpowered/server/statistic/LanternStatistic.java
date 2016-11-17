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
package org.lanternpowered.server.statistic;

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.catalog.PluginCatalogType;
import org.lanternpowered.server.statistic.achievement.LanternAchievement;
import org.spongepowered.api.statistic.Statistic;
import org.spongepowered.api.statistic.StatisticFormat;
import org.spongepowered.api.statistic.StatisticGroup;
import org.spongepowered.api.text.translation.Translation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

public class LanternStatistic extends PluginCatalogType.Base.Translatable implements Statistic {

    private final StatisticGroup group;
    @Nullable private final StatisticFormat format;

    /**
     * All the achievements that depend on this statistic, and the
     * achievements should be added when this statistic is reached.
     */
    private Set<LanternAchievement> updateAchievements = new HashSet<>();

    LanternStatistic(String pluginId, String id, String name, Translation translation,
            StatisticGroup group, @Nullable StatisticFormat format) {
        super(pluginId, id, name, translation);
        this.group = group;
        this.format = format;
    }

    void addUpdateAchievement(LanternAchievement achievement) {
        this.updateAchievements.add(achievement);
    }

    @Override
    public Optional<StatisticFormat> getStatisticFormat() {
        return Optional.ofNullable(this.format);
    }

    @Override
    public StatisticGroup getGroup() {
        return this.group;
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("group", this.group.getId());
    }

    public Set<LanternAchievement> getUpdateAchievements() {
        return Collections.unmodifiableSet(this.updateAchievements);
    }
}
