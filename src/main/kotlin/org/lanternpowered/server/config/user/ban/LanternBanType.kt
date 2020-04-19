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
package org.lanternpowered.server.config.user.ban

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.util.ban.Ban
import org.spongepowered.api.util.ban.BanType

class LanternBanType(key: CatalogKey, private val banClass: Class<out Ban>) : DefaultCatalogType(key), BanType {

    override fun getBanClass(): Class<out Ban> = this.banClass
}
