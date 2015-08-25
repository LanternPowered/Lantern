package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;

public class MessagePlayOutPlayerAbilities implements Message {

    private final boolean flying;
    private final boolean canFly;
    private final float flySpeed;
    private final float fieldOfView;

    public MessagePlayOutPlayerAbilities(boolean flying, boolean canFly, float flySpeed, float fieldOfView) {
        this.fieldOfView = fieldOfView;
        this.flySpeed = flySpeed;
        this.flying = flying;
        this.canFly = canFly;
    }

    public boolean isFlying() {
        return this.flying;
    }

    public boolean canFly() {
        return this.canFly;
    }

    public float getFlySpeed() {
        return this.flySpeed;
    }

    public float getFieldOfView() {
        return this.fieldOfView;
    }

}
