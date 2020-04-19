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
package org.lanternpowered.server.block;

import org.lanternpowered.api.util.Named;
import org.lanternpowered.server.block.property.FlammableInfo;
import org.spongepowered.api.block.BlockSoundGroup;
import org.spongepowered.api.data.property.Property;
import org.spongepowered.api.data.type.InstrumentType;
import org.spongepowered.api.data.type.Matter;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

@SuppressWarnings("ALL")
public final class BlockProperties {

    public static final Property<BlockSoundGroup> BLOCK_SOUND_GROUP =
            DummyObjectProvider.createFor(Property.class, "BLOCK_SOUND_GROUP");

    public static final Property<Boolean> IS_SOLID_CUBE =
            DummyObjectProvider.createFor(Property.class, "IS_SOLID_CUBE");

    public static final Property<Boolean> IS_SOLID_SIDE =
            DummyObjectProvider.createFor(Property.class, "IS_SOLID_SIDE");

    public static final Property<Boolean> IS_PASSABLE =
            DummyObjectProvider.createFor(Property.class, "IS_PASSABLE");

    public static final Property<Boolean> HAS_FULL_BLOCK_SELECTION_BOX =
            DummyObjectProvider.createFor(Property.class, "HAS_FULL_BLOCK_SELECTION_BOX");

    public static final Property<Matter> MATTER =
            DummyObjectProvider.createFor(Property.class, "MATTER");

    public static final Property<Double> HARDNESS =
            DummyObjectProvider.createFor(Property.class, "HARDNESS");

    public static final Property<Double> BLAST_RESISTANCE =
            DummyObjectProvider.createFor(Property.class, "BLAST_RESISTANCE");

    public static final Property<Boolean> IS_UNBREAKABLE =
            DummyObjectProvider.createFor(Property.class, "IS_UNBREAKABLE");

    public static final Property<Boolean> IS_FLAMMABLE =
            DummyObjectProvider.createFor(Property.class, "IS_FLAMMABLE");

    public static final Property<Double> LIGHT_EMISSION =
            DummyObjectProvider.createFor(Property.class, "LIGHT_EMISSION");

    @Named("IS_REPLACEABLE_BLOCK")
    public static final Property<Boolean> IS_REPLACEABLE =
            DummyObjectProvider.createFor(Property.class, "IS_REPLACEABLE");

    public static final Property<Boolean> IS_SOLID_MATERIAL =
            DummyObjectProvider.createFor(Property.class, "IS_SOLID_MATERIAL");

    public static final Property<Boolean> IS_GRAVITY_AFFECTED =
            DummyObjectProvider.createFor(Property.class, "IS_GRAVITY_AFFECTED");

    public static final Property<Boolean> HAS_STATISTICS_TRACKING =
            DummyObjectProvider.createFor(Property.class, "HAS_STATISTICS_TRACKING");

    @Named("IS_SURROGATE_BLOCK")
    public static final Property<Boolean> IS_SURROGATE =
            DummyObjectProvider.createFor(Property.class, "IS_SURROGATE");

    public static final Property<FlammableInfo> FLAMMABLE_INFO =
            DummyObjectProvider.createFor(Property.class, "FLAMMABLE_INFO");

    public static final Property<InstrumentType> REPRESENTED_INSTRUMENT =
            DummyObjectProvider.createFor(Property.class, "REPRESENTED_INSTRUMENT");

    private BlockProperties() {
    }
}
