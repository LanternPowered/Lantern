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
package org.lanternpowered.server.game.registry.type.entity

import org.lanternpowered.api.catalog.CatalogKeys.minecraft
import org.lanternpowered.api.catalog.CatalogKeys.sponge
import org.lanternpowered.api.ext.*
import org.lanternpowered.server.entity.LanternHuman
import org.lanternpowered.server.entity.LanternItem
import org.lanternpowered.server.entity.LanternZombie
import org.lanternpowered.server.entity.entityTypeOf
import org.lanternpowered.server.entity.living.player.LanternPlayer
import org.lanternpowered.server.entity.living.player.OfflineUser
import org.lanternpowered.server.entity.weather.LanternLightning
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule
import org.lanternpowered.server.game.registry.type.data.KeyRegistryModule
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
        KeyRegistryModule::class,
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
