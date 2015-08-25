package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;

public class MessagePlayOutSetReducedDebug implements Message {

    private final boolean reduced;

    public MessagePlayOutSetReducedDebug(boolean reduced) {
        this.reduced = reduced;
    }

    public boolean isReduced() {
        return this.reduced;
    }

}
