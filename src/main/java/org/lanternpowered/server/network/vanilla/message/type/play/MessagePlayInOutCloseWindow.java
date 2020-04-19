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

public class MessagePlayInOutCloseWindow implements Message {

    private final int window;

    public MessagePlayInOutCloseWindow(int window) {
        this.window = window;
    }

    /**
     * Gets the window id.
     * 
     * @return the window id
     */
    public int getWindow() {
        return this.window;
    }
}
