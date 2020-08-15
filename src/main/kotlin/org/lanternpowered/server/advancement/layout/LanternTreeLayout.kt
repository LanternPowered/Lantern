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
package org.lanternpowered.server.advancement.layout

import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.event.LanternEventFactory
import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.api.util.collections.immutableListBuilderOf
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.server.advancement.LanternAdvancement
import org.spongepowered.api.advancement.Advancement
import org.spongepowered.api.advancement.AdvancementTree
import org.spongepowered.api.advancement.TreeLayout
import org.spongepowered.api.advancement.TreeLayoutElement
import org.spongepowered.api.event.advancement.AdvancementTreeEvent
import java.util.Optional

class LanternTreeLayout(private val advancementTree: AdvancementTree) : TreeLayout {

    private val elements by lazy { collectElements(this.advancementTree.rootAdvancement) }

    override fun getTree(): AdvancementTree = this.advancementTree
    override fun getElements(): Collection<TreeLayoutElement> = this.elements

    override fun getElement(advancement: Advancement): Optional<TreeLayoutElement> {
        val tree = advancement.tree.orElse(null)
        if (tree != this.advancementTree)
            return emptyOptional()
        return (advancement as LanternAdvancement).layoutElement.asOptional()
    }

    private class TreeNode(
            // The layout element
            val layout: TreeLayoutElement,
            // The column index
            val x: Double
    ) {

        // The children nodes
        var children = mutableListOf<TreeNode>()

        // The previous node
        var previous: TreeNode? = null

        // The next node
        var next: TreeNode? = null

        // The parent node
        var parent: TreeNode? = null

        // The offset
        var offset = 0.0

    }

    /**
     * Generates the default layout and calls the
     * [AdvancementTreeEvent.GenerateLayout] event.
     */
    fun generate() {
        // Generate a simple layout
        val rootNode = generateNode(this.advancementTree.rootAdvancement)
        firstProcess(rootNode)
        secondProcess(rootNode)

        // TODO: Improve the tree to look more like vanilla, "compacter"

        // Call the event for post processing
        val cause = CauseStack.current().currentCause
        val event = LanternEventFactory.createAdvancementTreeEventGenerateLayout(
                cause, this, this.advancementTree)
        EventManager.post(event)
    }

    override fun toString(): String = ToStringHelper(this)
            .add("advancementTree", this.advancementTree.key)
            .toString()

    companion object {

        private const val HORIZONTAL_OFFSET = 1.0
        private const val VERTICAL_OFFSET = 1.0

        private fun collectElements(advancement: Advancement): List<TreeLayoutElement> =
                immutableListBuilderOf<TreeLayoutElement>()
                        .also { builder -> this.collectElements(advancement) { builder.add(it) } }
                        .build()

        private fun collectElements(advancement: Advancement, elements: (TreeLayoutElement) -> Unit) {
            val element = (advancement as LanternAdvancement).layoutElement
            if (element != null)
                elements(element)
            for (child in advancement.getChildren())
                this.collectElements(child, elements)
        }

        private fun generateNode(rootAdvancement: Advancement): TreeNode {
            return this.generateNode(rootAdvancement, (rootAdvancement as LanternAdvancement).layoutElement!!, 0)
        }

        private fun generateNode(advancement: Advancement, element: TreeLayoutElement, column: Int): TreeNode {
            val treeNode = TreeNode(element, column.toDouble() * HORIZONTAL_OFFSET)
            var previous: TreeNode? = null
            for (child in advancement.children) {
                val childElement = (child as LanternAdvancement).layoutElement
                if (childElement != null) {
                    val childNode = this.generateNode(child, childElement, column + 1)
                    childNode.previous = previous
                    childNode.parent = treeNode
                    if (previous != null) {
                        previous.next = childNode
                    }
                    treeNode.children.add(childNode)
                    previous = childNode
                }
            }
            return treeNode
        }

        /**
         * The following layout will be generated in this process.
         *
         *       |-Q
         *   |-Q---Q
         *   |-Q
         *   |   |-Q
         *   |   |-Q
         * Q---Q---Q
         */
        private fun firstProcess(treeNode: TreeNode) {
            val parent = treeNode.parent
            if (parent != null)
                treeNode.offset = parent.offset
            for (childNode in treeNode.children)
                this.firstProcess(childNode)
            if (parent != null) {
                if (treeNode.children.isEmpty())
                    treeNode.offset += VERTICAL_OFFSET
                parent.offset = treeNode.offset
            }
            treeNode.layout.setPosition(treeNode.x, treeNode.offset)
        }

        /**
         * The following layout will be generated in this process.
         *
         *       |-Q
         *   |-Q-|-Q
         *   |-Q
         * Q-|   |-Q
         *   |-Q-|-Q
         *       |-Q
         */
        private fun secondProcess(treeNode: TreeNode) {
            if (treeNode.children.isEmpty())
                return
            // Process the children
            for (childNode in treeNode.children)
                this.secondProcess(childNode)
            var y = 0.0
            for (node in treeNode.children) {
                y += node.offset
            }
            y /= treeNode.children.size.toDouble()
            treeNode.offset = y
            treeNode.layout.setPosition(treeNode.x, y)
        }
    }
}
