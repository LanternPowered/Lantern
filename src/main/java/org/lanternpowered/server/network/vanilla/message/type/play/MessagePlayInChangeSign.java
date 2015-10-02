package org.lanternpowered.server.network.vanilla.message.type.play;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

import org.lanternpowered.server.network.message.Message;

import com.flowpowered.math.vector.Vector3i;

public final class MessagePlayInChangeSign implements Message {

    private final Vector3i position;
    private final String[] lines;

    public MessagePlayInChangeSign(Vector3i position, String[] lines) {
        this.position = checkNotNull(position, "position");
        checkNotNull(lines, "lines");
        checkArgument(lines.length == 4, "lines length must be 4");
        this.lines = lines;
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
     * Gets the lines of the sign.
     * 
     * @return the lines
     */
    public String[] getLines() {
        return this.lines;
    }
}
