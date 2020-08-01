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

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.text.Text
import org.spongepowered.api.entity.Entity

fun <E : Entity> entityTypeOf(key: NamespacedKey, translation: Text, entityConstructor: (EntityCreationData) -> E) =
        LanternEntityType(key, translation, entityConstructor)
