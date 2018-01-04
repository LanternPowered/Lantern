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
package org.lanternpowered.server.advancement.old;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.advancement.LanternAdvancementType;
import org.lanternpowered.server.catalog.PluginCatalogType;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

public final class Advancement extends PluginCatalogType.Base {

    public static AdvancementBuilder builder() {
        return new AdvancementBuilder();
    }

    @Nullable private final Advancement parent;
    private final List<Advancement> children = new ArrayList<>();
    private final AdvancementCriterion advancementCriterion;
    private final Set<AdvancementCriterion> leafCriteria;
    private final Set<AdvancementCriterion> criteria;

    Advancement(String pluginId, String id, String name, @Nullable Advancement parent, AdvancementCriterion advancementCriterion,
            Text title, Text description, ItemStackSnapshot icon, LanternAdvancementType frameType, boolean showToast) {
        super(pluginId, id, name, title, description, icon, frameType, showToast);
        this.advancementCriterion = advancementCriterion;
        this.leafCriteria = advancementCriterion.getLeafCriteria();
        this.criteria = advancementCriterion.getCriteria(false);
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
     * Gets all the {@link AdvancementCriterion} that should be achieved
     * before this advancement is unlocked.
     * <p>
     * This {@link AdvancementCriterion} can be a AND or OR operation that
     * contains multiple possible {@link AdvancementCriterion}s.
     *
     * @return The criterion
     */
    public AdvancementCriterion getCriterion() {
        return this.advancementCriterion;
    }

    /**
     * Gets the parent {@link Advancement} if present.
     *
     * @return The parent advancement
     */
    public Optional<Advancement> getParent() {
        return Optional.ofNullable(this.parent);
    }

    Set<AdvancementCriterion> getLeafCriteria() {
        return this.leafCriteria;
    }

    Set<AdvancementCriterion> getCriteria() {
        return this.criteria;
    }
}
