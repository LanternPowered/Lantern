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
@file:Suppress("UNCHECKED_CAST")

package org.lanternpowered.server.registry

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.common.collect.ImmutableBiMap
import com.google.common.collect.MapMaker
import com.google.common.reflect.TypeParameter
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.api.cause.CauseStackManager
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.registry.CatalogTypeRegistry
import org.lanternpowered.api.registry.CatalogTypeRegistryBuilder
import org.lanternpowered.api.registry.MutableCatalogTypeRegistry
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.api.util.collections.toImmutableMap
import org.lanternpowered.api.util.type.TypeToken
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.registry.RegistryEvent
import java.util.function.Supplier

object LanternCatalogTypeRegistryFactory : CatalogTypeRegistry.Factory {

    override fun <T : CatalogType> build(typeToken: TypeToken<T>, fn: CatalogTypeRegistryBuilder<T>.() -> Unit): InternalCatalogTypeRegistry<T> =
            LanternInternalImmutableCatalogTypeRegistry(typeToken, fn)

    @JvmName("buildInternal")
    fun <T : CatalogType> build(typeToken: TypeToken<T>, fn: InternalCatalogTypeRegistryBuilder<T>.() -> Unit):
            InternalCatalogTypeRegistry<T> = LanternInternalImmutableCatalogTypeRegistry(typeToken, fn)

    fun <T : CatalogType, I> buildGeneric(typeToken: TypeToken<T>, fn: GenericInternalCatalogTypeRegistryBuilder<T, I>.() -> Unit):
            GenericInternalCatalogTypeRegistry<T, I> = LanternGenericInternalImmutableCatalogTypeRegistry(typeToken, fn)

    fun <T : CatalogType, I> buildMutableGeneric(typeToken: TypeToken<T>):
            GenericInternalCatalogTypeRegistry<T, I> = LanternGenericInternalMutableCatalogTypeRegistry(typeToken)

    override fun <T : CatalogType> buildMutable(typeToken: TypeToken<T>):
            MutableInternalCatalogTypeRegistry<T> = LanternInternalMutableCatalogTypeRegistry(typeToken)
}

private class LanternGenericInternalMutableCatalogTypeRegistry<T : CatalogType, I>(
        typeToken: TypeToken<T>
) : LanternMutableCatalogTypeRegistry<T, GenericInternalRegistryData<T, I>, LanternGenericRegistryBuilder<T, I>>(typeToken),
        GenericInternalCatalogTypeRegistry<T, I> {

    override fun createBuilder() = LanternGenericRegistryBuilder<T, I>(this.typeToken)
    override fun getId(type: T): I? = ensureLoaded().getId(type)
    override fun get(id: I): T? = ensureLoaded().get(id)
}

private class LanternInternalMutableCatalogTypeRegistry<T : CatalogType>(
        typeToken: TypeToken<T>
) : LanternMutableCatalogTypeRegistry<T, InternalRegistryData<T>, LanternRegistryBuilder<T>>(typeToken), MutableInternalCatalogTypeRegistry<T> {

    override fun createBuilder() = LanternRegistryBuilder(this.typeToken)
    override fun getId(type: T) = ensureLoaded().getId(type)
    override fun get(id: Int): T? = ensureLoaded().get(id)
}

private abstract class LanternMutableCatalogTypeRegistry<T : CatalogType, D : RegistryData<T>, B : AbstractCatalogTypeRegistryBuilder<T, *, D>>(
        typeToken: TypeToken<T>
) : LanternCatalogTypeRegistry<T, D>(typeToken), MutableCatalogTypeRegistry<T> {

    private val lock = Any()

    @Suppress("LeakingThis")
    @Volatile
    private var data = createBuilder().build()

    abstract fun createBuilder(): B

    override fun load(fn: CatalogTypeRegistryBuilder<T>.() -> Unit) {
        synchronized(this.lock) {
            val builder = createBuilder()
            builder.fn()
            builder.registerPluginTypes()
            this.data = builder.build()
        }
        invalidateSuppliers()
    }

    override fun ensureLoaded(): D = this.data
}

private class LanternGenericInternalImmutableCatalogTypeRegistry<T : CatalogType, I>(
        typeToken: TypeToken<T>, initializer: GenericInternalCatalogTypeRegistryBuilder<T, I>.() -> Unit
) : LanternImmutableCatalogTypeRegistry<T, GenericInternalRegistryData<T, I>, LanternGenericRegistryBuilder<T, I>>(typeToken, initializer),
        GenericInternalCatalogTypeRegistry<T, I> {

    override fun createBuilder() = LanternGenericRegistryBuilder<T, I>(this.typeToken)
    override fun getId(type: T): I? = ensureLoaded().getId(type)
    override fun get(id: I): T? = ensureLoaded().get(id)
}

private class LanternInternalImmutableCatalogTypeRegistry<T : CatalogType>(
        typeToken: TypeToken<T>, initializer: InternalCatalogTypeRegistryBuilder<T>.() -> Unit
) : LanternImmutableCatalogTypeRegistry<T, InternalRegistryData<T>, LanternRegistryBuilder<T>>(typeToken, initializer),
        InternalCatalogTypeRegistry<T> {

    override fun createBuilder() = LanternRegistryBuilder(this.typeToken)
    override fun getId(type: T) = ensureLoaded().getId(type)
    override fun get(id: Int): T? = ensureLoaded().get(id)
}

private abstract class LanternImmutableCatalogTypeRegistry<T : CatalogType, D : RegistryData<T>, B : AbstractCatalogTypeRegistryBuilder<T, *, D>>(
        typeToken: TypeToken<T>, initializer: B.() -> Unit
) : LanternCatalogTypeRegistry<T, D>(typeToken) {

    private val lock = Any()
    private var initializer: (B.() -> Unit)? = initializer

    @Volatile
    private var data: D? = null

    abstract fun createBuilder(): B

    override fun ensureLoaded(): D {
        var data = this.data
        if (data != null)
            return data
        synchronized(this.lock) {
            data = this.data
            if (data != null)
                return data as D
            return load(this.initializer!!)
        }
    }

    private fun load(initializer: B.() -> Unit): D {
        val builder = createBuilder()
        val dependents = dependentsThreadLocal.get()
        // Check for circular dependencies, this is not allowed.
        if (this in dependents) {
            val path = (dependents + listOf(this)).joinToString(separator = " -> ") { it.typeToken.rawType.simpleName }
            throw IllegalStateException("Circular dependency tree: $path")
        }
        dependents.add(this)
        try {
            builder.initializer()
            builder.registerPluginTypes()
            val data = builder.build()
            this.initializer = null
            this.data = data
            invalidateSuppliers()
            return data
        } finally {
            dependents.removeLast()
        }
    }

    companion object {

        private val dependentsThreadLocal: ThreadLocal<MutableList<CatalogTypeRegistry<*>>> =
                ThreadLocal.withInitial { mutableListOf<CatalogTypeRegistry<*>>() }
    }
}

abstract class LanternCatalogTypeRegistry<T : CatalogType, D : RegistryData<T>>(
        override val typeToken: TypeToken<T>
) : CatalogTypeRegistry<T> {

    val typeName: String get() = this.typeToken.rawType.simpleName

    private val suppliers: MutableMap<String, CatalogTypeSupplier<T>> = MapMaker().weakValues().makeMap()

    override val all: Collection<T>
        get() {
            val data = ensureLoaded()
            return data.byKey.values
        }

    override fun get(key: CatalogKey): T? {
        val data = ensureLoaded()
        return data.byKey[key]
    }

    override fun provideSupplier(suggestedId: String): Supplier<T> =
            this.suppliers.computeIfAbsent(suggestedId) { CatalogTypeSupplier(suggestedId, this) }

    protected fun invalidateSuppliers() {
        this.suppliers.values.forEach { supplier -> supplier.invalidate() }
    }

    fun find(suggestedId: String): T? {
        val lowerId = suggestedId.toLowerCase()
        val data = ensureLoaded()
        return data.byKey.values.asSequence()
                .filter { type ->
                    type.key.value.toLowerCase() == lowerId || data.suggestedIdMatchers.any { matcher -> matcher(lowerId, type) }
                }
                .sortedBy { type ->
                    val index = prioritizedNamespaces.indexOf(type.key.namespace)
                    if (index == -1) prioritizedNamespaces.size else index
                }
                .firstOrNull()
    }

    /**
     * Makes sure that the registry is loaded.
     */
    protected abstract fun ensureLoaded(): D

    companion object {
        val prioritizedNamespaces = listOf("minecraft", "lantern", "sponge")
    }
}

class GenericInternalRegistryData<T : CatalogType, I>(
        override val byKey: Map<CatalogKey, T> = emptyMap(),
        private val byId: BiMap<I, T> = HashBiMap.create(),
        override val suggestedIdMatchers: List<(String, T) -> Boolean> = emptyList()
) : RegistryData<T> {

    fun get(id: I): T? = this.byId[id]
    fun getId(type: T): I? = this.byId.inverse()[type]
}

class InternalRegistryData<T : CatalogType>(
        override val byKey: Map<CatalogKey, T> = emptyMap(),
        private val byId: Int2ObjectMap<T> = Int2ObjectMaps.emptyMap(),
        private val toId: Object2IntMap<T> = Object2IntOpenHashMap<T>().apply { defaultReturnValue(Integer.MIN_VALUE) },
        override val suggestedIdMatchers: List<(String, T) -> Boolean> = emptyList()
) : RegistryData<T> {

    fun get(id: Int): T? = this.byId[id]

    fun getId(type: T): Int {
        val id = this.toId.getInt(type)
        if (id == Integer.MIN_VALUE)
            throw IllegalArgumentException("No id was found for the given catalog type: $type")
        return id
    }
}

interface RegistryData<T> {
    val byKey: Map<CatalogKey, T>
    val suggestedIdMatchers: List<(String, T) -> Boolean>
}

private class CatalogTypeSupplier<T : CatalogType>(
        private val suggestedId: String,
        private val registry: LanternCatalogTypeRegistry<T, *>
) : Supplier<T> {

    private object Uninitialized

    @Volatile private var value: Any? = Uninitialized
    private val lock = Any()

    /**
     * Invalidates the [Supplier], can be triggered
     * after registry reloads.
     */
    fun invalidate() {
        synchronized(this.lock) {
            this.value = Uninitialized
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun getNullable(): T? {
        var value = this.value
        if (value !== Uninitialized)
            return value as T?
        synchronized(this.lock) {
            value = this.value
            if (value !== Uninitialized)
                return value as T?
            value = this.registry.find(this.suggestedId)
            this.value = value
            return value as T?
        }
    }

    override fun get(): T = getNullable() ?: error(
            "There's no ${registry.typeName} registered with the suggested id: $suggestedId")
}

private class LanternGenericInternalCatalogTypeRegistryBuilder<T : CatalogType>(typeToken: TypeToken<T>) :
        AbstractCatalogTypeRegistryBuilder<T, Int, InternalRegistryData<T>>(typeToken), InternalCatalogTypeRegistryBuilder<T> {

    private var nextFreeId: Int = 0

    override fun register(internalId: Int, type: T): T = register(type, internalId)

    override fun register(type: T, internalId: Int?): T {
        super.register(type, internalId ?: this.nextFreeId)
        while (this.byId.containsKey(this.nextFreeId)) {
            this.nextFreeId++
        }
        return type
    }

    override fun build(): InternalRegistryData<T> {
        val toId = Object2IntOpenHashMap<T>(this.byId.inverse()).apply { defaultReturnValue(Integer.MIN_VALUE) }
        val byId = Int2ObjectOpenHashMap(this.byId)
        return InternalRegistryData(this.byKey.toImmutableMap(), byId, toId, this.suggestedIdMatchers)
    }
}

private class LanternGenericRegistryBuilder<T : CatalogType, I>(typeToken: TypeToken<T>) :
        AbstractCatalogTypeRegistryBuilder<T, I, GenericInternalRegistryData<T, I>>(typeToken), GenericInternalCatalogTypeRegistryBuilder<T, I> {

    override fun register(internalId: I, type: T): T = register(type, internalId)

    override fun build(): GenericInternalRegistryData<T, I> {
        val byKey = this.byKey.toImmutableMap()
        val byId = ImmutableBiMap.copyOf(this.byId)
        val suggestedIdMatchers = this.suggestedIdMatchers.toImmutableList()
        return GenericInternalRegistryData(byKey, byId, suggestedIdMatchers)
    }
}

private class LanternRegistryBuilder<T : CatalogType>(typeToken: TypeToken<T>) :
        AbstractCatalogTypeRegistryBuilder<T, Int, InternalRegistryData<T>>(typeToken), InternalCatalogTypeRegistryBuilder<T> {

    private var nextFreeId: Int = 0

    override fun register(internalId: Int, type: T): T = register(type, internalId)

    override fun register(type: T, internalId: Int?): T {
        super.register(type, internalId ?: this.nextFreeId)
        while (this.byId.containsKey(this.nextFreeId)) {
            this.nextFreeId++
        }
        return type
    }

    override fun build(): InternalRegistryData<T> {
        val toId = Object2IntOpenHashMap<T>(this.byId.inverse()).apply { defaultReturnValue(Integer.MIN_VALUE) }
        val byId = Int2ObjectOpenHashMap(this.byId)
        return InternalRegistryData(this.byKey.toImmutableMap(), byId, toId, this.suggestedIdMatchers.toImmutableList())
    }
}

private abstract class AbstractCatalogTypeRegistryBuilder<T : CatalogType, I, D : RegistryData<T>>(
        val typeToken: TypeToken<T>
): CatalogTypeRegistryBuilder<T> {

    private val typeName: String get() = this.typeToken.rawType.simpleName
    protected val suggestedIdMatchers: MutableList<(String, T) -> Boolean> = mutableListOf()
    protected val byKey: MutableMap<CatalogKey, T> = mutableMapOf()
    protected val byId: BiMap<I, T> = HashBiMap.create()
    private var allowPluginRegistrations = false

    override fun register(type: T): T = register(type, null)

    protected open fun register(type: T, internalId: I?): T {
        val key = type.key
        check(!this.byKey.containsKey(key)) { "There's already a $typeName registered with the key: $key" }
        if (internalId != null)
            check(!this.byId.containsKey(internalId)) { "There's already a $typeName registered with the internal id: $internalId" }
        this.byKey[key] = type
        this.byId[internalId] = type
        return type
    }

    open fun validate(type: T) {
        val key = type.key
        check(!this.byKey.containsKey(key)) { "There's already a $typeName registered with the key: $key" }
    }

    fun registerPluginTypes() {
        if (!this.allowPluginRegistrations)
            return
        val cause = CauseStackManager.currentCause
        val genericType = object : TypeToken<RegistryEvent.Catalog<T>>() {}
                .where(object : TypeParameter<T>() {}, this.typeToken)
        val event = object : RegistryEvent.Catalog<T> {
            override fun getCause(): Cause = cause
            override fun getGenericType(): TypeToken<out RegistryEvent.Catalog<T>> = genericType
            override fun register(catalogType: T) { this@AbstractCatalogTypeRegistryBuilder.register(catalogType) }
        }
        EventManager.post(event)
    }

    abstract fun build(): D

    override fun allowPluginRegistrations() { this.allowPluginRegistrations = true }
    override fun matchSuggestedId(matcher: (suggestedId: String, type: T) -> Boolean) { this.suggestedIdMatchers += matcher }
}

private class LanternCatalogTypeRegistryBuilder<T : CatalogType, I>(
        val typeToken: TypeToken<T>
) : InternalCatalogTypeRegistryBuilder<T>, GenericInternalCatalogTypeRegistryBuilder<T, I> {

    private val suggestedIdMatchers: MutableList<(String, T) -> Boolean> = mutableListOf()
    private val byKey: MutableMap<CatalogKey, T> = mutableMapOf()
    private val byIntId: Int2ObjectMap<T> = Int2ObjectOpenHashMap<T>()
    private val toIntId: Object2IntMap<T> = Object2IntOpenHashMap<T>().apply { defaultReturnValue(Integer.MIN_VALUE) }
    private var allowPluginRegistrations = false

    private var nextFreeId: Int = 0
    private val typeName: String get() = this.typeToken.rawType.simpleName

    override fun register(internalId: Int, type: T): T {
        val key = type.key
        check(!this.byKey.containsKey(key)) { "There's already a $typeName registered with the key: $key" }
        check(!this.byIntId.containsKey(internalId)) { "There's already a $typeName registered with the internal id: $internalId" }
        this.byKey[key] = type
        this.byIntId[internalId] = type
        this.toIntId[type] = internalId
        while (this.byIntId.containsKey(this.nextFreeId)) {
            this.nextFreeId++
        }
        return type
    }

    override fun register(internalId: I, type: T): T {
        TODO("Not yet implemented")
    }

    override fun register(type: T) = register(this.nextFreeId, type)
    override fun allowPluginRegistrations() { this.allowPluginRegistrations = true }
    override fun matchSuggestedId(matcher: (suggestedId: String, type: T) -> Boolean) { this.suggestedIdMatchers += matcher }
}
