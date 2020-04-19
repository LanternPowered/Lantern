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

// This is the most useless message ever, it just spams for 20 times
// and stops. It is only send when the recipe book is opened.
public final class MessagePlayInDisplayedRecipe implements Message {

    private final String recipeId;

    public MessagePlayInDisplayedRecipe(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipeId() {
        return this.recipeId;
    }
}
