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
package org.lanternpowered.server.game.registry

import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import org.lanternpowered.api.namespace.NamespacedKey

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
        fun resourceKey(keyToId: Object2IntMap<String>) = InternalRegistry(keyToId) { key -> NamespacedKey.resolve(key) }
    }
}
