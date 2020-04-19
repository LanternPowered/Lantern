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
import org.spongepowered.api.effect.potion.PotionEffectType;

public final class MessagePlayOutRemovePotionEffect implements Message {

    private final int entityId;
    private final PotionEffectType potionEffectType;

    public MessagePlayOutRemovePotionEffect(int entityId, PotionEffectType potionEffectType) {
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
