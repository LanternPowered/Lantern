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
import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.advancement.AdvancementType;
import org.spongepowered.api.advancement.AdvancementTypes;
import org.spongepowered.api.advancement.DisplayInfo;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

public final class LanternDisplayInfoBuilder implements DisplayInfo.Builder {

    AdvancementType advancementType;
    Text description;
    Text title;
    ItemStackSnapshot icon;
    boolean showToast;
    boolean announceToChat;
    boolean hidden;

    public LanternDisplayInfoBuilder() {
        reset();
    }

    @Override
    public DisplayInfo.Builder type(AdvancementType advancementType) {
        checkNotNull(advancementType, "advancementType");
        this.advancementType = advancementType;
        return this;
    }

    @Override
    public DisplayInfo.Builder description(Text description) {
        checkNotNull(description, "description");
        this.description = description;
        return this;
    }

    @Override
    public DisplayInfo.Builder title(Text title) {
        checkNotNull(title, "title");
        this.title = title;
        return this;
    }

    @Override
    public DisplayInfo.Builder icon(ItemStackSnapshot itemStackSnapshot) {
        checkNotNull(itemStackSnapshot, "itemStackSnapshot");
        this.icon = itemStackSnapshot;
        return this;
    }

    @Override
    public DisplayInfo.Builder showToast(boolean showToast) {
        this.showToast = showToast;
        return this;
    }

    @Override
    public DisplayInfo.Builder announceToChat(boolean announceToChat) {
        this.announceToChat = announceToChat;
        return this;
    }

    @Override
    public DisplayInfo.Builder hidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    @Override
    public DisplayInfo build() {
        checkState(this.title != null, "Title has not been set");
        checkState(this.icon != null, "Icon has not been set");
        return new LanternDisplayInfo(this);
    }

    @Override
    public DisplayInfo.Builder from(DisplayInfo value) {
        this.icon = value.getIcon();
        this.description = value.getDescription();
        this.advancementType = value.getType();
        this.announceToChat = value.doesAnnounceToChat();
        this.hidden = value.isHidden();
        this.showToast = value.doesShowToast();
        this.title = value.getTitle();
        return this;
    }

    @Override
    public DisplayInfo.Builder reset() {
        this.icon = null;
        this.description = Text.EMPTY;
        this.advancementType = AdvancementTypes.TASK;
        this.announceToChat = true;
        this.hidden = false;
        this.showToast = true;
        this.title = null;
        return this;
    }
}
