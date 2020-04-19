/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.data;

import static org.spongepowered.api.data.persistence.DataQuery.of;

import org.spongepowered.api.data.persistence.DataQuery;

public final class DataQueries {

    private DataQueries() {
    }

    // General DataQueries
    public static final DataQuery DATA_MANIPULATORS = of("Data");
    public static final DataQuery DATA_VALUES = of("DataValues");

    public static final DataQuery MANIPULATOR_ID = of("ManipulatorId");
    public static final DataQuery MANIPULATOR_DATA = of("ManipulatorData");

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
    public static final DataQuery POTION_SHOWS_ICON = of("ShowsIcon");
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

    // Others
    public static final DataQuery ENTITY_TYPE = DataQuery.of("EntityType");
    public static final DataQuery TILE_ENTITY_TYPE = DataQuery.of("TileEntityType");

    public static final DataQuery ITEM_TYPE = DataQuery.of("ItemType");
    public static final DataQuery QUANTITY = DataQuery.of("Quantity");
    public static final DataQuery FLUID_TYPE = DataQuery.of("FluidType");
    public static final DataQuery VOLUME = DataQuery.of("Volume");

    public static final DataQuery POSITION = DataQuery.of("Position");
    public static final DataQuery ROTATION = DataQuery.of("Rotation");

    // GameProfile
    public static final DataQuery PROFILE_PROPERTIES = of("ProfileProperties");

    // ProfileProperty
    public static final DataQuery PROPERTY_NAME = of("PropertyName");
    public static final DataQuery PROPERTY_VALUE = of("PropertyValue");
    public static final DataQuery PROPERTY_SIGNATURE = of("PropertySignature");

    // User
    public static final DataQuery USER_UUID = of("UUID");
    public static final DataQuery USER_NAME = of("Name");
    public static final DataQuery USER_SPAWNS = of("Spawns");

}
