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
package org.lanternpowered.server.network.vanilla.command.argument

import org.lanternpowered.server.network.buffer.ByteBuffer

inline fun <A : NumberArgument<N>, N : Number> numberArgumentTypeOf(id: String, crossinline writeNumber: ByteBuffer.(N) -> Unit) =
        argumentTypeOf(id) { buf, argument: A ->
            val min = argument.min
            val max = argument.max
            var flags = 0
            if (min != null)
                flags += 0x1
            if (max != null)
                flags += 0x2
            buf.writeByte(flags.toByte())
            if (min != null)
                buf.writeNumber(min)
            if (max != null)
                buf.writeNumber(max)
        }

/**
 * Creates a new [ArgumentType] with the given id and codec function.
 */
inline fun <A : Argument> argumentTypeOf(id: String, crossinline fn: (buf: ByteBuffer, A) -> Unit): ArgumentType<A> =
        argumentTypeOf(id, object : ArgumentCodec<A> {
            override fun encode(buf: ByteBuffer, argument: A) = fn(buf, argument)
        })

/**
 * Creates a new [ArgumentType] with the given id.
 *
 * @param id The id
 * @return The argument type
 */
fun argumentTypeOf(id: String): ArgumentType<EmptyArgument> = ArgumentType(id, EmptyArgumentCodec)

/**
 * Creates a new [ArgumentType] with the given id and codec.
 *
 * @param id The id
 * @return The argument type
 */
fun <A : Argument> argumentTypeOf(id: String, codec: ArgumentCodec<A>): ArgumentType<A> = ArgumentType(id, codec)

class ArgumentType<A : Argument> internal constructor(
        val id: String,
        val codec: ArgumentCodec<A>
)
