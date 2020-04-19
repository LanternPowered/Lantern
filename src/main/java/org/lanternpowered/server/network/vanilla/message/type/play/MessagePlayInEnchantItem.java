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

public final class MessagePlayInEnchantItem implements Message {

    private final int windowId;
    private final int enchantmentSlot;

    public MessagePlayInEnchantItem(int windowId, int enchantmentSlot) {
        this.enchantmentSlot = enchantmentSlot;
        this.windowId = windowId;
    }

    public int getEnchantmentSlot() {
        return this.enchantmentSlot;
    }

    public int getWindowId() {
        return this.windowId;
    }
}
