package org.lanternpowered.server.network.message.codec.object;

/**
 * A variable long representation of a long value.
 */
public class VarLong {

    private final long value;

    private VarLong(long value) {
        this.value = value;
    }

    /**
     * Gets the long value.
     * 
     * @return the value
     */
    public long value() {
        return this.value;
    }

    /**
     * Creates a new {@link VarLong} object for the specified long value.
     * 
     * @param value the value
     * @return the object
     */
    public static VarLong of(long value) {
        return new VarLong(value);
    }

}
