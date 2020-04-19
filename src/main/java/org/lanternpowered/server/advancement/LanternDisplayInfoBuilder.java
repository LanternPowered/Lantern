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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.advancement.AdvancementType;
import org.spongepowered.api.advancement.AdvancementTypes;
import org.spongepowered.api.advancement.DisplayInfo;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

@SuppressWarnings({"NullableProblems", "ConstantConditions"})
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
        this.description = Text.empty();
        this.advancementType = AdvancementTypes.TASK;
        this.announceToChat = true;
        this.hidden = false;
        this.showToast = true;
        this.title = null;
        return this;
    }
}
