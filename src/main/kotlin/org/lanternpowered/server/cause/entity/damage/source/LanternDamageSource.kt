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
package org.lanternpowered.server.cause.entity.damage.source

import org.spongepowered.api.event.cause.entity.damage.source.common.AbstractDamageSource
import org.spongepowered.api.event.cause.entity.damage.source.common.AbstractDamageSourceBuilder

internal open class LanternDamageSource(builder: AbstractDamageSourceBuilder<*, *>) : AbstractDamageSource(builder) {
    override fun isFire(): Boolean = false // TODO
}
