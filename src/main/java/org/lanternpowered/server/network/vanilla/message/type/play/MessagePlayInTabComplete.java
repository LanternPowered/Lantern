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

public final class MessagePlayInTabComplete implements Message {

    private final String input;
    private final int id;

    public MessagePlayInTabComplete(String input, int id) {
        this.input = input;
        this.id = id;
    }

    public String getInput() {
        return this.input;
    }

    public int getId() {
        return this.id;
    }
}
