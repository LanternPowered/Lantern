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
