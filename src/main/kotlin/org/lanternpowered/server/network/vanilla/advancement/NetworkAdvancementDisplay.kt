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

import org.lanternpowered.api.item.inventory.stack.asStack
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.value.ContextualValueCodec
import org.lanternpowered.server.network.item.NetworkItemStack
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.text.NetworkText
import org.lanternpowered.server.registry.type.advancement.AdvancementTypeRegistry
import org.spongepowered.api.advancement.AdvancementType
import org.spongepowered.api.item.inventory.ItemStackSnapshot
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

    companion object : ContextualValueCodec<NetworkAdvancementDisplay> {

        override fun write(ctx: CodecContext, buf: ByteBuffer, value: NetworkAdvancementDisplay) {
            NetworkText.write(ctx, buf, value.title)
            NetworkText.write(ctx, buf, value.description)
            NetworkItemStack.write(ctx, buf, value.icon.asStack())
            buf.writeVarInt(AdvancementTypeRegistry.getId(value.frameType))
            var flags = 0
            if (value.background != null)
                flags += 0x1
            if (value.showToast)
                flags += 0x2
            if (value.hidden)
                flags += 0x4
            buf.writeInt(flags)
            if (value.background != null)
                buf.writeString(value.background)
            buf.writeFloat(value.position.x.toFloat())
            buf.writeFloat(value.position.y.toFloat())
        }

        override fun read(ctx: CodecContext, buf: ByteBuffer): NetworkAdvancementDisplay = throw UnsupportedOperationException()
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
