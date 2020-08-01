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
package org.lanternpowered.server.registry.type.entity

import org.lanternpowered.api.entity.Entity
import org.lanternpowered.api.entity.EntityType
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.api.key.spongeKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.server.entity.EntityCreationData
import org.lanternpowered.server.entity.LanternHuman
import org.lanternpowered.server.entity.LanternItem
import org.lanternpowered.server.entity.LanternZombie
import org.lanternpowered.server.entity.entityTypeOf
import org.lanternpowered.server.entity.weather.LanternLightningBolt

val EntityTypeRegistry = catalogTypeRegistry<EntityType<*>> {
    fun <E : Entity> register(key: NamespacedKey, translationKey: String, constructor: (EntityCreationData) -> E) =
            register(entityTypeOf(key, translatableTextOf(translationKey), constructor))

    register(spongeKey("human"), "entity.human.name", ::LanternHuman)

    register(minecraftKey("zombie"), "entity.zombie.name", ::LanternZombie)
    register(minecraftKey("item"), "entity.item.name", ::LanternItem)
    register(minecraftKey("lightning_bolt"), "entity.lightning.name", ::LanternLightningBolt)

    register(minecraftKey("player"), "entity.player.name") {
        throw UnsupportedOperationException("You cannot construct a Player.")
    }
}
