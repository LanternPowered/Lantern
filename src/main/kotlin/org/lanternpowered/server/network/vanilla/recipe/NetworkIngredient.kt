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
package org.lanternpowered.server.network.vanilla.recipe

import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.item.NetworkItemStack
import org.lanternpowered.server.network.packet.codec.CodecContext

class NetworkIngredient(private val items: Collection<ItemStack>) {

    constructor(vararg items: ItemStack) : this(items.asList())

    fun write(ctx: CodecContext, buf: ByteBuffer) {
        buf.writeVarInt(this.items.size)
        for (itemStack in this.items)
            NetworkItemStack.write(ctx, buf, itemStack)
    }
}
