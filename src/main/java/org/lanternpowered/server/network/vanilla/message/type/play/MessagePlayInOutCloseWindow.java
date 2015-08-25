package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;

public class MessagePlayInOutCloseWindow implements Message {

    private final int window;

    public MessagePlayInOutCloseWindow(int window) {
        this.window = window;
    }

    /**
     * Gets the window id.
     * 
     * @return the window id
     */
    public int getWindow() {
        return this.window;
    }
}
