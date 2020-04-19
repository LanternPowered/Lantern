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
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.math.vector.Vector3d;

public class MessagePlayOutParticleEffect implements Message {

    private final Vector3d position;
    private final ParticleEffect particleEffect;

    public MessagePlayOutParticleEffect(Vector3d position, ParticleEffect particleEffect) {
        this.particleEffect = particleEffect;
        this.position = position;
    }

    public Vector3d getPosition() {
        return this.position;
    }

    public ParticleEffect getParticleEffect() {
        return this.particleEffect;
    }
}
