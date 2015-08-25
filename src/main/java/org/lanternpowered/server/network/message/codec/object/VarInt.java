package org.lanternpowered.server.network.message.codec.object;

/**
 * A variable int representation of a int value.
 */
public class VarInt {

    private final int value;

    private VarInt(int value) {
        this.value = value;
    }

    /**
     * Gets the int value.
     * 
     * @return the value
     */
    public int value() {
        return this.value;
    }

    /**
     * Creates a new {@link VarInt} object for the specified int value.
     * 
     * @param value the value
     * @return the object
     */
    public static VarInt of(int value) {
        return new VarInt(value);
    }

}
