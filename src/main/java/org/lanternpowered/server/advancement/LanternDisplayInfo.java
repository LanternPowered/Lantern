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

import com.google.common.base.MoreObjects;
import org.spongepowered.api.advancement.AdvancementType;
import org.spongepowered.api.advancement.DisplayInfo;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

public final class LanternDisplayInfo implements DisplayInfo {

    private final Text title;
    private final Text description;
    private final ItemStackSnapshot icon;
    private final AdvancementType type;
    private final boolean showToast;
    private final boolean announceToChat;
    private final boolean hidden;

    LanternDisplayInfo(LanternDisplayInfoBuilder builder) {
        this.announceToChat = builder.announceToChat;
        this.description = builder.description;
        this.type = builder.advancementType;
        this.showToast = builder.showToast;
        this.hidden = builder.hidden;
        this.title = builder.title;
        this.icon = builder.icon;
    }

    @Override
    public AdvancementType getType() {
        return this.type;
    }

    @Override
    public Text getDescription() {
        return this.description;
    }

    @Override
    public ItemStackSnapshot getIcon() {
        return this.icon;
    }

    @Override
    public Text getTitle() {
        return this.title;
    }

    @Override
    public boolean doesShowToast() {
        return this.showToast;
    }

    @Override
    public boolean doesAnnounceToChat() {
        return this.announceToChat;
    }

    @Override
    public boolean isHidden() {
        return this.hidden;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LanternDisplayInfo)) {
            return false;
        }
        final LanternDisplayInfo that = (LanternDisplayInfo) obj;
        return this.type == that.type && this.title.equals(that.title) &&
                this.description.equals(that.description) && this.icon.equals(that.icon) &&
                this.showToast == that.showToast && this.announceToChat == that.announceToChat &&
                this.hidden == that.hidden;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", this.type)
                .add("title", this.title)
                .add("description", this.description)
                .add("icon", this.icon)
                .add("showToast", this.type)
                .add("announceToChat", this.announceToChat)
                .add("hidden", this.hidden)
                .toString();
    }
}
