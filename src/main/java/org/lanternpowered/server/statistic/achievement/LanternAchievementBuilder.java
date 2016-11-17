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

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.statistic.Statistic;
import org.spongepowered.api.statistic.achievement.Achievement;
import org.spongepowered.api.text.translation.Translation;

import java.util.Locale;

import javax.annotation.Nullable;

public class LanternAchievementBuilder implements Achievement.Builder {

    private String name;
    private Translation translation;
    private Translation description;
    @Nullable private Achievement parent;
    @Nullable private Statistic sourceStatistic;
    private long targetValue;

    public LanternAchievementBuilder() {
        reset();
    }

    @Override
    public LanternAchievementBuilder from(Achievement value) {
        this.name = value.getName();
        this.translation = value.getTranslation();
        this.description = value.getDescription();
        this.parent = value.getParent().orElse(null);
        this.sourceStatistic = value.getSourceStatistic().orElse(null);
        this.targetValue = value.getStatisticTargetValue().orElse(1L);
        return this;
    }

    @Override
    public LanternAchievementBuilder reset() {
        this.name = null;
        this.translation = null;
        this.description = null;
        this.parent = null;
        this.sourceStatistic = null;
        this.targetValue = 1;
        return this;
    }

    @Override
    public LanternAchievementBuilder name(String name) {
        this.name = checkNotNull(name, "name");
        return this;
    }

    @Override
    public LanternAchievementBuilder translation(Translation translation) {
        this.translation = checkNotNull(translation, "translation");
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
    public LanternAchievementBuilder sourceStatistic(@Nullable Statistic stat) {
        this.sourceStatistic = stat;
        return this;
    }

    @Override
    public LanternAchievementBuilder targetValue(long value) {
        this.targetValue = value;
        return this;
    }

    public LanternAchievement build() throws IllegalStateException {
        checkNotNull(this.name, "name");
        checkNotNull(this.translation, "translation");
        checkNotNull(this.description, "description");
        final int index = this.name.indexOf(':');
        final String pluginId;
        final String name;
        if (index == -1) {
            pluginId = LanternGame.SPONGE_PLATFORM_ID;
            name = this.name;
        } else {
            pluginId = this.name.substring(0, index).toLowerCase();
            name = this.name.substring(index + 1);
        }
        return new LanternAchievement(pluginId, name.toLowerCase(Locale.ENGLISH), name,
                this.translation, this.parent, this.description, this.sourceStatistic, this.targetValue);
    }

    @Override
    public Achievement buildAndRegister() throws IllegalStateException {
        final LanternAchievement achievement = build();
        Lantern.getRegistry().register(Achievement.class, achievement);
        return achievement;
    }
}
