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
import org.spongepowered.api.data.type.HandType;

public final class MessagePlayInPlayerUseItem implements Message {

    private final HandType handType;

    public MessagePlayInPlayerUseItem(HandType handType) {
        this.handType = handType;
    }

    public HandType getHandType() {
        return this.handType;
    }
}
