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
package org.lanternpowered.server.statistic.achievement;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.catalog.PluginCatalogType;
import org.spongepowered.api.statistic.Statistic;
import org.spongepowered.api.statistic.achievement.Achievement;
import org.spongepowered.api.text.translation.Translation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

public class LanternAchievement extends PluginCatalogType.Base.Translatable implements Achievement {

    private final Set<Achievement> children = new HashSet<>();
    @Nullable private volatile Set<Achievement> immutableChildren;

    @Nullable private final Achievement parent;
    private final Translation description;
    @Nullable private final Statistic sourceStatistic;
    private final long statisticTargetValue;

    LanternAchievement(String pluginId, String id, String name, Translation translation,
            @Nullable Achievement parent, Translation description, @Nullable Statistic sourceStatistic, long statisticTargetValue) {
        super(pluginId, id, name, translation);
        this.sourceStatistic = sourceStatistic;
        this.statisticTargetValue = statisticTargetValue;
        this.description = description;
        this.parent = parent;
    }

    void addChild(LanternAchievement achievement) {
        this.children.add(achievement);
    }

    @Override
    public Translation getDescription() {
        return this.description;
    }

    @Override
    public Optional<Achievement> getParent() {
        return Optional.ofNullable(this.parent);
    }

    @Override
    public Collection<Achievement> getChildren() {
        Set<Achievement> immutableChildren = this.immutableChildren;
        if (immutableChildren == null) {
            immutableChildren = ImmutableSet.copyOf(this.children);
            this.immutableChildren = immutableChildren;
        }
        return immutableChildren;
    }

    @Override
    public Optional<Statistic> getSourceStatistic() {
        return Optional.ofNullable(this.sourceStatistic);
    }

    @Override
    public Optional<Long> getStatisticTargetValue() {
        return this.sourceStatistic == null ? Optional.empty() : Optional.of(this.statisticTargetValue);
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .omitNullValues()
                .add("sourceStatistic", getSourceStatistic().orElse(null))
                .add("statisticTargetValue", getStatisticTargetValue().orElse(null));
    }
}
