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

import static org.lanternpowered.server.text.translation.TranslationHelper.tr;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.advancement.layout.LanternTreeLayoutElement;
import org.lanternpowered.server.catalog.PluginCatalogType;
import org.lanternpowered.server.event.CauseStack;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.AdvancementTree;
import org.spongepowered.api.advancement.AdvancementType;
import org.spongepowered.api.advancement.DisplayInfo;
import org.spongepowered.api.advancement.TreeLayoutElement;
import org.spongepowered.api.advancement.criteria.AdvancementCriterion;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextFormat;
import org.spongepowered.api.util.Tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

public class LanternAdvancement extends PluginCatalogType.Base implements Advancement {

    @Nullable private AdvancementTree advancementTree;
    @Nullable private final Advancement parent;
    @Nullable private final DisplayInfo displayInfo;
    @Nullable private final TreeLayoutElement layoutElement;
    private final AdvancementCriterion criterion;
    private final List<Advancement> children = new ArrayList<>();
    private final List<Advancement> unmodifiableChildren = Collections.unmodifiableList(this.children);

    private final Text text;
    private final List<Text> toast;

    // Criteria data that will be used to sync criteria with the client
    final Tuple<List<AdvancementCriterion>, String[][]> clientCriteria;

    LanternAdvancement(LanternAdvancementBuilder builder) {
        super(CauseStack.current().first(PluginContainer.class).get().getId(), builder.id,
                builder.name == null ? (builder.displayInfo != null ?
                        builder.displayInfo.getTitle().toPlain() : builder.id) : builder.name);
        this.layoutElement = builder.displayInfo == null ? null : new LanternTreeLayoutElement(this);
        this.displayInfo = builder.displayInfo;
        this.criterion = builder.criterion;
        this.parent = builder.parent;
        if (this.parent != null) {
            ((LanternAdvancement) this.parent).children.add(this);
        }
        // Cache the client criteria
        this.clientCriteria = LanternPlayerAdvancements.createCriteria(builder.criterion);
        final ImmutableList.Builder<Text> toastBuilder = ImmutableList.builder();
        if (builder.displayInfo == null) {
            this.text = Text.of(getName());
            toastBuilder.add(Text.of("Achieved: ", this.text));
        } else {
            final AdvancementType type = builder.displayInfo.getType();
            final TextFormat format = type.getTextFormat();
            toastBuilder.add(Text.builder(tr("advancements.toast." + type.getName().toLowerCase()))
                    .format(format).build());
            final Text title = builder.displayInfo.getTitle();
            toastBuilder.add(title);
            final Text description = builder.displayInfo.getDescription();
            final Text.Builder hoverBuilder = Text.builder()
                    .append(title.toBuilder().format(format).build());
            if (!description.isEmpty()) {
                hoverBuilder.append(Text.NEW_LINE, description);
            }
            this.text = Text.builder()
                    .append(Text.of("["))
                    .append(Text.of(title.toBuilder().onHover(TextActions.showText(hoverBuilder.build()))))
                    .append(Text.of("]"))
                    .format(format)
                    .build();
        }
        this.toast = toastBuilder.build();
    }

    void setTree(AdvancementTree advancementTree) {
        this.advancementTree = advancementTree;
    }

    @Override
    public Optional<AdvancementTree> getTree() {
        return Optional.ofNullable(this.advancementTree);
    }

    @Override
    public Collection<Advancement> getChildren() {
        return this.unmodifiableChildren;
    }

    @Override
    public AdvancementCriterion getCriterion() {
        return this.criterion;
    }

    @Override
    public Optional<Advancement> getParent() {
        return Optional.ofNullable(this.parent);
    }

    @Override
    public Optional<DisplayInfo> getDisplayInfo() {
        return Optional.ofNullable(this.displayInfo);
    }

    @Override
    public List<Text> toToastText() {
        return this.toast;
    }

    @Override
    public Text toText() {
        return this.text;
    }

    @Nullable
    public TreeLayoutElement getLayoutElement() {
        return this.layoutElement;
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("tree", this.advancementTree == null ? null : this.advancementTree.getId())
                .add("parent", this.parent == null ? null : this.parent.getId())
                .add("displayInfo", this.displayInfo)
                .add("layoutElement", this.layoutElement)
                .add("criterion", this.criterion)
                .omitNullValues();
    }
}
