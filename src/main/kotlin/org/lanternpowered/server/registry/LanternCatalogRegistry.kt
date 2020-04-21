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

import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.api.registry.CatalogRegistry
import org.lanternpowered.api.registry.CatalogTypeRegistry
import org.lanternpowered.api.registry.DuplicateRegistrationException
import org.lanternpowered.api.util.optional.optional
import org.spongepowered.api.CatalogKey
import java.util.Optional
import java.util.function.Supplier
import java.util.stream.Stream
import kotlin.reflect.KClass
import kotlin.streams.asSequence

object LanternCatalogRegistry : CatalogRegistry {

    private val registriesByType = mutableMapOf<Class<*>, CatalogTypeRegistry<*>>()

    /**
     * Makes sure that all the registries are loaded.
     */
    fun ensureLoaded() {
        // Accessing one of the methods loads the registry
        this.registriesByType.values.forEach { it.all }
    }

    /**
     * Registers the [CatalogTypeRegistry].
     *
     * @param registry The catalog type registry
     * @throws DuplicateRegistrationException If there's already a registry for the target type
     */
    fun register(registry: CatalogTypeRegistry<*>) {
        val type = registry.typeToken.rawType
        if (type in this.registriesByType)
            throw DuplicateRegistrationException("The type ${type.simpleName} is already registered.")
        this.registriesByType[type] = registry
    }

    fun <T : CatalogType> getRegistry(catalogClass: KClass<T>): CatalogTypeRegistry<T>? =
            getRegistry(catalogClass.java)

    @Suppress("UNCHECKED_CAST")
    fun <T : CatalogType> getRegistry(catalogClass: Class<T>): CatalogTypeRegistry<T>? =
            this.registriesByType[catalogClass] as CatalogTypeRegistry<T>?

    private fun <T : CatalogType> requireRegistry(catalogClass: Class<T>): CatalogTypeRegistry<T> =
            getRegistry(catalogClass) ?: error("No registry is registered for the type ${catalogClass.simpleName}")

    private fun <T : CatalogType> requireRegistry(catalogClass: KClass<T>): CatalogTypeRegistry<T> =
            requireRegistry(catalogClass.java)

    override fun <T : CatalogType, E : T> provideSupplier(catalogClass: KClass<T>, suggestedId: String): Supplier<E> =
            provideSupplier(catalogClass.java, suggestedId)

    @Suppress("UNCHECKED_CAST")
    override fun <T : CatalogType, E : T> provideSupplier(catalogClass: Class<T>, suggestedId: String): Supplier<E> =
            requireRegistry(catalogClass).provideSupplier(suggestedId) as Supplier<E>

    override fun <T : CatalogType> get(typeClass: KClass<T>, key: CatalogKey): T? = getNullable(typeClass.java, key)
    override fun <T : CatalogType> get(typeClass: Class<T>, key: CatalogKey): Optional<T> = getNullable(typeClass, key).optional()

    private fun <T : CatalogType> getNullable(typeClass: Class<T>, key: CatalogKey): T? = getRegistry(typeClass)?.get(key)
    override fun <T : CatalogType> getAllOf(typeClass: KClass<T>): Sequence<T> = getAllOf(typeClass.java).asSequence()
    override fun <T : CatalogType> getAllOf(typeClass: Class<T>): Stream<T> = getRegistry(typeClass)?.all?.stream() ?: emptyList<T>().stream()

    override fun <T : CatalogType> getAllFor(typeClass: Class<T>, namespace: String): Stream<T> =
            getAllOf(typeClass).filter { type -> type.key.namespace == namespace }
}
