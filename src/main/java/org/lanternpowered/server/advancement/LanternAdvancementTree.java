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
package org.lanternpowered.server.advancement;

import org.lanternpowered.api.util.ToStringHelper;
import org.lanternpowered.server.catalog.DefaultCatalogType;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.AdvancementTree;
import org.spongepowered.api.text.translation.Translation;

public class LanternAdvancementTree extends DefaultCatalogType.Named implements AdvancementTree {

    private final Advancement rootAdvancement;
    private final String background;

    LanternAdvancementTree(ResourceKey key, Translation name, Advancement rootAdvancement, String background) {
        super(key, name);
        this.rootAdvancement = rootAdvancement;
        this.background = background;
        applyTree(rootAdvancement, this);
    }

    private static void applyTree(Advancement advancement, AdvancementTree tree) {
        ((LanternAdvancement) advancement).setTree(tree);
        for (Advancement child : advancement.getChildren()) {
            applyTree(child, tree);
        }
    }

    @Override
    public Advancement getRootAdvancement() {
        return this.rootAdvancement;
    }

    @Override
    public String getBackgroundPath() {
        return this.background;
    }

    @Override
    public ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("rootAdvancement", this.rootAdvancement.getKey())
                .add("background", this.background);
    }
}
