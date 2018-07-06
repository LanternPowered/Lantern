/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.advancement.layout;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.advancement.LanternAdvancement;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.AdvancementTree;
import org.spongepowered.api.advancement.TreeLayout;
import org.spongepowered.api.advancement.TreeLayoutElement;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.advancement.AdvancementTreeEvent;
import org.spongepowered.api.event.cause.Cause;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
public class LanternTreeLayout implements TreeLayout {

    private final AdvancementTree advancementTree;
    @Nullable private List<TreeLayoutElement> elements;

    public LanternTreeLayout(AdvancementTree advancementTree) {
        this.advancementTree = advancementTree;
    }

    @Override
    public AdvancementTree getTree() {
        return this.advancementTree;
    }

    @Override
    public Collection<TreeLayoutElement> getElements() {
        if (this.elements == null) {
            final ImmutableList.Builder<TreeLayoutElement> builder = ImmutableList.builder();
            collectElements(this.advancementTree.getRootAdvancement(), builder);
            this.elements = builder.build();
        }
        return this.elements;
    }

    private static void collectElements(Advancement advancement, ImmutableList.Builder<TreeLayoutElement> elements) {
        final TreeLayoutElement element = ((LanternAdvancement) advancement).getLayoutElement();
        if (element != null) {
            elements.add(element);
        }
        for (Advancement child : advancement.getChildren()) {
            collectElements(child, elements);
        }
    }

    @Override
    public Optional<TreeLayoutElement> getElement(Advancement advancement) {
        final AdvancementTree tree = advancement.getTree().orElse(null);
        if (tree != this.advancementTree) {
            return Optional.empty();
        }
        return Optional.ofNullable(((LanternAdvancement) advancement).getLayoutElement());
    }

    private final static double HORIZONTAL_OFFSET = 1.0;
    private final static double VERTICAL_OFFSET = 1.0;

    private static final class TreeNode {

        // The layout element
        private final TreeLayoutElement layout;

        // The column index
        private final double x;

        // The children nodes
        private final List<TreeNode> children = new ArrayList<>();

        // The previous node
        private TreeNode previous;

        // The next node
        private TreeNode next;

        // The parent node
        private TreeNode parent;

        // The offset
        private double offset;

        private TreeNode(TreeLayoutElement layout, double x) {
            this.layout = layout;
            this.x = x;
        }
    }

    private static TreeNode generateNode(Advancement rootAdvancement) {
        return generateNode(rootAdvancement, ((LanternAdvancement) rootAdvancement).getLayoutElement(), 0);
    }

    private static TreeNode generateNode(Advancement advancement, TreeLayoutElement element, int column) {
        final TreeNode treeNode = new TreeNode(element, (double) column * HORIZONTAL_OFFSET);
        TreeNode previous = null;
        for (Advancement child : advancement.getChildren()) {
            final TreeLayoutElement childElement = ((LanternAdvancement) child).getLayoutElement();
            if (childElement != null) {
                final TreeNode childNode = generateNode(child, childElement, column + 1);
                childNode.previous = previous;
                childNode.parent = treeNode;
                if (previous != null) {
                    previous.next = childNode;
                }
                treeNode.children.add(childNode);
                previous = childNode;
            }
        }
        return treeNode;
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
    private static void firstProcess(TreeNode treeNode) {
        if (treeNode.parent != null) {
            treeNode.offset = treeNode.parent.offset;
        }
        for (TreeNode childNode : treeNode.children) {
            firstProcess(childNode);
        }
        if (treeNode.parent != null) {
            if (treeNode.children.isEmpty()) {
                treeNode.offset += VERTICAL_OFFSET;
            }
            treeNode.parent.offset = treeNode.offset;
        }
        treeNode.layout.setPosition(treeNode.x, treeNode.offset);
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
    private static void secondProcess(TreeNode treeNode) {
        if (treeNode.children.isEmpty()) {
            return;
        }
        // Process the children
        for (TreeNode childNode : treeNode.children) {
            secondProcess(childNode);
        }
        double y = 0;
        for (TreeNode node : treeNode.children) {
            y += node.offset;
        }
        y /= (double) treeNode.children.size();
        treeNode.offset = y;
        treeNode.layout.setPosition(treeNode.x, y);
    }

    /**
     * Generates the default layout and calls the
     * {@link AdvancementTreeEvent.GenerateLayout} event.
     */
    public void generate() {
        // Generate a simple layout
        final TreeNode rootNode = generateNode(this.advancementTree.getRootAdvancement());
        firstProcess(rootNode);
        secondProcess(rootNode);

        // TODO: Improve the tree to look more like vanilla, "compacter"

        // Call the event for post processing
        final Cause cause = CauseStack.current().getCurrentCause();
        final AdvancementTreeEvent.GenerateLayout event = SpongeEventFactory.createAdvancementTreeEventGenerateLayout(
                cause, this, this.advancementTree);
        Sponge.getEventManager().post(event);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("advancementTree", this.advancementTree.getKey())
                .toString();
    }
}
