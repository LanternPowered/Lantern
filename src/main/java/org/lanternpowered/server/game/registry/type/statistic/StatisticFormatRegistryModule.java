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
package org.lanternpowered.server.game.registry.type.statistic;

import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.statistic.LanternStatisticFormat;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.statistic.StatisticFormat;
import org.spongepowered.api.statistic.StatisticFormats;

import java.text.DecimalFormat;

public final class StatisticFormatRegistryModule extends AdditionalPluginCatalogRegistryModule<StatisticFormat> {

    private static final DecimalFormat FORMAT = new DecimalFormat("########0.00");
    private static final StatisticFormatRegistryModule INSTANCE = new StatisticFormatRegistryModule();

    public static StatisticFormatRegistryModule get() {
        return INSTANCE;
    }

    private StatisticFormatRegistryModule() {
        super(StatisticFormats.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternStatisticFormat("minecraft", "count", Long::toString));
        register(new LanternStatisticFormat("minecraft", "distance", value -> {
            if (value <= 50) {
                return value + " cm";
            }
            final double meters = (double) value / 100.0;
            if (meters <= 500) {
                return FORMAT.format(meters) + " m";
            }
            return FORMAT.format(meters / 1000.0) + " km";
        }));
        register(new LanternStatisticFormat("minecraft", "fractional", value -> FORMAT.format((double) value * 0.1)));
        register(new LanternStatisticFormat("minecraft", "time", value -> {
            final double seconds = (double) value / 20.0;
            if (seconds <= 30) {
                return FORMAT.format(seconds) + " s";
            }
            final double minutes = seconds / 60.0;
            if (minutes <= 30) {
                return FORMAT.format(minutes) + " m";
            }
            final double hours = seconds / 60.0;
            if (hours <= 12) {
                return FORMAT.format(hours) + " h";
            }
            final double days = seconds / 24.0;
            if (days <= 183) {
                return FORMAT.format(days) + " d";
            }
            return FORMAT.format(days / 365.0) + " y";
        }));
    }
}
