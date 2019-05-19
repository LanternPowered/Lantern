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
package org.lanternpowered.server.network

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap

/**
 * Represents a storage for transactional requests/responses of
 * a specific [NetworkSession].
 *
 * This store should only be accessed from the netty thread, it's
 * not thread-safe.
 */
@NettyThreadOnly
class DefaultTransactionStore : TransactionStore {

    private var idCounter = 0

    /**
     * A map with stored data related to transactions.
     */
    private val data = Int2ObjectOpenHashMap<Any>()

    override fun nextId() = this.idCounter++

    override fun setData(id: Int, data: Any) {
        this.data[id] = data
    }

    override fun getData(id: Int): Any? = this.data[id]

    override fun removeData(id: Int): Any? = this.data.remove(id)
}
