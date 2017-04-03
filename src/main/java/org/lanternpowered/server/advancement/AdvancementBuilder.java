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

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.ResettableBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public final class AdvancementBuilder implements ResettableBuilder<Advancement, AdvancementBuilder> {

    private final List<List<AdvancementCriterion>> criteria = new ArrayList<>();

    private Text title;
    private ItemType icon;
    private AdvancementFrameType frameType;
    @Nullable private Advancement parent;

    AdvancementBuilder() {
        reset();
    }

    /**
     * Sets the parent {@link Advancement} of the advancement.
     *
     * @param parent The parent
     * @return This builder, for chaining
     */
    public AdvancementBuilder parent(Advancement parent) {
        this.parent = checkNotNull(parent, "parent");
        return this;
    }

    /**
     * Sets the title of the advancement.
     *
     * @param title The title
     * @return This builder, for chaining
     */
    public AdvancementBuilder title(Text title) {
        this.title = checkNotNull(title, "title");
        return this;
    }

    /**
     * Sets the {@link AdvancementFrameType} of the advancement.
     *
     * @param frameType The frame type
     * @return This builder, for chaining
     */
    public AdvancementBuilder frameType(AdvancementFrameType frameType) {
        this.frameType = checkNotNull(frameType, "frameType");
        return this;
    }

    /**
     * Sets the icon of the advancement with the
     * specified {@link ItemType}.
     *
     * @param itemType The item type
     * @return This builder, for chaining
     */
    public AdvancementBuilder icon(ItemType itemType) {
        this.icon = checkNotNull(itemType, "itemType");
        return this;
    }

    /**
     * Sets the icon of the advancement with the
     * specified {@link ItemType}.
     *
     * @param blockType The block type
     * @return This builder, for chaining
     */
    public AdvancementBuilder icon(BlockType blockType) {
        this.icon = checkNotNull(blockType, "blockType").getItem().orElseThrow(
                () -> new IllegalArgumentException("The block type: " + blockType.getId() + " doesn't have a item type."));
        return this;
    }

    /**
     * Builds a new {@link Advancement}.
     *
     * @param pluginId The plugin id
     * @param id The id
     * @return The advancement
     */
    public Advancement build(String pluginId, String id) {
        checkNotNull(pluginId, "pluginId");
        checkNotNull(id, "id");
        //noinspection ConstantConditions
        checkArgument(this.title != null, "The title must be set");
        return new Advancement(pluginId, id, id, this.parent, this.criteria, this.title, this.icon, this.frameType);
    }

    @Override
    public AdvancementBuilder from(Advancement value) {
        this.parent = value.getParent().orElse(null);
        this.criteria.clear();
        this.criteria.addAll(value.getCriteria());
        this.icon = value.getIcon();
        this.frameType = value.getFrameType();
        this.title = value.getTitle();
        return this;
    }

    @Override
    public AdvancementBuilder reset() {
        this.parent = null;
        this.criteria.clear();
        this.icon = ItemTypes.APPLE;
        this.frameType = AdvancementFrameTypes.TASK;
        this.title = null;
        return this;
    }
}
