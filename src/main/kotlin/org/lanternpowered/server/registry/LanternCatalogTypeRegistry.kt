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
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import org.lanternpowered.api.ResourceKey
import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.api.cause.Cause
import org.lanternpowered.api.cause.CauseStackManager
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.registry.CatalogTypeRegistry
import org.lanternpowered.api.registry.CatalogTypeRegistryBuilder
import org.lanternpowered.api.registry.MutableCatalogTypeRegistry
import org.lanternpowered.api.registry.MutableCatalogTypeRegistryBase
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.api.util.collections.toImmutableMap
import org.lanternpowered.api.util.type.TypeToken
import org.lanternpowered.server.LanternGame
import org.spongepowered.api.Game
import org.spongepowered.api.event.lifecycle.RegisterCatalogEvent
import java.util.function.Supplier

object LanternCatalogTypeRegistryFactory : CatalogTypeRegistry.Factory {

    override fun <T : CatalogType> build(typeToken: TypeToken<T>, fn: CatalogTypeRegistryBuilder<T>.() -> Unit): CatalogTypeRegistry<T> =
            LanternImmutableCatalogTypeRegistry(typeToken, fn)

    @JvmName("buildInternal")
    fun <T : CatalogType> build(typeToken: TypeToken<T>, fn: InternalCatalogTypeRegistryBuilder<T, Int>.() -> Unit):
            InternalCatalogTypeRegistry<T> = LanternInternalImmutableCatalogTypeRegistry(typeToken, fn)

    fun <T : CatalogType, I> buildInternalGeneric(typeToken: TypeToken<T>, fn: InternalCatalogTypeRegistryBuilder<T, I>.() -> Unit):
            GenericInternalCatalogTypeRegistry<T, I> = LanternGenericInternalImmutableCatalogTypeRegistry(typeToken, fn)

    fun <T : CatalogType, I> buildMutableInternalGeneric(typeToken: TypeToken<T>):
            MutableGenericInternalCatalogTypeRegistry<T, I> = LanternGenericInternalMutableCatalogTypeRegistry(typeToken)

    override fun <T : CatalogType> buildMutable(typeToken: TypeToken<T>):
            MutableCatalogTypeRegistry<T> = LanternMutableCatalogTypeRegistry(typeToken)

    fun <T : CatalogType> buildMutableInternal(typeToken: TypeToken<T>):
            MutableInternalCatalogTypeRegistry<T> = LanternInternalMutableCatalogTypeRegistry(typeToken)
}

private class LanternGenericInternalMutableCatalogTypeRegistry<T : CatalogType, I>(
        typeToken: TypeToken<T>
) : AbstractMutableCatalogTypeRegistry<T, GenericInternalRegistryData<T, I>, InternalCatalogTypeRegistryBuilder<T, I>,
        MutableGenericInternalCatalogTypeRegistry<T, I>>(typeToken), MutableGenericInternalCatalogTypeRegistry<T, I> {

    override fun createBuilder() = LanternGenericInternalRegistryBuilder<T, I>(this.typeToken)
    override fun getId(type: T): I? = ensureLoaded().getId(type)
    override fun get(id: I): T? = ensureLoaded().get(id)
}

private class LanternInternalMutableCatalogTypeRegistry<T : CatalogType>(
        typeToken: TypeToken<T>
) : AbstractMutableCatalogTypeRegistry<T, InternalRegistryData<T>, InternalCatalogTypeRegistryBuilder<T, Int>,
        MutableInternalCatalogTypeRegistry<T>>(typeToken), MutableInternalCatalogTypeRegistry<T> {

    override fun createBuilder() = LanternInternalRegistryBuilder(this.typeToken)
    override fun getId(type: T) = ensureLoaded().getId(type)
    override fun get(id: Int): T? = ensureLoaded().get(id)
}

private class LanternMutableCatalogTypeRegistry<T : CatalogType>(
        typeToken: TypeToken<T>
) : AbstractMutableCatalogTypeRegistry<T, InternalRegistryData<T>, CatalogTypeRegistryBuilder<T>, MutableCatalogTypeRegistry<T>>(typeToken),
        MutableCatalogTypeRegistry<T> {

    override fun createBuilder() = LanternRegistryBuilder(this.typeToken)
}

private abstract class AbstractMutableCatalogTypeRegistry<T, D, B, R>(
        typeToken: TypeToken<T>
) : LanternCatalogTypeRegistry<T, D>(typeToken), MutableCatalogTypeRegistryBase<T, B, R>
        where T : CatalogType,
              D : RegistryData<T>,
              B : CatalogTypeRegistryBuilder<T>,
              R : MutableCatalogTypeRegistryBase<T, B, R> {

    private val lock = Any()
    @Suppress("LeakingThis") @Volatile private var data = createBuilder().fix().build()
    private val watchers = mutableListOf<R.() -> Unit>()

    private fun B.fix(): AbstractCatalogTypeRegistryBuilder<T, *, D> =
            this as AbstractCatalogTypeRegistryBuilder<T, *, D>

    abstract fun createBuilder(): B

    override fun load(fn: B.() -> Unit) {
        synchronized(this.lock) {
            val builder = createBuilder()
            builder.fn()
            builder.fix().registerPluginTypes()
            this.data = builder.fix().build()
            invalidateSuppliers()
            for (watcher in this.watchers)
                watcher(this as R)
        }
    }

    override fun ensureLoaded(): D = this.data

    override fun watch(watcher: R.() -> Unit) {
        synchronized(this.lock) {
            this.watchers += watcher
        }
    }
}

private class LanternGenericInternalImmutableCatalogTypeRegistry<T : CatalogType, I>(
        typeToken: TypeToken<T>, initializer: InternalCatalogTypeRegistryBuilder<T, I>.() -> Unit
) : AbstractImmutableCatalogTypeRegistry<T, GenericInternalRegistryData<T, I>, LanternGenericInternalRegistryBuilder<T, I>>(typeToken, initializer),
        GenericInternalCatalogTypeRegistry<T, I> {

    override fun createBuilder() = LanternGenericInternalRegistryBuilder<T, I>(this.typeToken)
    override fun getId(type: T): I? = ensureLoaded().getId(type)
    override fun get(id: I): T? = ensureLoaded().get(id)
}

private class LanternInternalImmutableCatalogTypeRegistry<T : CatalogType>(
        typeToken: TypeToken<T>, initializer: InternalCatalogTypeRegistryBuilder<T, Int>.() -> Unit
) : AbstractImmutableCatalogTypeRegistry<T, InternalRegistryData<T>, InternalCatalogTypeRegistryBuilder<T, Int>>(typeToken, initializer),
        InternalCatalogTypeRegistry<T> {

    override fun createBuilder() = LanternInternalRegistryBuilder(this.typeToken)
    override fun getId(type: T) = ensureLoaded().getId(type)
    override fun get(id: Int): T? = ensureLoaded().get(id)
}

private class LanternImmutableCatalogTypeRegistry<T : CatalogType>(
        typeToken: TypeToken<T>, initializer: CatalogTypeRegistryBuilder<T>.() -> Unit
) : AbstractImmutableCatalogTypeRegistry<T, RegistryData<T>, CatalogTypeRegistryBuilder<T>>(typeToken, initializer),
        CatalogTypeRegistry<T> {

    override fun createBuilder() = LanternRegistryBuilder(this.typeToken)
}

private abstract class AbstractImmutableCatalogTypeRegistry<T, D, B>(
        typeToken: TypeToken<T>, initializer: B.() -> Unit
) : LanternCatalogTypeRegistry<T, D>(typeToken)
        where T : CatalogType,
              D : RegistryData<T>,
              B : CatalogTypeRegistryBuilder<T> {

    private val lock = Any()
    private var initializer: (B.() -> Unit)? = initializer
    @Volatile private var data: D? = null

    private fun B.fix(): AbstractCatalogTypeRegistryBuilder<T, *, D> =
            this as AbstractCatalogTypeRegistryBuilder<T, *, D>

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
        val cycleStack = dependencyCyclesThreadLocal.get()
        // Check for cyclic dependencies, this is not allowed.
        if (this in cycleStack) {
            val cycles = cycleStack + listOf(this)
            val message = StringBuilder()
            message.append("Dependencies are cyclic! [\n  ")
            cycles.joinTo(message, separator = " ->\n  ") { it.typeToken.rawType.simpleName }
            message.append("\n]")
            throw CyclicDependencyException(message.toString())
        }
        cycleStack.add(this)
        try {
            builder.initializer()
            builder.fix().registerPluginTypes()
            val data = builder.fix().build()
            this.initializer = null
            this.data = data
            invalidateSuppliers()
            return data
        } finally {
            cycleStack.removeLast()
        }
    }

    companion object {

        private val dependencyCyclesThreadLocal: ThreadLocal<MutableList<CatalogTypeRegistry<*>>> =
                ThreadLocal.withInitial { mutableListOf<CatalogTypeRegistry<*>>() }
    }
}

private abstract class LanternCatalogTypeRegistry<T : CatalogType, D : RegistryData<T>>(
        override val typeToken: TypeToken<T>
) : CatalogTypeRegistry<T> {

    private val suppliers: MutableMap<String, CatalogTypeSupplier<T>> = MapMaker().weakValues().makeMap()

    val typeName: String
        get() = this.typeToken.rawType.simpleName

    override val all: org.lanternpowered.api.registry.Collection<T>
        get() = ensureLoaded().values

    override fun get(key: ResourceKey): T? = ensureLoaded().byKey[key]

    override fun provideSupplier(suggestedId: String): Supplier<T> =
            this.suppliers.computeIfAbsent(suggestedId) { CatalogTypeSupplier(suggestedId, this) }

    override fun provide(suggestedId: String): T = find(suggestedId) ?: error("There's no $typeName registered with the suggested id: $suggestedId")

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

private class GenericInternalRegistryData<T : CatalogType, I>(
        byKey: Map<ResourceKey, T> = emptyMap(),
        private val byId: BiMap<I, T> = HashBiMap.create(),
        suggestedIdMatchers: List<(String, T) -> Boolean> = emptyList()
) : RegistryData<T>(byKey, suggestedIdMatchers) {

    fun get(id: I): T? = this.byId[id]
    fun getId(type: T): I? = this.byId.inverse()[type]
}

private class InternalRegistryData<T : CatalogType>(
        byKey: Map<ResourceKey, T> = emptyMap(),
        private val byId: Int2ObjectMap<T> = Int2ObjectMaps.emptyMap(),
        private val toId: Object2IntMap<T> = Object2IntOpenHashMap<T>().apply { defaultReturnValue(Integer.MIN_VALUE) },
        suggestedIdMatchers: List<(String, T) -> Boolean> = emptyList()
) : RegistryData<T>(byKey, suggestedIdMatchers) {

    fun get(id: Int): T? = this.byId[id]

    fun getId(type: T): Int {
        val id = this.toId.getInt(type)
        if (id == Integer.MIN_VALUE)
            throw IllegalArgumentException("No id was found for the given catalog type: $type")
        return id
    }
}

private open class RegistryData<T>(
        val byKey: Map<ResourceKey, T>,
        val suggestedIdMatchers: List<(String, T) -> Boolean>
) {
    val values = CustomCollection(this.byKey.values)
}

/**
 * The custom collection interface is used to prevent JVM signature collisions. E.g. in [LanternRecipeRegistry].
 */
private class CustomCollection<E>(backing: Collection<E>) : org.lanternpowered.api.registry.Collection<E>, Collection<E> by backing

private class CatalogTypeSupplier<T : CatalogType>(
        private val suggestedId: String,
        private val registry: LanternCatalogTypeRegistry<T, *>
) : Supplier<T> {

    private object Uninitialized

    @Volatile private var value: Any? = Uninitialized
    private val lock = Any()

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

private class LanternGenericInternalRegistryBuilder<T : CatalogType, I>(typeToken: TypeToken<T>) :
        AbstractCatalogTypeRegistryBuilder<T, I, GenericInternalRegistryData<T, I>>(typeToken) {

    override fun build(): GenericInternalRegistryData<T, I> {
        val byKey = this.byKey.toImmutableMap()
        val byId = ImmutableBiMap.copyOf(this.byId)
        val suggestedIdMatchers = this.suggestedIdMatchers.toImmutableList()
        return GenericInternalRegistryData(byKey, byId, suggestedIdMatchers)
    }
}

private class LanternInternalRegistryBuilder<T : CatalogType>(typeToken: TypeToken<T>) :
        AbstractCatalogTypeRegistryBuilder<T, Int, InternalRegistryData<T>>(typeToken) {

    private var nextFreeId: Int = 0

    override fun <R : T> register(type: R, internalId: Int?): R {
        super.register(type, internalId ?: this.nextFreeId)
        while (this.byId.containsKey(this.nextFreeId)) {
            this.nextFreeId++
        }
        return type
    }

    override fun build(): InternalRegistryData<T> {
        val byKey = this.byKey.toImmutableMap()
        val toId = Object2IntOpenHashMap<T>(this.byId.inverse()).apply { defaultReturnValue(Integer.MIN_VALUE) }
        val byId = Int2ObjectOpenHashMap(this.byId)
        val suggestedIdMatchers = this.suggestedIdMatchers.toImmutableList()
        return InternalRegistryData(byKey, byId, toId, suggestedIdMatchers)
    }
}

private class LanternRegistryBuilder<T : CatalogType>(typeToken: TypeToken<T>) :
        AbstractCatalogTypeRegistryBuilder<T, Int, RegistryData<T>>(typeToken) {

    override fun build(): RegistryData<T> {
        val byKey = this.byKey.toImmutableMap()
        val suggestedIdMatchers = this.suggestedIdMatchers.toImmutableList()
        return RegistryData(byKey, suggestedIdMatchers)
    }
}

private abstract class AbstractCatalogTypeRegistryBuilder<T : CatalogType, I, D : RegistryData<T>>(
        val typeToken: TypeToken<T>
): InternalCatalogTypeRegistryBuilder<T, I> {

    private val typeName: String get() = this.typeToken.rawType.simpleName
    protected val suggestedIdMatchers: MutableList<(String, T) -> Boolean> = mutableListOf()
    protected val byKey: MutableMap<ResourceKey, T> = mutableMapOf()
    protected val byId: BiMap<I, T> = HashBiMap.create()
    private var allowPluginRegistrations = false

    override fun <R : T> register(type: R): R = register(type, null)
    override fun <R : T> register(internalId: I, type: R): R = register(type, internalId)

    protected open fun <R : T> register(type: R, internalId: I?): R {
        val key = type.key
        check(!this.byKey.containsKey(key)) { "There's already a $typeName registered with the key: $key" }
        if (internalId != null)
            check(!this.byId.containsKey(internalId)) { "There's already a $typeName registered with the internal id: $internalId" }
        this.byKey[key] = type
        if (internalId != null)
            this.byId[internalId] = type
        return type
    }

    fun registerPluginTypes() {
        if (!this.allowPluginRegistrations)
            return
        val cause = CauseStackManager.currentCause
        val event = object : RegisterCatalogEvent<T> {
            override fun getCause(): Cause = cause
            override fun getGame(): Game = LanternGame
            override fun getGenericType(): TypeToken<T> = typeToken
            override fun register(catalogType: T): T = this@AbstractCatalogTypeRegistryBuilder.register(catalogType)
        }
        EventManager.post(event)
    }

    abstract fun build(): D

    override fun allowPluginRegistrations() { this.allowPluginRegistrations = true }
    override fun matchSuggestedId(matcher: (suggestedId: String, type: T) -> Boolean) { this.suggestedIdMatchers += matcher }
}
