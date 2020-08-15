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

import org.lanternpowered.api.text.Text
import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.api.util.collections.asUnmodifiableCollection
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.server.advancement.layout.LanternTreeLayoutElement
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.ResourceKey
import org.spongepowered.api.advancement.Advancement
import org.spongepowered.api.advancement.AdvancementTree
import org.spongepowered.api.advancement.DisplayInfo
import org.spongepowered.api.advancement.TreeLayoutElement
import org.spongepowered.api.advancement.criteria.AdvancementCriterion
import java.util.Optional

class LanternAdvancement internal constructor(
        key: ResourceKey,
        name: String,
        private val parent: Advancement?,
        private val displayInfo: DisplayInfo?,
        private val criterion: AdvancementCriterion
) : DefaultCatalogType.Named(key, name), Advancement {

    private var tree: AdvancementTree? = null
    private val children = mutableListOf<Advancement>()
    private val unmodifiableChildren = this.children.asUnmodifiableCollection()
    private var text: Text
    private val toast: List<Text>

    /**
     * The layout element attached to this advancement.
     */
    val layoutElement: TreeLayoutElement? =
            if (this.displayInfo == null) null else LanternTreeLayoutElement(this)

    /**
     * Criteria data that will be used to sync criteria with the client
     */
    @JvmField
    val clientCriteria: Pair<List<AdvancementCriterion>, Array<Array<String>>>

    init {
        (this.parent as? LanternAdvancement)?.children?.add(this)
        // Cache the client criteria
        this.clientCriteria = LanternPlayerAdvancements.createCriteria(this.criterion)
        this.toast = generateToast(this.name, this.displayInfo)
        this.text = generateTextRepresentation(this.name, this.displayInfo)
    }

    fun setTree(advancementTree: AdvancementTree?) {
        this.tree = advancementTree
    }

    override fun getTree(): Optional<AdvancementTree> = this.tree.asOptional()
    override fun getChildren(): Collection<Advancement> = this.unmodifiableChildren
    override fun getCriterion(): AdvancementCriterion = this.criterion
    override fun getParent(): Optional<Advancement> = this.parent.asOptional()
    override fun getDisplayInfo(): Optional<DisplayInfo> = this.displayInfo.asOptional()
    override fun toToastText(): List<Text> = this.toast
    override fun asComponent(): Text = this.text

    override fun toStringHelper(): ToStringHelper = super.toStringHelper()
            .add("tree", this.tree?.key)
            .add("parent", this.parent?.key)
            .add("displayInfo", this.displayInfo)
            .add("layoutElement", this.layoutElement)
            .add("criterion", this.criterion)
            .omitNullValues()
}
