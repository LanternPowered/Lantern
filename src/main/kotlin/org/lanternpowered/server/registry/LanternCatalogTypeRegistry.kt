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
package org.lanternpowered.server.registry

import com.google.common.collect.MapMaker
import com.google.common.reflect.TypeParameter
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntMaps
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.api.cause.CauseStackManager
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.registry.CatalogTypeRegistry
import org.lanternpowered.api.registry.CatalogTypeRegistryBuilder
import org.lanternpowered.api.util.collections.toImmutableMap
import org.lanternpowered.api.util.type.TypeToken
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.registry.RegistryEvent
import java.util.function.Supplier

object LanternCatalogTypeRegistryFactory : CatalogTypeRegistry.Factory {

    override fun <T : CatalogType> build(typeToken: TypeToken<T>, fn: CatalogTypeRegistryBuilder<T>.() -> Unit): InternalCatalogTypeRegistry<T> =
            LanternImmutableCatalogTypeRegistry(typeToken, fn)

    @JvmName("buildInternal")
    fun <T : CatalogType> build(typeToken: TypeToken<T>, fn: InternalCatalogTypeRegistryBuilder<T>.() -> Unit): InternalCatalogTypeRegistry<T> =
            LanternImmutableCatalogTypeRegistry(typeToken, fn)

    override fun <T : CatalogType> buildMutable(typeToken: TypeToken<T>):
            MutableInternalCatalogTypeRegistry<T> = LanternMutableCatalogTypeRegistry(typeToken)
}

private class LanternMutableCatalogTypeRegistry<T : CatalogType>(
        typeToken: TypeToken<T>
) : LanternCatalogTypeRegistry<T, InvalidatableCatalogTypeSupplier<T>>(typeToken), MutableInternalCatalogTypeRegistry<T> {

    override fun createSupplier(suggestedId: String) = InvalidatableCatalogTypeSupplier(suggestedId, this)

    override fun load(fn: CatalogTypeRegistryBuilder<T>.() -> Unit) {
        synchronized(this.lock) {
            val builder = LanternCatalogTypeRegistryBuilder(this.typeToken)
            builder.fn()
            builder.registerPluginTypes()
            this.mappings = builder.toMappings()
        }
        this.suppliers.values.forEach { supplier -> supplier.invalidate() }
    }

    override fun ensureLoaded() {
    }
}

private class LanternImmutableCatalogTypeRegistry<T : CatalogType>(
        typeToken: TypeToken<T>, initializer: InternalCatalogTypeRegistryBuilder<T>.() -> Unit
) : LanternCatalogTypeRegistry<T, ConstantCatalogTypeSupplier<T>>(typeToken) {

    @Volatile
    private var initializer: (InternalCatalogTypeRegistryBuilder<T>.() -> Unit)? = initializer

    override fun createSupplier(suggestedId: String) = ConstantCatalogTypeSupplier(suggestedId, this)

    override fun ensureLoaded() {
        if (this.initializer == null)
            return
        synchronized(this.lock) {
            val initializer = this.initializer ?: return
            load(initializer)
        }
    }

    private fun load(initializer: InternalCatalogTypeRegistryBuilder<T>.() -> Unit) {
        val builder = LanternCatalogTypeRegistryBuilder(this.typeToken)
        val dependents = dependentsThreadLocal.get()
        // Check for circular dependencies, this is not allowed.
        if (this in dependents) {
            val path = (dependents + this).joinToString(separator = " -> ") { it.typeToken.rawType.simpleName }
            throw IllegalStateException("Circular dependency tree: $path")
        }
        dependents.add(this)
        try {
            builder.initializer()
            builder.registerPluginTypes()
            this.initializer = null
        } finally {
            dependents.removeLast()
        }
    }

    companion object {

        private val dependentsThreadLocal: ThreadLocal<MutableList<CatalogTypeRegistry<*>>> =
                ThreadLocal.withInitial { mutableListOf<CatalogTypeRegistry<*>>() }
    }
}

abstract class LanternCatalogTypeRegistry<T : CatalogType, S : CatalogTypeSupplier<T>>(
        override val typeToken: TypeToken<T>
) : InternalCatalogTypeRegistry<T> {

    val typeName: String get() = this.typeToken.rawType.simpleName

    protected val lock = Any()
    protected val suppliers: MutableMap<String, S> = MapMaker().weakValues().makeMap()

    class Mappings<T : CatalogType>(
            val byKey: Map<CatalogKey, T> = emptyMap(),
            val byId: Int2ObjectMap<T> = Int2ObjectMaps.emptyMap(),
            val toId: Object2IntMap<T> = Object2IntMaps.emptyMap()
    )

    @Volatile
    protected var mappings = Mappings<T>()

    override val all: Collection<T>
        get() {
            ensureLoaded()
            return this.mappings.byKey.values
        }

    override fun get(key: CatalogKey): T? {
        ensureLoaded()
        return this.mappings.byKey[key]
    }

    override fun get(id: InternalCatalogId): T? {
        ensureLoaded()
        return this.mappings.byId[id.value]
    }

    override fun getId(type: T): InternalCatalogId {
        ensureLoaded()
        val id = this.mappings.toId.getInt(type)
        if (id == Integer.MIN_VALUE)
            throw IllegalArgumentException("No id was found for the given catalog type: $type")
        return InternalCatalogId(id)
    }

    override fun provideSupplier(suggestedId: String): Supplier<T> {
        ensureLoaded()
        return this.suppliers.computeIfAbsent(suggestedId) { createSupplier(suggestedId) }
    }

    protected abstract fun createSupplier(suggestedId: String): S

    /**
     * Makes sure that the registry is loaded.
     */
    protected abstract fun ensureLoaded()
}

abstract class CatalogTypeSupplier<T : CatalogType>(
        protected val suggestedId: String,
        protected val registry: LanternCatalogTypeRegistry<T, *>
) : Supplier<T> {

    protected fun load(): T? {
        val lowerId = this.suggestedId.toLowerCase()
        return this.registry.all.asSequence()
                .filter { type -> type.key.value.toLowerCase() == lowerId }
                .sortedBy { type ->
                    val index = prioritizedNamespaces.indexOf(type.key.namespace)
                    if (index == -1) prioritizedNamespaces.size else index
                }
                .firstOrNull()
    }

    protected abstract fun getNullable(): T?

    override fun get(): T = getNullable() ?: error("There's no ${registry.typeName} registered with the suggested id: $suggestedId")

    companion object {

        val prioritizedNamespaces = listOf("minecraft", "lantern", "sponge")
    }
}

private class ConstantCatalogTypeSupplier<T : CatalogType>(suggestedId: String, registry: LanternCatalogTypeRegistry<T, *>) :
        CatalogTypeSupplier<T>(suggestedId, registry) {

    private val value: T? = load()

    override fun getNullable(): T? = this.value
}

private class InvalidatableCatalogTypeSupplier<T : CatalogType>(suggestedId: String, registry: LanternCatalogTypeRegistry<T, *>) :
        CatalogTypeSupplier<T>(suggestedId, registry) {

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
    override fun getNullable(): T? {
        var value = this.value
        if (value != Uninitialized)
            return value as T?
        synchronized(this.lock) {
            value = this.value
            if (value != Uninitialized)
                return value as T?
            value = load()
            this.value = value
            return value as T?
        }
    }
}

private class LanternCatalogTypeRegistryBuilder<T : CatalogType>(
        val typeToken: TypeToken<T>
) : InternalCatalogTypeRegistryBuilder<T> {

    private val byKey: MutableMap<CatalogKey, T> = mutableMapOf()
    private val byId: Int2ObjectMap<T> = Int2ObjectOpenHashMap<T>()
    private val toId: Object2IntMap<T> = Object2IntOpenHashMap<T>()
    private var allowPluginRegistrations = false

    private var nextFreeId: Int = 0
    private val typeName: String get() = this.typeToken.rawType.simpleName

    override fun register(internalId: Int, type: T) {
        val key = type.key
        check(!this.byKey.containsKey(key)) { "There's already a $typeName registered with the key: $key" }
        check(!this.byId.containsKey(internalId)) { "There's already a $typeName registered with the internal id: $internalId" }
        this.byKey[key] = type
        this.byId[internalId] = type
        this.toId[type] = internalId
        while (this.byId.containsKey(this.nextFreeId)) {
            this.nextFreeId++
        }
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
            override fun register(catalogType: T) = this@LanternCatalogTypeRegistryBuilder.register(catalogType)
        }
        EventManager.post(event)
    }

    fun toMappings() = LanternCatalogTypeRegistry.Mappings(
            this.byKey.toImmutableMap(), this.byId, this.toId)

    override fun register(type: T) = register(this.nextFreeId, type)
    override fun register(internalId: InternalCatalogId, type: T) = register(internalId.value, type)
    override fun allowPluginRegistrations() { this.allowPluginRegistrations = true }
}
