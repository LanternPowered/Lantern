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
package org.lanternpowered.server.statistic.builder;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.statistic.LanternEntityStatistic;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.scoreboard.critieria.Criterion;
import org.spongepowered.api.statistic.EntityStatistic;
import org.spongepowered.api.statistic.StatisticType;
import org.spongepowered.api.text.translation.Translation;

import java.text.NumberFormat;

import javax.annotation.Nullable;

final class LanternEntityStatisticBuilder extends AbstractStatisticBuilder<EntityStatistic, EntityStatisticBuilder>
        implements EntityStatisticBuilder {

    private EntityType entityType;

    @Override
    public LanternEntityStatisticBuilder entity(EntityType entity) {
        this.entityType = checkNotNull(entity, "entity");
        return this;
    }

    @Override
    public LanternEntityStatisticBuilder from(EntityStatistic value) {
        super.from(value);
        this.entityType = value.getEntityType();
        return this;
    }

    @Override
    public LanternEntityStatisticBuilder reset() {
        super.reset();
        this.entityType = null;
        return this;
    }

    @Override
    protected EntityStatistic build(String pluginId, String id, String name, Translation translation, StatisticType type, NumberFormat format,
            String internalId, @Nullable Criterion criterion) {
        checkState(this.entityType != null, "The entityType must be set");
        return new LanternEntityStatistic(pluginId, id, name, translation, internalId, format, criterion, type, this.entityType);
    }
}
