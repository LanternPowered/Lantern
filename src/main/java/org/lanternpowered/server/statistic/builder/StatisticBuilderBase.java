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

import org.spongepowered.api.scoreboard.critieria.Criterion;
import org.spongepowered.api.statistic.Statistic;
import org.spongepowered.api.statistic.StatisticType;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.ResettableBuilder;

import javax.annotation.Nullable;

public interface StatisticBuilderBase<T extends Statistic, B extends StatisticBuilderBase<T, B>>
        extends ResettableBuilder<T, B> {

    /**
     * Sets the internal name for the {@link Statistic}.
     *
     * @param name The name of this achievement
     * @return This builder, for chaining
     */
    B name(String name);

    /**
     * Sets the translation for the {@link Statistic}.
     *
     * @param translation The translation for the statistic
     * @return This builder, for chaining
     */
    B translation(Translation translation);

    /**
     * Sets the criterion of the {@link Statistic}.
     *
     * @param criterion The criterion
     * @return This builder, for chaining
     */
    B criterion(@Nullable Criterion criterion);

    /**
     * Sets the {@link StatisticType} of the {@link Statistic}.
     *
     * @param type The statistic type
     * @return This builder, for chaining
     */
    B type(StatisticType type);

    /**
     * Builds an instance of a {@link Statistic}.
     *
     * @return A new instance of a statistic
     * @throws IllegalStateException If the statistic is not completed
     */
    T build(String pluginId, String id) throws IllegalStateException;

    @Override
    B from(T value);

    @Override
    B reset();
}
