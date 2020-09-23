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
import org.lanternpowered.api.util.collections.contentToString
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.value.ContextualValueCodec
import org.lanternpowered.server.network.packet.codec.CodecContext

class NetworkAdvancement(
        private val id: String,
        private val parentId: String?,
        private val display: NetworkAdvancementDisplay?,
        private val criteria: Collection<String>,
        private val requirements: Array<Array<String>>
) {

    companion object : ContextualValueCodec<NetworkAdvancement> {

        override fun write(ctx: CodecContext, buf: ByteBuffer, value: NetworkAdvancement) {
            buf.writeString(value.id)
            buf.writeBoolean(value.parentId != null)
            if (value.parentId != null)
                buf.writeString(value.parentId)
            buf.writeBoolean(value.display != null)
            if (value.display != null)
                NetworkAdvancementDisplay.write(ctx, buf, value.display)
            buf.writeVarInt(value.criteria.size)
            for (criterion in value.criteria)
                buf.writeString(criterion)
            buf.writeVarInt(value.requirements.size)
            for (requirements in value.requirements) {
                buf.writeVarInt(requirements.size)
                for (requirement in requirements)
                    buf.writeString(requirement)
            }
        }

        override fun read(ctx: CodecContext, buf: ByteBuffer): NetworkAdvancement = throw UnsupportedOperationException()
    }

    override fun toString(): String = ToStringHelper(this)
            .omitNullValues()
            .add("id", this.id)
            .add("parentId", this.parentId)
            .add("display", this.display)
            .add("criteria", this.criteria.contentToString())
            .toString()
}
