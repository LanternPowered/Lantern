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
import org.lanternpowered.server.catalog.AbstractNamedCatalogBuilder
import org.spongepowered.api.advancement.Advancement
import org.spongepowered.api.advancement.DisplayInfo
import org.spongepowered.api.advancement.criteria.AdvancementCriterion

class LanternAdvancementBuilder : AbstractNamedCatalogBuilder<Advancement, Advancement.Builder>(), Advancement.Builder {

    private var parent: Advancement? = null
    private var criterion: AdvancementCriterion = AdvancementCriterion.empty()
    private var displayInfo: DisplayInfo? = null

    override fun getFinalName(key: NamespacedKey): String {
        val name = this.name
        if (name != null)
            return name
        val displayInfo = this.displayInfo
        if (displayInfo != null)
            return displayInfo.title.toPlain()
        return key.value
    }

    override fun parent(parent: Advancement?): Advancement.Builder =
            this.apply { this.parent = parent }

    override fun criterion(criterion: AdvancementCriterion): Advancement.Builder =
            this.apply { this.criterion = criterion }

    override fun displayInfo(displayInfo: DisplayInfo?): Advancement.Builder =
            this.apply { this.displayInfo = displayInfo }

    override fun reset(): Advancement.Builder {
        this.criterion = AdvancementCriterion.empty()
        this.displayInfo = null
        this.parent = null
        return super.reset()
    }

    override fun build(key: NamespacedKey, name: String): Advancement =
            LanternAdvancement(key, name, this.parent, this.displayInfo, this.criterion)
}
