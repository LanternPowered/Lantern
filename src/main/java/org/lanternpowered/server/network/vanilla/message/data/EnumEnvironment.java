package org.lanternpowered.server.network.vanilla.message.data;

public enum EnumEnvironment {
    OVERWORLD   ( 0),
    NETHER      (-1),
    END         ( 1);

    private final byte id;

    EnumEnvironment(int id) {
        this.id = (byte) id;
    }

    /**
     * Gets the id of the environment.
     * 
     * @return the id
     */
    public byte getId() {
        return this.id;
    }

}
