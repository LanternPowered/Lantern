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
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import org.lanternpowered.server.statistic.StatisticNumberFormats;
import org.spongepowered.api.scoreboard.critieria.Criterion;
import org.spongepowered.api.statistic.Statistic;
import org.spongepowered.api.statistic.StatisticType;
import org.spongepowered.api.statistic.StatisticTypes;
import org.spongepowered.api.text.translation.Translation;

import java.text.NumberFormat;
import java.util.Locale;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public abstract class AbstractStatisticBuilder<T extends Statistic, B extends StatisticBuilderBase<T, B>> implements StatisticBuilderBase<T, B> {

    @Nullable private String name;
    private Translation translation;
    private StatisticType type;
    private NumberFormat format;
    @Nullable private String internalId;
    @Nullable private Criterion criterion;

    public AbstractStatisticBuilder() {
        reset();
    }

    @Override
    public B from(T value) {
        this.name = value.getName();
        this.translation = value.getTranslation();
        return (B) this;
    }

    @Override
    public B reset() {
        this.name = null;
        this.translation = null;
        this.type = StatisticTypes.BASIC;
        this.format = StatisticNumberFormats.COUNT;
        this.internalId = null;
        this.criterion = null;
        return (B) this;
    }

    @Override
    public B name(String name) {
        this.name = checkNotNull(name, "name");
        return (B) this;
    }

    @Override
    public B translation(Translation translation) {
        this.translation = checkNotNull(translation, "translation");
        return (B) this;
    }

    @Override
    public B criterion(@Nullable Criterion criterion) {
        this.criterion = criterion;
        return (B) this;
    }

    @Override
    public B type(StatisticType type) {
        this.type = checkNotNull(type, "type");
        return (B) this;
    }

    @Override
    public T build(String pluginId, String id) throws IllegalStateException {
        checkNotNullOrEmpty(pluginId, "pluginId");
        checkNotNullOrEmpty(id, "id");
        checkState(this.translation != null, "The translation must be set");
        final String name = this.name == null ? id : this.name;
        final String internalId = this.internalId == null ? pluginId + ':' + name.toLowerCase(Locale.ENGLISH) : this.internalId;
        return build(pluginId, id, name, this.translation, this.type, this.format, internalId, this.criterion);
    }

    public B internalId(String internalId) {
        this.internalId = checkNotNull(internalId, "internalId");
        return (B) this;
    }

    protected abstract T build(String pluginId, String id, String name, Translation translation,
            StatisticType type, NumberFormat format, String internalId, @Nullable Criterion criterion);
}
