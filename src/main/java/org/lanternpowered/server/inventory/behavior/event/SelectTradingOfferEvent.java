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
package org.lanternpowered.server.inventory.behavior.event;

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.inventory.client.TradingClientContainer;

/**
 * Is thrown when the selected offer in the
 * {@link TradingClientContainer} is changed.
 */
public final class SelectTradingOfferEvent implements ContainerEvent {

    private final int index;

    public SelectTradingOfferEvent(int index) {
        this.index = index;
    }

    /**
     * The selected offer index.
     *
     * @return The index
     */
    public int getIndex() {
        return this.index;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("index", this.index)
                .toString();
    }
}
