package org.lanternpowered.server.network.vanilla.message.type.play;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.message.Message;

import com.flowpowered.math.vector.Vector3i;

public final class MessagePlayOutUpdateSign implements Message {

    private final Vector3i position;
    private final String[] lines;

    /**
     * Creates the update sign message.
     * 
     * @param position the position
     * @param lines the lines in the json format
     */
    public MessagePlayOutUpdateSign(Vector3i position, String[] lines) {
        this.position = checkNotNull(position, "position");
        this.lines = checkNotNull(lines, "lines");
    }

    /**
     * Gets the sign position of this message.
     * 
     * @return the position
     */
    public Vector3i getPosition() {
        return this.position;
    }

    /**
     * Gets the lines.
     * 
     * @return the lines
     */
    public String[] getLines() {
        return this.lines;
    }

}
