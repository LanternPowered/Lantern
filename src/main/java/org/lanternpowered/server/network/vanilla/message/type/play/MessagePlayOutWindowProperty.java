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
package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;

public final class MessagePlayOutWindowProperty implements Message {

    private final int windowId;
    private final int property;
    private final int value;

    public MessagePlayOutWindowProperty(int windowId, int property, int value) {
        this.windowId = windowId;
        this.property = property;
        this.value = value;
    }

    public int getWindowId() {
        return this.windowId;
    }

    public int getProperty() {
        return this.property;
    }

    public int getValue() {
        return this.value;
    }
}
