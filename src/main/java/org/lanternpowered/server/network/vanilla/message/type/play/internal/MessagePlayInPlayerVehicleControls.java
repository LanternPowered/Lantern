package org.lanternpowered.server.network.vanilla.message.type.play.internal;

import org.lanternpowered.server.network.message.Message;

public final class MessagePlayInPlayerVehicleControls implements Message {

    private final float sideways;
    private final float forwards;
    private final boolean jumping;
    private final boolean sneaking;

    public MessagePlayInPlayerVehicleControls(float forwards, float sideways, boolean jumping, boolean sneaking) {
        this.sideways = sideways;
        this.forwards = forwards;
        this.sneaking = sneaking;
        this.jumping = jumping;
    }

    public float getForwards() {
        return this.forwards;
    }

    public float getSideways() {
        return this.sideways;
    }

    public boolean isJumping() {
        return this.jumping;
    }

    public boolean isSneaking() {
        return this.sneaking;
    }
}
