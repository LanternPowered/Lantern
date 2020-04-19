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

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;

import java.util.function.Supplier;

public final class SwingHandEntityEvent implements EntityEvent {

    public static SwingHandEntityEvent of(Supplier<? extends HandType> handType) {
        return of(handType.get());
    }

    public static SwingHandEntityEvent of(HandType handType) {
        checkNotNull(handType, "handType");
        return handType == HandTypes.MAIN_HAND.get() ? Holder.MAIN_HAND :
                handType == HandTypes.OFF_HAND.get() ? Holder.OFF_HAND : new SwingHandEntityEvent(handType);
    }

    private final HandType handType;

    private SwingHandEntityEvent(HandType handType) {
        this.handType = handType;
    }

    public HandType getHandType() {
        return this.handType;
    }

    @Override
    public EntityEventType type() {
        return EntityEventType.ALIVE;
    }

    @Override
    public String toString() {
        return toStringHelper(this).add("handType", this.handType.getKey()).toString();
    }

    private static class Holder {
        static final SwingHandEntityEvent MAIN_HAND = new SwingHandEntityEvent(HandTypes.MAIN_HAND.get());
        static final SwingHandEntityEvent OFF_HAND = new SwingHandEntityEvent(HandTypes.OFF_HAND.get());
    }
}
