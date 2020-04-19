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

import java.util.UUID;

public final class MessagePlayInSpectate implements Message {

    private final UUID targetPlayer;

    public MessagePlayInSpectate(UUID targetPlayer) {
        this.targetPlayer = targetPlayer;
    }

    public UUID getTargetPlayer() {
        return this.targetPlayer;
    }
}
