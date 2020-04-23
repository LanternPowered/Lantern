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

import io.netty.handler.codec.CodecException
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.buffer.contextual.ContextualValueType
import org.lanternpowered.server.network.message.codec.CodecContext

class NetworkRecipeContextualValueType : ContextualValueType<NetworkRecipe> {

    override fun write(ctx: CodecContext, recipe: NetworkRecipe, buf: ByteBuffer) {
        buf.writeString(recipe.id)
        buf.writeString(recipe.type)
        recipe.write(ctx, buf)
    }

    @Throws(CodecException::class)
    override fun read(ctx: CodecContext, buf: ByteBuffer): NetworkRecipe = throw UnsupportedOperationException()
}
