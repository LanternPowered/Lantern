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
package org.lanternpowered.server.advancement

import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.ResourceKey
import org.spongepowered.api.advancement.Advancement
import org.spongepowered.api.advancement.AdvancementTree

class LanternAdvancementTree internal constructor(
        key: ResourceKey,
        name: String,
        private val rootAdvancement: Advancement,
        private val background: String
) : DefaultCatalogType.Named(key, name), AdvancementTree {

    init {
        applyTree(this.rootAdvancement, this)
    }

    override fun getRootAdvancement(): Advancement = this.rootAdvancement
    override fun getBackgroundPath(): String = this.background

    override fun toStringHelper(): ToStringHelper = super.toStringHelper()
            .add("rootAdvancement", this.rootAdvancement.key)
            .add("background", this.background)

    companion object {

        private fun applyTree(advancement: Advancement, tree: AdvancementTree) {
            (advancement as LanternAdvancement).setTree(tree)
            for (child in advancement.children)
                this.applyTree(child, tree)
        }
    }
}
