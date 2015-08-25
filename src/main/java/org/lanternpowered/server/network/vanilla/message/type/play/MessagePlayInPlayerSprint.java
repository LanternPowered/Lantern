package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;

public final class MessagePlayInPlayerSprint implements Message {

    private final boolean sprinting;

    public MessagePlayInPlayerSprint(boolean sprinting) {
        this.sprinting = sprinting;
    }

    public boolean isSprinting() {
        return this.sprinting;
    }

}
