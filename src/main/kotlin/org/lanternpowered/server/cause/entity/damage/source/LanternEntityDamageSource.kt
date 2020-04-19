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

import org.spongepowered.api.entity.Entity
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource

internal open class LanternEntityDamageSource(builder: AbstractEntityDamageSourceBuilder<*, *>) : LanternDamageSource(builder), EntityDamageSource {

    private val source: Entity = builder.source!!

    override fun getSource(): Entity = this.source
}
