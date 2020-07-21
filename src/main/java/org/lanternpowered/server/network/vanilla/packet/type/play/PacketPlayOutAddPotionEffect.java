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
package org.lanternpowered.server.network.vanilla.packet.type.play;

import org.lanternpowered.server.network.packet.Packet;
import org.spongepowered.api.effect.potion.PotionEffectType;

public final class PacketPlayOutAddPotionEffect implements Packet {

    private final int entityId;

    private final PotionEffectType potionEffectType;
    private final int duration;
    private final int amplifier;
    private final boolean ambient;
    private final boolean showParticles;

    public PacketPlayOutAddPotionEffect(int entityId, PotionEffectType potionEffectType, int duration, int amplifier,
            boolean ambient, boolean showParticles) {
        this.potionEffectType = potionEffectType;
        this.entityId = entityId;
        this.duration = duration;
        this.amplifier = amplifier;
        this.ambient = ambient;
        this.showParticles = showParticles;
    }

    public PotionEffectType getType() {
        return this.potionEffectType;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getAmplifier() {
        return this.amplifier;
    }

    public boolean isAmbient() {
        return this.ambient;
    }

    public boolean getShowParticles() {
        return this.showParticles;
    }
}
