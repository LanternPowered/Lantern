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
package org.lanternpowered.server.advancement

import org.spongepowered.api.advancement.AdvancementType
import org.spongepowered.api.advancement.AdvancementTypes
import org.spongepowered.api.advancement.DisplayInfo
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.text.Text

class LanternDisplayInfoBuilder : DisplayInfo.Builder {

    private var type: AdvancementType = AdvancementTypes.TASK.get()
    private var description: Text = Text.empty()
    private var title: Text? = null
    private var icon: ItemStackSnapshot? = null
    private var showToast = false
    private var announceToChat = false
    private var hidden = false

    override fun type(advancementType: AdvancementType): DisplayInfo.Builder = apply { this.type = advancementType }
    override fun description(description: Text): DisplayInfo.Builder = apply { this.description = description }
    override fun title(title: Text): DisplayInfo.Builder = apply { this.title = title }
    override fun icon(itemStackSnapshot: ItemStackSnapshot): DisplayInfo.Builder = apply { this.icon = itemStackSnapshot }
    override fun showToast(showToast: Boolean): DisplayInfo.Builder = apply { this.showToast = showToast }
    override fun announceToChat(announceToChat: Boolean): DisplayInfo.Builder = apply { this.announceToChat = announceToChat }
    override fun hidden(hidden: Boolean): DisplayInfo.Builder = apply { this.hidden = hidden }

    override fun build(): DisplayInfo {
        val title = checkNotNull(this.title) { "Title has not been set" }
        val icon = checkNotNull(this.icon) { "Icon has not been set" }
        return LanternDisplayInfo(title, this.description, icon, this.type,
                this.showToast, this.announceToChat, this.hidden)
    }

    override fun from(value: DisplayInfo): DisplayInfo.Builder = apply {
        this.icon = value.icon
        this.description = value.description
        this.type = value.type
        this.announceToChat = value.doesAnnounceToChat()
        this.hidden = value.isHidden
        this.showToast = value.doesShowToast()
        this.title = value.title
    }

    override fun reset(): DisplayInfo.Builder = apply {
        this.icon = null
        this.description = Text.empty()
        this.type = AdvancementTypes.TASK.get()
        this.announceToChat = true
        this.hidden = false
        this.showToast = true
        this.title = null
    }
}

private data class LanternDisplayInfo internal constructor(
        private val title: Text,
        private val description: Text,
        private val icon: ItemStackSnapshot,
        private val type: AdvancementType,
        private val showToast: Boolean,
        private val announceToChat: Boolean,
        private val hidden: Boolean
) : DisplayInfo {
    override fun getType(): AdvancementType = this.type
    override fun getDescription(): Text = this.description
    override fun getIcon(): ItemStackSnapshot = this.icon
    override fun getTitle(): Text = this.title
    override fun doesShowToast(): Boolean = this.showToast
    override fun doesAnnounceToChat(): Boolean = this.announceToChat
    override fun isHidden(): Boolean = this.hidden
}
