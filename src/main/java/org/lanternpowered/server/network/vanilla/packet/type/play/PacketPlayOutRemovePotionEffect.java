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

public final class PacketPlayOutRemovePotionEffect implements Packet {

    private final int entityId;
    private final PotionEffectType potionEffectType;

    public PacketPlayOutRemovePotionEffect(int entityId, PotionEffectType potionEffectType) {
        this.potionEffectType = potionEffectType;
        this.entityId = entityId;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public PotionEffectType getType() {
        return this.potionEffectType;
    }
}
