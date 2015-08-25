package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;

public final class MessagePlayInPlayerSneak implements Message {

    private final boolean sneaking;

    public MessagePlayInPlayerSneak(boolean sneaking) {
        this.sneaking = sneaking;
    }

    public boolean isSprinting() {
        return this.sneaking;
    }

}
