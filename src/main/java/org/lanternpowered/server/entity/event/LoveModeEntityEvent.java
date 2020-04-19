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

/**
 * Plays the love mode animation of the entity. Spawns
 * hearts above the entity.
 */
public final class LoveModeEntityEvent implements EntityEvent {

    public static LoveModeEntityEvent of() {
        return INSTANCE;
    }

    private static final LoveModeEntityEvent INSTANCE = new LoveModeEntityEvent();

    private LoveModeEntityEvent() {
    }

    @Override
    public EntityEventType type() {
        return EntityEventType.ALIVE;
    }
}
