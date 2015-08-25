package org.lanternpowered.server.network.vanilla.message.type.play.internal;

import org.lanternpowered.server.network.message.Message;

public final class MessagePlayInPlayerAction implements Message {

    private final int action;
    private final int value;

    public MessagePlayInPlayerAction(int action, int value) {
        this.action = action;
        this.value = value;
    }

    public int getAction() {
        return this.action;
    }

    public int getValue() {
        return this.value;
    }

}
