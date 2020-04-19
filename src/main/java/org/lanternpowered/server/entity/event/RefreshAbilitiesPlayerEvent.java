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

public final class RefreshAbilitiesPlayerEvent implements EntityEvent {

    public static RefreshAbilitiesPlayerEvent of() {
        return INSTANCE;
    }

    private static final RefreshAbilitiesPlayerEvent INSTANCE = new RefreshAbilitiesPlayerEvent();

    private RefreshAbilitiesPlayerEvent() {
    }

    @Override
    public EntityEventType type() {
        return EntityEventType.DEATH_OR_ALIVE;
    }
}
