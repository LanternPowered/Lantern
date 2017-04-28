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
package org.lanternpowered.server.advancement;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.catalog.PluginCatalogType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.GuavaCollectors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

public final class Advancement extends Styleable {

    public static AdvancementBuilder builder() {
        return new AdvancementBuilder();
    }

    @Nullable private final Advancement parent;
    private final List<Advancement> children = new ArrayList<>();
    private final List<List<AdvancementCriterion>> advancementCriteria;
    private final List<AdvancementCriterion> advancementCriteria0;

    Advancement(String pluginId, String id, String name, @Nullable Advancement parent,
            List<List<AdvancementCriterion>> advancementCriteria, Text title, Text description, ItemStackSnapshot icon, FrameType frameType) {
        super(pluginId, id, name, title, description, icon, frameType);
        this.advancementCriteria = advancementCriteria.stream().map(ImmutableList::copyOf).collect(GuavaCollectors.toImmutableList());
        final ImmutableList.Builder<AdvancementCriterion> criteria = ImmutableList.builder();
        advancementCriteria.forEach(criteria::addAll);
        this.advancementCriteria0 = criteria.build();
        this.parent = parent;
    }

    /**
     * Adds the specified child {@link Advancement}.
     *
     * @param child The child advancement
     */
    void addChild(Advancement child) {
        checkNotNull(child, "child");
        checkArgument(child != this, "A advancement cannot be a child of itself");
        this.children.add(child);
    }

    /**
     * Gets all the children {@link Advancement}s.
     *
     * @return The children advancements
     */
    public List<Advancement> getChildren() {
        return ImmutableList.copyOf(this.children);
    }

    /**
     * Gets all the {@link AdvancementCriterion}s that should be achieved
     * before this advancement is unlocked.
     *
     * @return The criteria
     */
    public List<List<AdvancementCriterion>> getCriteria() {
        return this.advancementCriteria;
    }

    /**
     * Gets the parent {@link Advancement} if present.
     *
     * @return The parent advancement
     */
    public Optional<Advancement> getParent() {
        return Optional.ofNullable(this.parent);
    }

    List<AdvancementCriterion> getAdvancementCriteria0() {
        return this.advancementCriteria0;
    }
}
