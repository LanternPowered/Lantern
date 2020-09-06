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
package org.lanternpowered.server.util.netty

import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener

inline fun ChannelFuture.addChannelFutureListener(crossinline listener: (ChannelFuture) -> Unit): ChannelFuture {
    @Suppress("RedundantSamConstructor") // Nope, not redundant
    return this.addListener(ChannelFutureListener { future ->
        listener(future)
    })
}
