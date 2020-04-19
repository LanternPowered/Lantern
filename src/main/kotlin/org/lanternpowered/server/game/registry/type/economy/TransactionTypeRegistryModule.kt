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
package org.lanternpowered.server.game.registry.type.economy

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule
import org.lanternpowered.server.service.economy.LanternTransactionType
import org.spongepowered.api.service.economy.transaction.TransactionType
import org.spongepowered.api.service.economy.transaction.TransactionTypes

class TransactionTypeRegistryModule : AdditionalPluginCatalogRegistryModule<TransactionType>(TransactionTypes::class) {

    override fun registerDefaults() {
        fun register(id: String) = register(LanternTransactionType(CatalogKey.sponge(id)))

        register("deposit")
        register("withdraw")
        register("transfer")
    }
}
