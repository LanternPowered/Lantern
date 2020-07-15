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
package org.lanternpowered.server.entity

import org.spongepowered.api.ResourceKey
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.text.translation.Translation
import java.util.UUID

fun <E : Entity> entityTypeOf(key: ResourceKey, translation: Translation, entityClass: Class<E>, entityConstructor: (UUID) -> E) =
        LanternEntityType(key, translation, entityClass, entityConstructor)

fun <E : Entity> entityTypeOf(key: ResourceKey, translation: String, entityClass: Class<E>, entityConstructor: (UUID) -> E) =
        LanternEntityType(key, translation, entityClass, entityConstructor)

inline fun <reified E : Entity> entityTypeOf(key: ResourceKey, translation: Translation, noinline entityConstructor: (UUID) -> E) =
        entityTypeOf(key, translation, E::class.java, entityConstructor)

inline fun <reified E : Entity> entityTypeOf(key: ResourceKey, translation: String, noinline entityConstructor: (UUID) -> E) =
        entityTypeOf(key, translation, E::class.java, entityConstructor)
