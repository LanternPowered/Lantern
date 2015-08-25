package org.lanternpowered.server.network.vanilla.message.data;

public enum EnumGameMode {
    SURVIVAL        (0),
    CREATIVE        (1),
    ADVENTURE       (2),
    SPECTATOR       (3);

    private final byte id;

    EnumGameMode(int id) {
        this.id = (byte) id;
    }

    /**
     * Gets the id of the game mode.
     * 
     * @return the id
     */
    public byte getId() {
        return this.id;
    }

}
