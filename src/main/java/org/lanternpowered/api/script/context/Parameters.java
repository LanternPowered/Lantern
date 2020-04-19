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
package org.lanternpowered.api.script.context;

import org.lanternpowered.api.world.World;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;
import org.spongepowered.api.world.Location;

@SuppressWarnings("unchecked")
public final class Parameters {

    public static final Parameter<Location> TARGET_LOCATION = DummyObjectProvider.createFor(Parameter.class, "TARGET_LOCATION");

    public static final Parameter<World> WORLD = DummyObjectProvider.createFor(Parameter.class, "WORLD");

    private Parameters() {
    }
}
