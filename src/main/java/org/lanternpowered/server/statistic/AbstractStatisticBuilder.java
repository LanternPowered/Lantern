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

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.statistic.Statistic;
import org.spongepowered.api.statistic.StatisticFormat;
import org.spongepowered.api.statistic.StatisticGroup;
import org.spongepowered.api.text.translation.Translation;

import java.util.Locale;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public abstract class AbstractStatisticBuilder<T extends Statistic, B extends Statistic.StatisticBuilder<T, B>> implements Statistic.StatisticBuilder<T, B> {

    private String name;
    private Translation translation;
    private StatisticGroup group;
    @Nullable private StatisticFormat format;

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
        this.group = null;
        this.format = null;
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
    public B format(@Nullable StatisticFormat format) {
        this.format = format;
        return (B) this;
    }

    @Override
    public B group(StatisticGroup group) {
        this.group = checkNotNull(group, "group");
        return (B) this;
    }

    public T build() throws IllegalStateException {
        checkState(this.name != null, "The name must be set");
        checkState(this.translation != null, "The translation must be set");
        checkState(this.group != null, "The group must be set");
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
        final StatisticFormat format = this.format == null ? this.group.getDefaultStatisticFormat() : this.format;
        return build(pluginId, name.toLowerCase(Locale.ENGLISH), name, this.translation, this.group, format);
    }

    abstract T build(String pluginId, String id, String name, Translation translation,
            StatisticGroup group, @Nullable StatisticFormat format);

    @Override
    public T buildAndRegister() throws IllegalStateException {
        final T statistic = build();
        Lantern.getRegistry().register(Statistic.class, statistic);
        return statistic;
    }
}
