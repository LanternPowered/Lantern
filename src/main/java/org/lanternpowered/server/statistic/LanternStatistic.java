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
import org.spongepowered.api.scoreboard.critieria.Criterion;
import org.spongepowered.api.statistic.Statistic;
import org.spongepowered.api.statistic.StatisticType;
import org.spongepowered.api.text.translation.Translation;

import java.text.NumberFormat;
import java.util.Optional;

import javax.annotation.Nullable;

public class LanternStatistic extends PluginCatalogType.Base.Translatable implements Statistic {

    @Nullable private final Criterion criterion;
    private final StatisticType type;
    private final NumberFormat format;
    private final String internalId;

    public LanternStatistic(String pluginId, String id, String name, Translation translation,
            String internalId, NumberFormat format, @Nullable Criterion criterion, StatisticType type) {
        super(pluginId, id, name, translation);
        this.internalId = internalId;
        this.criterion = criterion;
        this.format = format;
        this.type = type;
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("type", this.type.getId())
                .add("criterion", this.criterion == null ? null : this.criterion.getId())
                .omitNullValues();
    }

    public String getInternalId() {
        return this.internalId;
    }

    @Override
    public Optional<Criterion> getCriterion() {
        return Optional.ofNullable(this.criterion);
    }

    @Override
    public NumberFormat getFormat() {
        return this.format;
    }

    @Override
    public StatisticType getType() {
        return this.type;
    }
}
