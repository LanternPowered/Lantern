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

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.entity.living.Living;

public final class CollectEntityEvent implements EntityEvent {

    private final Living collector;
    private final int collectedItemsCount;

    public CollectEntityEvent(Living collector, int collectedItemsCount) {
        this.collector = checkNotNull(collector, "collector");
        this.collectedItemsCount = collectedItemsCount;
    }

    public CollectEntityEvent(Living collector) {
        this(collector, Integer.MAX_VALUE);
    }

    public Living getCollector() {
        return this.collector;
    }

    public int getCollectedItemsCount() {
        return this.collectedItemsCount;
    }

    @Override
    public EntityEventType type() {
        return EntityEventType.DEATH_OR_ALIVE;
    }
}
