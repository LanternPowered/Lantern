package org.lanternpowered.server.network.vanilla.message.type.play;

import java.util.Locale;

import org.lanternpowered.server.network.message.Message;

public class MessagePlayInClientSettings implements Message {

    private final Locale locale;
    private final int viewDistance;

    public MessagePlayInClientSettings(Locale locale, int viewDistance, int chatMode, boolean colors, byte skinFlags) {
        this.viewDistance = viewDistance;
        this.locale = locale;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public int getViewDistance() {
        return this.viewDistance;
    }
}
