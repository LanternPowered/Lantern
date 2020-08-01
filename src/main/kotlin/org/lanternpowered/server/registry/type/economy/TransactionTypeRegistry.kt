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
package org.lanternpowered.server.registry.type.economy

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.spongeKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.service.economy.transaction.TransactionType

val TransactionTypeRegistry = catalogTypeRegistry<TransactionType> {
    fun register(id: String) =
            register(LanternTransactionType(spongeKey(id)))

    register("deposit")
    register("withdraw")
    register("transfer")
}

private class LanternTransactionType(key: NamespacedKey) : DefaultCatalogType(key), TransactionType
