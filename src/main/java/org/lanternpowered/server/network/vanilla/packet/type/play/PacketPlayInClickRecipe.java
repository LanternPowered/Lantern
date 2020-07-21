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

public final class PacketPlayInClickRecipe implements Packet {

    private final int windowId;
    private final String recipeId;
    private final boolean shift;

    public PacketPlayInClickRecipe(int windowId, String recipeId, boolean shift) {
        this.windowId = windowId;
        this.recipeId = recipeId;
        this.shift = shift;
    }

    public int getWindowId() {
        return this.windowId;
    }

    public String getRecipeId() {
        return this.recipeId;
    }

    public boolean hasShift() {
        return this.shift;
    }
}
