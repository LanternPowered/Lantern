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
package org.lanternpowered.server.network.channel

import io.netty.util.AttributeKey
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Represents a storage for transactional requests/responses of
 * a specific connection.
 */
open class ChannelTransactionStore {

    private var idCounter = AtomicInteger()

    /**
     * A map with stored data related to transactions.
     */
    internal val transactions = ConcurrentHashMap<Int, ChannelTransaction>()

    /**
     * Gets the next available transaction id.
     *
     * @return The transaction id
     */
    fun nextId() = this.idCounter.getAndIncrement()

    /**
     * Sets the transaction data for the given transaction id.
     *
     * @param id The id
     * @param transaction The transaction data
     */
    fun put(id: Int, transaction: ChannelTransaction) {
        this.transactions[id] = transaction
    }

    /**
     * Gets the transaction data for the given transaction id.
     *
     * @param id The id
     * @return The transaction data
     */
    fun getData(id: Int): ChannelTransaction? = this.transactions[id]

    /**
     * Removes the transaction data for the given transaction id.
     *
     * @param id The id
     * @return The removed transaction data
     */
    open fun removeData(id: Int): ChannelTransaction? = this.transactions.remove(id)

    companion object {

        /**
         * The [AttributeKey] of the [ChannelTransactionStore].
         */
        val KEY: AttributeKey<ChannelTransactionStore> = AttributeKey.valueOf("transaction-store")
    }
}
