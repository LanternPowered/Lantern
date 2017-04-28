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

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.inventory.LanternItemStack;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.ResettableBuilder;

@SuppressWarnings("unchecked")
public abstract class StyleableBuilder<T extends Styleable, B extends StyleableBuilder<T, B>> implements ResettableBuilder<T, B> {

    Text title;
    Text description;
    ItemStackSnapshot icon;
    FrameType frameType;

    /**
     * Sets the description of the advancement.
     *
     * @param description The description
     * @return This builder, for chaining
     */
    public B description(Text description) {
        this.description = checkNotNull(description, "description");
        return (B) this;
    }

    /**
     * Sets the title of the advancement.
     *
     * @param title The title
     * @return This builder, for chaining
     */
    public B title(Text title) {
        this.title = checkNotNull(title, "title");
        return (B) this;
    }

    /**
     * Sets the {@link FrameType} of the advancement.
     *
     * @param frameType The frame type
     * @return This builder, for chaining
     */
    public B frameType(FrameType frameType) {
        this.frameType = checkNotNull(frameType, "frameType");
        return (B) this;
    }

    /**
     * Sets the icon of the advancement with the
     * specified {@link ItemType}.
     *
     * @param itemType The item type
     * @return This builder, for chaining
     */
    public B icon(ItemType itemType) {
        this.icon = new LanternItemStack(checkNotNull(itemType, "itemType")).createSnapshot();
        return (B) this;
    }

    /**
     * Sets the icon of the advancement with the
     * specified {@link ItemStack}.
     *
     * @param itemStack The item stack
     * @return This builder, for chaining
     */
    public B icon(ItemStack itemStack) {
        this.icon = checkNotNull(itemStack, "itemStack").createSnapshot();
        return (B) this;
    }

    /**
     * Sets the icon of the advancement with the
     * specified {@link ItemStackSnapshot}.
     *
     * @param itemStackSnapshot The item stack snapshot
     * @return This builder, for chaining
     */
    public B icon(ItemStackSnapshot itemStackSnapshot) {
        this.icon = checkNotNull(itemStackSnapshot, "itemStackSnapshot");
        return (B) this;
    }

    /**
     * Sets the icon of the advancement with the
     * specified {@link ItemType}.
     *
     * @param blockType The block type
     * @return This builder, for chaining
     */
    public B icon(BlockType blockType) {
        return icon(checkNotNull(blockType, "blockType").getItem().orElseThrow(
                () -> new IllegalArgumentException("The block type: " + blockType.getId() + " doesn't have a item type.")));
    }

    @Override
    public B from(T value) {
        this.icon = value.getIcon();
        this.frameType = value.getFrameType();
        this.title = value.getTitle();
        this.description = value.getDescription();
        return (B) this;
    }

    @Override
    public B reset() {
        this.icon = ItemStackSnapshot.NONE;
        this.frameType = FrameTypes.TASK;
        this.description = Text.EMPTY;
        this.title = null;
        return (B) this;
    }
}
