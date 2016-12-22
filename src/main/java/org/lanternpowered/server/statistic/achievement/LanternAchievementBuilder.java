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

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.statistic.builder.AbstractStatisticBuilder;
import org.spongepowered.api.scoreboard.critieria.Criterion;
import org.spongepowered.api.statistic.StatisticType;
import org.spongepowered.api.statistic.achievement.Achievement;
import org.spongepowered.api.text.translation.Translation;

import java.text.NumberFormat;

import javax.annotation.Nullable;

public final class LanternAchievementBuilder extends AbstractStatisticBuilder<IAchievement, AchievementBuilder> implements AchievementBuilder {

    private Translation description;
    @Nullable private Achievement parent;
    private long targetValue;

    @Override
    public LanternAchievementBuilder from(IAchievement value) {
        super.from(value);
        this.description = value.getDescription();
        this.parent = value.getParent().orElse(null);
        this.targetValue = value.getStatisticTargetValue();
        return this;
    }

    @Override
    public LanternAchievementBuilder reset() {
        super.reset();
        this.description = null;
        this.parent = null;
        this.targetValue = 1;
        return this;
    }

    @Override
    public LanternAchievementBuilder targetValue(long value) {
        this.targetValue = value;
        return this;
    }

    @Override
    public LanternAchievementBuilder description(Translation description) {
        this.description = checkNotNull(description, "description");
        return this;
    }

    @Override
    public LanternAchievementBuilder parent(@Nullable Achievement parent) {
        this.parent = parent;
        return this;
    }

    @Override
    protected IAchievement build(String pluginId, String id, String name, Translation translation, StatisticType type, NumberFormat format,
            String internalId, @Nullable Criterion criterion) {
        checkNotNull(this.description, "description");
        return new LanternAchievement(pluginId, id, name, translation, internalId, format, criterion,
                type, this.targetValue, this.parent, this.description);
    }
}
