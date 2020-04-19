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
import org.lanternpowered.server.inventory.client.EnchantmentTableClientContainer;

/**
 * Will be thrown when one of the buttons in the
 * {@link EnchantmentTableClientContainer} is pressed.
 */
public final class EnchantButtonEvent implements ContainerEvent {

    private final int button;

    public EnchantButtonEvent(int button) {
        this.button = button;
    }

    /**
     * Gets the pressed button. Can be 0, 1 or 2.
     *
     * @return The button
     */
    public int getButton() {
        return this.button;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("button", this.button)
                .toString();
    }
}
