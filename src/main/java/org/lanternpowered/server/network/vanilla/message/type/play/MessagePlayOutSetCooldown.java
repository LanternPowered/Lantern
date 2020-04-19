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
import org.spongepowered.api.item.ItemType;

public final class MessagePlayOutSetCooldown implements Message {

    private final ItemType itemType;
    private final int cooldownTicks;

    public MessagePlayOutSetCooldown(ItemType itemType, int cooldownTicks) {
        this.cooldownTicks = cooldownTicks;
        this.itemType = itemType;
    }

    public ItemType getItemType() {
        return this.itemType;
    }

    public int getCooldownTicks() {
        return this.cooldownTicks;
    }
}
