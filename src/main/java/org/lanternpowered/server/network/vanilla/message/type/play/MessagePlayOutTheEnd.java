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
import org.spongepowered.api.entity.living.complex.EnderDragon;
import org.spongepowered.api.entity.living.player.Player;

/**
 * This message will be send when a {@link Player} the
 * {@link EnderDragon} defeats. This will open the credits
 * or directly send a {@link MessagePlayInPerformRespawn}.
 */
public final class MessagePlayOutTheEnd implements Message {

    private final boolean playCredits;

    public MessagePlayOutTheEnd(boolean playCredits) {
        this.playCredits = playCredits;
    }

    public boolean shouldPlayCredits() {
        return this.playCredits;
    }
}
