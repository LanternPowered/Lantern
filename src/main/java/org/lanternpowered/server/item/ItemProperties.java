package org.lanternpowered.server.item;

import org.spongepowered.api.data.property.Property;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

@SuppressWarnings("unchecked")
public final class ItemProperties {

    public static final Property<Boolean> IS_ALWAYS_CONSUMABLE =
            DummyObjectProvider.createFor(Property.class, "IS_ALWAYS_CONSUMABLE");

    public static final Property<Integer> USE_COOLDOWN =
            DummyObjectProvider.createFor(Property.class, "USE_COOLDOWN");

    public static final Property<Boolean> IS_DUAL_WIELDABLE =
            DummyObjectProvider.createFor(Property.class, "IS_DUAL_WIELDABLE");

    public static final Property<Double> HEALTH_RESTORATION =
            DummyObjectProvider.createFor(Property.class, "HEALTH_RESTORATION");

    public static final Property<Integer> MAXIMUM_USE_DURATION =
            DummyObjectProvider.createFor(Property.class, "MAXIMUM_USE_DURATION");

    public static final Property<Integer> MINIMUM_USE_DURATION =
            DummyObjectProvider.createFor(Property.class, "MINIMUM_USE_DURATION");

    private ItemProperties() {
    }
}
