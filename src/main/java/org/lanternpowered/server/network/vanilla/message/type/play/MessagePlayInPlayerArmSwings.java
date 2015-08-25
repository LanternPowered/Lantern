package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.entity.player.PlayerHand;
import org.lanternpowered.server.network.message.Message;

public final class MessagePlayInPlayerArmSwings implements Message {

    private final PlayerHand hand;

    public MessagePlayInPlayerArmSwings(PlayerHand hand) {
        this.hand = hand;
    }

    public PlayerHand getHand() {
        return this.hand;
    }

}
