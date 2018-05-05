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

import org.apache.commons.lang3.StringUtils;
import org.lanternpowered.server.util.UncheckedThrowables;
import org.lanternpowered.server.util.functions.Long2ObjectFunction;
import org.lanternpowered.server.util.functions.Object2LongThrowableFunction;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;

/**
 * An enumeration of all the statistic formats in vanilla minecraft.
 */
public final class StatisticNumberFormats {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("########0.00");

    /**
     * A statistic without a format.
     */
    public static NumberFormat COUNT = NumberFormat.getIntegerInstance();

    /**
     * A statistic measured in centimeters, meters, or kilometers depending on
     * the magnitude. The input is taken as centimeters with a scale of 1 block
     * equaling 1 meter.
     */
    public static NumberFormat DISTANCE = of(
            value -> {
                if (value <= 50) {
                    return value + " cm";
                }
                final double meters = (double) value / 100.0;
                if (meters <= 500) {
                    return DECIMAL_FORMAT.format(meters) + " m";
                }
                return DECIMAL_FORMAT.format(meters / 1000.0) + " km";
            },
            object -> {
                object = StringUtils.normalizeSpace(object);
                final int index = object.lastIndexOf(' ');
                final String type = object.substring(index + 1).toLowerCase(Locale.ENGLISH);
                final Number value = DECIMAL_FORMAT.parse(object.substring(0, index));
                switch (type) {
                    case "cm": return value.longValue();
                    case "km": return (long) (value.doubleValue() * 100000.0);
                    case "m" : return (long) (value.doubleValue() * 100.0);
                    default:
                        throw new IllegalArgumentException("Invalid distance formatted string: " + object);
                }
            });

    /**
     * A statistic measured in 0.1 steps.
     */
    public static NumberFormat FRACTIONAL = of(
            value -> DECIMAL_FORMAT.format((double) value * 0.1),
            object -> (long) (DECIMAL_FORMAT.parse(object).doubleValue() / 0.1));

    /**
     * A statistic measured in seconds, minutes, hours, or days depending on the
     * magnitude. The input is taken as ticks with 20 ticks equaling one second.
     */
    public static NumberFormat TIME = of(
            value -> {
                final double seconds = (double) value / 20.0;
                if (seconds <= 30) {
                    return DECIMAL_FORMAT.format(seconds) + " s";
                }
                final double minutes = seconds / 60.0;
                if (minutes <= 30) {
                    return DECIMAL_FORMAT.format(minutes) + " m";
                }
                final double hours = seconds / 60.0;
                if (hours <= 12) {
                    return DECIMAL_FORMAT.format(hours) + " h";
                }
                final double days = seconds / 24.0;
                if (days <= 183) {
                    return DECIMAL_FORMAT.format(days) + " d";
                }
                return DECIMAL_FORMAT.format(days / 365.0) + " y";
            },
            object -> {
                object = StringUtils.normalizeSpace(object);
                final int index = object.lastIndexOf(' ');
                final String type = object.substring(index + 1).toLowerCase(Locale.ENGLISH);
                final double value = DECIMAL_FORMAT.parse(object.substring(0, index)).doubleValue();
                switch (type) {
                    case "s": return (long) (value * 20.0);
                    case "m": return (long) (value * 1200.0);
                    case "h": return (long) (value * 72000.0);
                    case "d": return (long) (value * 1728000.0);
                    case "y": return (long) (value * 630720000.0);
                    default:
                        throw new IllegalArgumentException("Invalid distance formatted string: " + object);
                }
            });

    /**
     * Creates a new {@link NumberFormat} with the specified formatter function.
     *
     * @param formatter The formatter
     * @return The number format
     */
    public static NumberFormat of(Long2ObjectFunction<String> formatter, Object2LongThrowableFunction<String, ParseException> parser) {
        return new NumberFormat() {
            @Override
            public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
                return format((long) number, toAppendTo, pos);
            }

            @Override
            public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
                return toAppendTo.append(formatter.apply(number));
            }

            @Override
            public Number parse(String source, ParsePosition parsePosition) {
                try {
                    return parser.apply(source);
                } catch (ParseException e) {
                    throw UncheckedThrowables.thrOw(e);
                }
            }
        };
    }

    private StatisticNumberFormats() {
    }
}
