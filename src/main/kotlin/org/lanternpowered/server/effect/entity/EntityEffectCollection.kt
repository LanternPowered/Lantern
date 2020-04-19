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