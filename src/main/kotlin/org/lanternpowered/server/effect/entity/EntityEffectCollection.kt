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

import org.lanternpowered.api.registry.builderOf
import org.spongepowered.api.util.CopyableBuilder

/**
 * Gets an empty [EntityEffectCollection].
 */
fun entityEffectCollectionOf(): EntityEffectCollection {
    return builderOf<EntityEffectCollection.Builder>().build()
}

/**
 * Gets an empty [EntityEffectCollection].
 */
fun entityEffectCollectionOf(vararg pairs: Pair<EntityEffectType, EntityEffect>): EntityEffectCollection =
        builderOf<EntityEffectCollection.Builder>().addAll(mapOf(*pairs)).build()

/**
 * Creates a new [EntityEffectCollection].
 */
fun entityEffectCollection(block: EntityEffectCollection.Builder.() -> Unit): EntityEffectCollection =
        builderOf<EntityEffectCollection.Builder>().apply(block).build()

interface EntityEffectCollection {

    /**
     * Gets a collection of [EntityEffect]s for the given
     * [EntityEffectType].
     *
     * @param effectType The entity effect type
     * @return The entity effects
     */
    fun getAll(effectType: EntityEffectType): Collection<EntityEffect>

    /**
     * Attempts to get the combined [EntityEffect] for the given
     * [EntityEffectType]. This [EntityEffect] will trigger
     * all the [EntityEffect] that are registered for the given
     * type. Returns `null` if no effects are found.
     *
     * @param effectType The entity effect type
     * @return The entity effects
     */
    fun getCombined(effectType: EntityEffectType): EntityEffect?

    /**
     * Attempts to get the combined [EntityEffect] for the given
     * [EntityEffectType]. This [EntityEffect] will trigger
     * all the [EntityEffect] that are registered for the given
     * type. Returns an [emptyEntityEffect] if no effects could
     * be found.
     *
     * @param effectType The entity effect type
     * @return The entity effects
     */
    fun getCombinedOrEmpty(effectType: EntityEffectType): EntityEffect {
        return getCombined(effectType) ?: emptyEntityEffect()
    }

    /**
     * Adds a [EntityEffect] for the given [EntityEffectType].
     *
     * @param effectType The entity effect type
     * @param entityEffect The entity effect effect
     * @return Whether the effect was added
     */
    fun add(effectType: EntityEffectType, entityEffect: EntityEffect): Boolean

    /**
     * Removes a [EntityEffect] for the given [EntityEffectType].
     *
     * @param effectType The entity effect type
     * @param entityEffect The entity effect effect
     * @return Whether the effect was removed
     */
    fun remove(effectType: EntityEffectType, entityEffect: EntityEffect): Boolean

    /**
     * Creates a copy of this [EntityEffectCollection].
     *
     * @return The copy
     */
    fun copy(): EntityEffectCollection

    /**
     * Creates a new [Builder] from this [EntityEffectCollection].
     *
     * @return The builder
     */
    fun toBuilder(): Builder {
        return builderOf<Builder>().from(this)
    }

    /**
     * A builder to construct [EntityEffectCollection]s.
     */
    interface Builder : CopyableBuilder<EntityEffectCollection, Builder> {

        /**
         * Adds a [EntityEffect] for the given [EntityEffectType].
         *
         * @param effectType The entity effect type
         * @param entityEffect The entity effect effect
         * @return This builder, for chaining
         */
        fun add(effectType: EntityEffectType, entityEffect: EntityEffect): Builder

        /**
         * Adds a [EntityEffect] for the given [EntityEffectType].
         *
         * @param entries The entries
         * @return This builder, for chaining
         */
        fun addAll(entries: Map<EntityEffectType, EntityEffect>): Builder

        fun replaceOrAdd(effectType: EntityEffectType, effectToReplace: Class<out EntityEffect>, entityEffect: EntityEffect): Builder

        fun remove(effectType: EntityEffectType, effectToRemove: Class<out EntityEffect>): Builder {
            return removeIf(effectType) { effect -> effectToRemove.isInstance(effect) }
        }

        fun removeIf(effectType: EntityEffectType, predicate: (EntityEffect) -> Boolean): Builder

        fun reset(effectType: EntityEffectType): Builder

        /**
         * Builds a [EntityEffectCollection].
         *
         * @return The entity sound collection
         */
        fun build(): EntityEffectCollection
    }

    companion object {

        /**
         * Gets a new [Builder].
         */
        @JvmStatic
        fun builder(): Builder = builderOf()

        /**
         * Gets an empty [EntityEffectCollection].
         */
        @JvmStatic
        fun of(): EntityEffectCollection = builderOf<Builder>().build()
    }
}