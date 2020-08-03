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
package org.lanternpowered.server.network

import org.lanternpowered.server.network.packet.handler.Handler

/**
 * A annotation that can be applied to methods and
 * fields to mark them as only supported on Netty threads.
 *
 * It can also be used to define whether a specific or [Handler]
 * method should be handled on the netty thread.
 */
@Target(AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER,
        AnnotationTarget.FIELD,
        AnnotationTarget.PROPERTY
)
@Retention(AnnotationRetention.RUNTIME)
annotation class NettyThreadOnly
