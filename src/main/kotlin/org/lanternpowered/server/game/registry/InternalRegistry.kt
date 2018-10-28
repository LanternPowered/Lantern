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
package org.lanternpowered.server.game.registry

import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import org.lanternpowered.api.catalog.CatalogKey

class InternalRegistry<T : Any>(keyToId: Object2IntMap<String>, objectConstructor: (String, Int) -> T) {

    constructor(keyToId: Object2IntMap<String>, objectConstructor: (String) -> T) : this(keyToId, { key, _ -> objectConstructor(key) })

    private val keyToId: Object2IntMap<T>
    private val idToKey: Int2ObjectMap<T>

    init {
        this.keyToId = Object2IntOpenHashMap()
        this.keyToId.defaultReturnValue(InvalidId)
        this.idToKey = Int2ObjectOpenHashMap()

        keyToId.forEach { key, id ->
            val obj = objectConstructor(key, id)
            this.keyToId[obj] = id
            this.idToKey[id] = obj
        }
    }

    operator fun get(obj: T): Int? {
        val id = this.keyToId.getInt(obj)
        return if (id == InvalidId) id else null
    }

    operator fun get(id: Int): T? = this.idToKey.get(id)

    fun require(obj: T): Int {
        val id = this.keyToId.getInt(obj)
        check(id != InvalidId) { "Cannot find a mapping for object: $obj" }
        return id
    }

    fun require(id: Int): T = requireNotNull(this.idToKey.get(id)) { "Cannot find a mapping for the internal id: $id" }

    companion object {

        private const val InvalidId = -1

        @JvmStatic
        fun identityKey(keyToId: Object2IntMap<String>) = InternalRegistry(keyToId) { key -> key }

        @JvmStatic
        fun catalogKey(keyToId: Object2IntMap<String>) = InternalRegistry(keyToId) { key -> CatalogKey.resolve(key) }
    }
}
