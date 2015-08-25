package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;

public final class MessagePlayOutPlayerHealthUpdate implements Message {

    private final float health;
    private final float food;
    private final float saturation;

    public MessagePlayOutPlayerHealthUpdate(float health, float food, float saturation) {
        this.saturation = saturation;
        this.health = health;
        this.food = food;
    }

    public float getHealth() {
        return this.health;
    }

    public float getFood() {
        return this.food;
    }

    public float getSaturation() {
        return this.saturation;
    }
}
