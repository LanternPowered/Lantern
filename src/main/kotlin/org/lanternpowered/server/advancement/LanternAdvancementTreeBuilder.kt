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

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.text.toPlain
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.server.catalog.AbstractNamedCatalogBuilder
import org.lanternpowered.server.registry.type.advancement.AdvancementRegistry
import org.spongepowered.api.advancement.Advancement
import org.spongepowered.api.advancement.AdvancementTree

class LanternAdvancementTreeBuilder : AbstractNamedCatalogBuilder<AdvancementTree, AdvancementTree.Builder>(), AdvancementTree.Builder {

    companion object {

        const val DEFAULT_BACKGROUND = "minecraft:textures/gui/advancements/backgrounds/stone.png"
    }

    private var rootAdvancement: Advancement? = null
    private var background: String = DEFAULT_BACKGROUND

    override fun getFinalName(key: NamespacedKey): String {
        val name = this.name
        if (name != null)
            return name
        return this.rootAdvancement!!.displayInfo.orNull()?.title?.toPlain() ?: key.value
    }

    override fun rootAdvancement(rootAdvancement: Advancement): AdvancementTree.Builder = this.apply {
        val registry = AdvancementRegistry
        check(rootAdvancement == registry[rootAdvancement.key]) { "The root advancement must be registered." }
        check(!rootAdvancement.parent.isPresent) { "The root advancement cannot have a parent." }
        check(rootAdvancement.displayInfo.isPresent) { "The root advancement must have display info." }
        check(!rootAdvancement.tree.isPresent) { "The root advancement is already used by a different Advancement Tree." }
        this.rootAdvancement = rootAdvancement
    }

    override fun background(background: String): AdvancementTree.Builder =
            this.apply { this.background = background }

    override fun build(key: NamespacedKey, name: String): AdvancementTree {
        val rootAdvancement = checkNotNull(this.rootAdvancement) { "The root advancement must be set" }
        check(!rootAdvancement.tree.isPresent) { "The root advancement is already used by a different Advancement Tree." }
        return LanternAdvancementTree(key, name, rootAdvancement, this.background)
    }

    override fun reset(): AdvancementTree.Builder = this.apply {
        super.reset()
        this.background = DEFAULT_BACKGROUND
        this.rootAdvancement = null
    }
}
