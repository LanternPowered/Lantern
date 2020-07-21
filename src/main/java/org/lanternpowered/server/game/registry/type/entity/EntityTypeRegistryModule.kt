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
package org.lanternpowered.server.game.registry.type.entity

import org.lanternpowered.api.ResourceKeys.minecraft
import org.lanternpowered.api.ResourceKeys.sponge
import org.lanternpowered.server.entity.LanternHuman
import org.lanternpowered.server.entity.LanternItem
import org.lanternpowered.server.entity.LanternZombie
import org.lanternpowered.server.entity.entityTypeOf
import org.lanternpowered.server.entity.living.player.LanternPlayer
import org.lanternpowered.server.entity.living.player.OfflineUser
import org.lanternpowered.server.entity.weather.LanternLightning
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule
import org.lanternpowered.server.game.registry.type.effect.sound.entity.EntityEffectTypeRegistryModule
import org.lanternpowered.server.game.registry.type.item.inventory.InventoryArchetypeRegistryModule
import org.lanternpowered.server.network.entity.EntityProtocolTypeRegistryModule
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.EntityType
import org.spongepowered.api.entity.EntityTypes
import org.spongepowered.api.registry.util.RegistrationDependency
import java.util.HashMap

@RegistrationDependency(
        EntityEffectTypeRegistryModule::class,
        EntityProtocolTypeRegistryModule::class,
        ValueKeyRegistryModule::class,
        InventoryArchetypeRegistryModule::class
)
object EntityTypeRegistryModule : AdditionalPluginCatalogRegistryModule<EntityType<*>>(EntityTypes::class) {

    private val entityTypeByClass = HashMap<Class<*>, EntityType<*>>()

    override fun doRegistration(catalogType: EntityType<*>, disallowInbuiltPluginIds: Boolean) {
        check(catalogType.entityClass !in this.entityTypeByClass) {
            "There is already a EntityType registered for the class: ${catalogType.entityClass.name}"
        }
        super.doRegistration(catalogType, disallowInbuiltPluginIds)
        this.entityTypeByClass[catalogType.entityClass] = catalogType
    }

    fun getByClass(entityClass: Class<out Entity>) = this.entityTypeByClass[entityClass].optional()

    override fun registerDefaults() {
        register(entityTypeOf(sponge("human"), "entity.human.name", ::LanternHuman))

        register(entityTypeOf(minecraft("zombie"), "entity.zombie.name", ::LanternZombie))
        register(entityTypeOf(minecraft("item"), "entity.item.name", ::LanternItem))
        register(entityTypeOf(minecraft("lightning"), "entity.lightning.name", ::LanternLightning))

        // Cannot be constructed
        register(entityTypeOf<LanternPlayer>(minecraft("player"), "entity.player.name") {
            throw UnsupportedOperationException("You cannot construct a Player.")
        })
        register(entityTypeOf<OfflineUser>(minecraft("offline_user"), "entity.player.name") {
            throw UnsupportedOperationException("You cannot construct a Offline User.")
        })
    }
}
