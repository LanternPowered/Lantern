/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;

public class MessagePlayOutPlayerAbilities implements Message {

    private final boolean flying;
    private final boolean canFly;
    private final float flySpeed;
    private final float fieldOfView;
    private final boolean invulnerable;
    private final boolean creative;

    public MessagePlayOutPlayerAbilities(boolean flying, boolean canFly, boolean invulnerable,
            boolean creative, float flySpeed, float fieldOfView) {
        this.fieldOfView = fieldOfView;
        this.flySpeed = flySpeed;
        this.flying = flying;
        this.canFly = canFly;
        this.invulnerable = invulnerable;
        this.creative = creative;
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

    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    public boolean isCreative() {
        return this.creative;
    }
}
