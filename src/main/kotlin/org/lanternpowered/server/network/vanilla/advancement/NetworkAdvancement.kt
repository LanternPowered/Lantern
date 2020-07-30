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
import org.lanternpowered.server.network.packet.codec.CodecContext

class NetworkAdvancement(
        private val id: String,
        private val parentId: String?,
        private val display: NetworkAdvancementDisplay?,
        private val criteria: Collection<String>,
        private val requirements: Array<Array<String>>
) {

    fun write(ctx: CodecContext, buf: ByteBuffer) {
        buf.writeString(this.id)
        buf.writeBoolean(this.parentId != null)
        if (this.parentId != null)
            buf.writeString(this.parentId)
        buf.writeBoolean(this.display != null)
        this.display?.write(ctx, buf)
        buf.writeVarInt(this.criteria.size)
        this.criteria.forEach { criterion -> buf.writeString(criterion) }
        buf.writeVarInt(this.requirements.size)
        for (requirements in this.requirements) {
            buf.writeVarInt(requirements.size)
            for (requirement in requirements)
                buf.writeString(requirement)
        }
    }

    override fun toString(): String = ToStringHelper(this)
            .omitNullValues()
            .add("id", this.id)
            .add("parentId", this.parentId)
            .add("display", this.display)
            .add("criteria", this.criteria.contentToString())
            .toString()
}
