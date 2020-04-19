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
package org.lanternpowered.server.game.registry.type.data;

import org.lanternpowered.server.data.type.LanternRailDirection;
import org.lanternpowered.server.game.registry.EnumValueRegistryModule;
import org.spongepowered.api.data.type.RailDirection;
import org.spongepowered.api.data.type.RailDirections;

public class RailDirectionRegistryModule extends EnumValueRegistryModule<RailDirection> {

    private static final RailDirectionRegistryModule INSTANCE = new RailDirectionRegistryModule();

    public static RailDirectionRegistryModule get() {
        return INSTANCE;
    }

    private RailDirectionRegistryModule() {
        super(LanternRailDirection.class, RailDirections.class);
    }
}
