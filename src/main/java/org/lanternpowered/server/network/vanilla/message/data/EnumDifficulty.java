package org.lanternpowered.server.network.vanilla.message.data;

public enum EnumDifficulty {
    PEACEFUL        (0),
    EASY            (1),
    NORMAL          (2),
    HARD            (3);

    private final byte id;

    EnumDifficulty(int id) {
        this.id = (byte) id;
    }

    /**
     * Gets the id of the difficulty.
     * 
     * @return the id
     */
    public byte getId() {
        return this.id;
    }

}
