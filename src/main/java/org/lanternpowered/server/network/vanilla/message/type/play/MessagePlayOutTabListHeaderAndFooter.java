package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.text.Text;

public final class MessagePlayOutTabListHeaderAndFooter implements Message {

    private final Text header;
    private final Text footer;

    public MessagePlayOutTabListHeaderAndFooter(Text header, Text footer) {
        this.header = header;
        this.footer = footer;
    }

    public Text getHeader() {
        return this.header;
    }

    public Text getFooter() {
        return this.footer;
    }
}
