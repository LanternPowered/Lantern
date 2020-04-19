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
package org.lanternpowered.server.entity.event;

public final class DamagedEntityEvent implements EntityEvent {

    public static DamagedEntityEvent of() {
        return INSTANCE;
    }

    private static final DamagedEntityEvent INSTANCE = new DamagedEntityEvent();

    private DamagedEntityEvent() {
    }

    @Override
    public EntityEventType type() {
        return EntityEventType.ALIVE;
    }
}
