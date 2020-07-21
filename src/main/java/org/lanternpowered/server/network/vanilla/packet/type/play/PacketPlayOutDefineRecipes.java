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
import org.lanternpowered.server.network.vanilla.recipe.NetworkRecipe;

import java.util.List;

public final class PacketPlayOutDefineRecipes implements Packet {

    private final List<NetworkRecipe> recipes;

    public PacketPlayOutDefineRecipes(List<NetworkRecipe> recipes) {
        this.recipes = recipes;
    }

    public List<NetworkRecipe> getRecipes() {
        return this.recipes;
    }
}
