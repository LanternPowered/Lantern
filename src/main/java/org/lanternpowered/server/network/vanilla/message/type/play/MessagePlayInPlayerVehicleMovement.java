package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;

/**
 * This is the only message that we will use to modify the controls
 * of the player. More info will come as I write the implementation.
 */
public final class MessagePlayInPlayerVehicleMovement implements Message {

    private final float sideways;
    private final float forwards;

    public MessagePlayInPlayerVehicleMovement(float forwards, float sideways) {
        this.sideways = sideways;
        this.forwards = forwards;
    }

    /**
     * Gets the forwards value. (Positive is forwards, negative is backwards.)
     * 
     * @return the forwards value
     */
    public float getForwards() {
        return this.forwards;
    }

    /**
     * Gets the sideways value. (Positive is left, negative is right.)
     * 
     * @return the sideways value
     */
    public float getSideways() {
        return this.sideways;
    }

}
