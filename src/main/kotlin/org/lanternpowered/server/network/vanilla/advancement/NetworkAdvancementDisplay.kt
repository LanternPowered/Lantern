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
package org.lanternpowered.server.network.vanilla.advancement

import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.registry.type.advancement.AdvancementTypeRegistry
import org.spongepowered.api.advancement.AdvancementType
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.text.Text
import org.spongepowered.math.vector.Vector2d

class NetworkAdvancementDisplay(
        private val title: Text,
        private val description: Text,
        private val icon: ItemStackSnapshot,
        private val frameType: AdvancementType,
        private val background: String?,
        private val position: Vector2d,
        private val showToast: Boolean,
        private val hidden: Boolean
) {

    fun write(ctx: CodecContext, buf: ByteBuffer) {
        ctx.write(buf, ContextualValueTypes.TEXT, this.title)
        ctx.write(buf, ContextualValueTypes.TEXT, this.description)
        ctx.write(buf, ContextualValueTypes.ITEM_STACK, this.icon.createStack())
        buf.writeVarInt(AdvancementTypeRegistry.getId(this.frameType))
        var flags = 0
        if (this.background != null)
            flags += 0x1
        if (this.showToast)
            flags += 0x2
        if (this.hidden)
            flags += 0x4
        buf.writeInt(flags)
        if (this.background != null)
            buf.writeString(this.background)
        buf.writeFloat(this.position.x.toFloat())
        buf.writeFloat(this.position.y.toFloat())
    }

    override fun toString(): String = ToStringHelper(this)
            .omitNullValues()
            .add("icon", this.icon)
            .add("title", this.title)
            .add("description", this.description)
            .add("type", this.frameType.key.toString())
            .add("background", this.background)
            .add("showToast", this.showToast)
            .add("hidden", this.hidden)
            .add("position", this.position)
            .toString()

}
