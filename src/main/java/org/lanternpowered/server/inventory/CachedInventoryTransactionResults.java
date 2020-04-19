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
package org.lanternpowered.server.inventory;

import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;

/**
 * Cache the following {@link InventoryTransactionResult}s
 * to avoid reconstructing them constantly.
 */
final class CachedInventoryTransactionResults {

    static final InventoryTransactionResult SUCCESS_NO_TRANSACTIONS = InventoryTransactionResult.successNoTransactions();

    static final InventoryTransactionResult FAIL_NO_TRANSACTIONS = InventoryTransactionResult.failNoTransactions();
}
