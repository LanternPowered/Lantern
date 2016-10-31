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
package org.lanternpowered.server.data.util;

import static org.spongepowered.api.data.DataQuery.of;

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.util.annotation.NonnullByDefault;

public final class DataQueries {

    private DataQueries() {
    }

    // General DataQueries
    public static final DataQuery UNSAFE_NBT = of("UnsafeData");
    public static final DataQuery DATA_MANIPULATORS = of("Data");
    public static final DataQuery DATA_CLASS = of("DataClass");
    public static final DataQuery INTERNAL_DATA = of("ManipulatorData");

    // Snapshots
    public static final DataQuery SNAPSHOT_WORLD_POSITION = of("Position");

    // Blocks
    public static final DataQuery BLOCK_STATE = of("BlockState");
    public static final DataQuery BLOCK_TYPE = of("BlockType");
    public static final DataQuery BLOCK_STATE_UNSAFE_META = of("UnsafeMeta");

    // Sponge data
    public static final DataQuery SPONGE_DATA = of("SpongeData");
    // Extended sponge data - Only used to separate player data and player sponge data files
    public static final DataQuery EXTENDED_SPONGE_DATA = of("ExtendedSpongeData");
    // Forge data
    public static final DataQuery FORGE_DATA = of("ForgeData");
    // Custom data manipulators
    public static final DataQuery CUSTOM_MANIPULATORS = of("CustomManipulators");

    // Potions
    public static final DataQuery POTION_TYPE = of("PotionType");
    public static final DataQuery POTION_AMPLIFIER = of("Amplifier");
    public static final DataQuery POTION_SHOWS_PARTICLES = of("ShowsParticles");
    public static final DataQuery POTION_AMBIANCE = of("Ambiance");
    public static final DataQuery POTION_DURATION = of("Duration");

    // TileEntity
    public static final DataQuery X_POS = of("x");
    public static final DataQuery Y_POS = of("y");
    public static final DataQuery Z_POS = of("z");
    public static final DataQuery W_POS = of("w");

    // Java API Queries for DataTranslators
    public static final DataQuery LOCAL_TIME_HOUR = of("LocalTimeHour");
    public static final DataQuery LOCAL_TIME_MINUTE = of("LocalTimeMinute");
    public static final DataQuery LOCAL_TIME_SECOND = of("LocalTimeSecond");
    public static final DataQuery LOCAL_TIME_NANO = of("LocalTimeNano");
    public static final DataQuery LOCAL_DATE_YEAR = of("LocalDateYear");
    public static final DataQuery LOCAL_DATE_MONTH = of("LocalDateMonth");
    public static final DataQuery LOCAL_DATE_DAY = of("LocalDateDay");
    public static final DataQuery ZONE_TIME_ID = of("ZoneDateTimeId");

    // Particle Effects
    public static final DataQuery PARTICLE_TYPE = of("Type");
    public static final DataQuery PARTICLE_OPTIONS = of("Options");
    public static final DataQuery PARTICLE_OPTION_KEY = of("Option");
    public static final DataQuery PARTICLE_OPTION_VALUE = of("Value");

    // Firework Effects
    public static final DataQuery FIREWORK_SHAPE = of("Type");
    public static final DataQuery FIREWORK_COLORS = of("Colors");
    public static final DataQuery FIREWORK_FADE_COLORS = of("Fades");
    public static final DataQuery FIREWORK_TRAILS = of("Trails");
    public static final DataQuery FIREWORK_FLICKERS = of("Flickers");
}
