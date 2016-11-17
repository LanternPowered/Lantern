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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.statistic.StatisticFormat;
import org.spongepowered.api.statistic.StatisticGroup;
import org.spongepowered.api.statistic.TeamStatistic;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.translation.Translation;

import javax.annotation.Nullable;

public class LanternTeamStatisticBuilder extends AbstractStatisticBuilder<TeamStatistic, TeamStatistic.Builder>
        implements TeamStatistic.Builder {

    private TextColor teamColor;

    @Override
    public TeamStatistic.Builder teamColor(TextColor color) {
        this.teamColor = checkNotNull(color, "color");
        return this;
    }

    @Override
    public LanternTeamStatisticBuilder from(TeamStatistic value) {
        super.from(value);
        this.teamColor = value.getTeamColor();
        return this;
    }

    @Override
    public LanternTeamStatisticBuilder reset() {
        super.reset();
        this.teamColor = null;
        return this;
    }


    @Override
    TeamStatistic build(String pluginId, String id, String name, Translation translation, StatisticGroup group,
            @Nullable StatisticFormat format) {
        checkState(this.teamColor != null, "The teamColor must be set");
        return new LanternTeamStatistic(pluginId, id, name, translation, group, format, this.teamColor);
    }
}
