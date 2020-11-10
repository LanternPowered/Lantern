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
package org.lanternpowered.server.registry.type.util

import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.minecraftKey
import org.spongepowered.api.service.ban.Ban
import org.spongepowered.api.service.ban.BanType
import kotlin.reflect.KClass

val BanTypeRegistry = catalogTypeRegistry<BanType> {
    fun register(id: String, banClass: KClass<out Ban>) =
            register(LanternBanType(minecraftKey(id), banClass.java))

    register("profile", Ban.Profile::class)
    register("ip", Ban.Ip::class)
}

private class LanternBanType(key: NamespacedKey, private val banClass: Class<out Ban>) : DefaultCatalogType(key), BanType {
    override fun getBanClass(): Class<out Ban> = this.banClass
}
