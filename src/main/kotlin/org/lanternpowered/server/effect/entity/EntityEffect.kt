/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.effect.entity

import org.lanternpowered.server.entity.LanternEntity
import org.spongepowered.api.entity.Entity

private val emptyEntityEffect = entityEffect {}

/**
 * Gets an empty entity effect.
 */
fun emptyEntityEffect(): EntityEffect = emptyEntityEffect

/**
 * Constructs a new [EntityEffect].
 */
inline fun entityEffect(crossinline block: (entity: LanternEntity) -> Unit): EntityEffect {
    return object : EntityEffect {
        override fun play(entity: LanternEntity) {
            block(entity)
        }
    }
}

/**
 * Plays this [EntityEffect] for the given [Entity].
 *
 * @param entity The entity
 */
fun EntityEffect.play(entity: Entity) = play(entity as LanternEntity)

/**
 * Represents an effect of an entity.
 */
interface EntityEffect {

    /**
     * Plays this [EntityEffect] for the given [LanternEntity].
     *
     * @param entity The entity
     */
    fun play(entity: LanternEntity)
}
