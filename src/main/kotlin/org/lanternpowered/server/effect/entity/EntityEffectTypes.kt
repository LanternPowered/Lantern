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

import org.lanternpowered.api.registry.CatalogRegistry
import org.lanternpowered.api.registry.provide

object EntityEffectTypes {

    /**
     * The [EntityEffectType] that is played when a entity gets angry. Is
     * in vanilla minecraft only used [Horse]s.
     */
    val ANGRY: EntityEffectType by CatalogRegistry.provide("ANGRY")

    /**
     * The [EntityEffectType] that is played when an entity dies.
     */
    val DEATH: EntityEffectType by CatalogRegistry.provide("DEATH")

    /**
     * The [EntityEffectType] that is played when a entity falls and hits the ground.
     *
     * This is a special [EntityEffectType], the fall height will be available in
     * the current [CauseStack] when the [EntityEffect] is being played.
     */
    val FALL: EntityEffectType by CatalogRegistry.provide("FALL")

    /**
     * The [EntityEffectType] that is played when a entity takes damage.
     */
    val HURT: EntityEffectType by CatalogRegistry.provide("HURT")

    /**
     * The [EntityEffectType] that is played when a entity is looking/wandering around.
     */
    val IDLE: EntityEffectType by CatalogRegistry.provide("IDLE")

    /**
     * The [EntityEffectType] that is played when lightning strikes the ground.
     */
    val LIGHTNING: EntityEffectType by CatalogRegistry.provide("LIGHTNING")

    /**
     * The [EntityEffectType] that is played when a entity is merged. For
     * example dropped items which merge into one item.
     */
    val MERGE: EntityEffectType by CatalogRegistry.provide("MERGE")

    /**
     * The [EntityEffectType] that is played when a entity falls into the water.
     */
    val SPLASH: EntityEffectType by CatalogRegistry.provide("SPLASH")

    /**
     * The [EntityEffectType] that is played when a entity is swimming.
     */
    val SWIM: EntityEffectType by CatalogRegistry.provide("SWIM")

    /**
     * The [EntityEffectType] that is played when a villager says no.
     */
    val VILLAGER_NO: EntityEffectType by CatalogRegistry.provide("VILLAGER_NO")

    /**
     * The [EntityEffectType] that is played when a villager says yes.
     */
    val VILLAGER_YES: EntityEffectType by CatalogRegistry.provide("VILLAGER_YES")

    /**
     * The [EntityEffectType] that is played when a entity is walking.
     */
    val WALK: EntityEffectType by CatalogRegistry.provide("WALK")
}
