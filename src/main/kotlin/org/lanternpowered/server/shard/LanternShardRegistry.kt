package org.lanternpowered.server.shard

import org.lanternpowered.api.ext.*
import org.lanternpowered.api.shard.RequiredHolderOfTypeProperty
import org.lanternpowered.api.shard.Shard
import org.lanternpowered.api.shard.ShardRegistry
import org.lanternpowered.api.shard.ShardType
import java.lang.reflect.TypeVariable
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaType

object LanternShardRegistry : ShardRegistry {

    /**
     * [ShardType]s mapped by their [Shard] type.
     */
    private val typeCache = ConcurrentHashMap<Class<*>, ShardType<*>>()

    /**
     * [ShardType]s mapped by all the [Shard] subclasses, includes type classes and implementations.
     */
    private val resolvedTypeCache = ConcurrentHashMap<Class<*>, ShardType<*>>()

    private val requirementsCache = ConcurrentHashMap<Class<*>, ShardRequirements>()

    private val shardTypeVariable = Shard::class.java.typeParameters[0]

    override fun <T : Shard<T>> getType(shardClass: Class<out T>): ShardType<T> {
        return this.resolvedTypeCache.computeIfAbsent(shardClass) { _ ->
            // Extract the shard class that represents the type from the target
            val type = shardClass.typeToken.resolveType(this.shardTypeVariable)
            // Must be resolved, so not a type variable
            check(type.type !is TypeVariable<*>) { "The shard type of ${shardClass.name} must be resolved, and not $type." }
            // Create the shard type
            this.typeCache.computeIfAbsent(type.rawType.uncheckedCast<Class<T>>()) { LanternShardType<T>(it.uncheckedCast()) }
        }.uncheckedCast()
    }

    private fun buildRequirements(shardClass: Class<out Shard<*>>): ShardRequirements {
        // Extract all the holder requirements from the shard class
        val requiredHolderTypes = mutableListOf<Class<*>>()

        var target: Class<*> = shardClass
        while (target != Shard::class.java) {
            try {
                target.kotlin.declaredMemberProperties.forEach { property ->
                    val field = property.javaField
                    if (field != null && RequiredHolderOfTypeProperty::class.java.isAssignableFrom(field.type)) {
                        // Extract the holder type from the property
                        requiredHolderTypes += property.returnType.javaType.typeToken.rawType
                    }
                }
            } catch (ignored: UnsupportedOperationException) {
            }
            target = target.superclass
        }

        // Validate the required holder types, to check whether there
        // are multiple non interface requirements, which is impossible
        // to exist.

        val nonInterfaceHolders = requiredHolderTypes.filter { !it.isInterface }
        check(nonInterfaceHolders.size <= 1) {
            """A impossible required holder structure is found, the following holder classes in ${target.name} are causing conflicts:
                ${nonInterfaceHolders.joinToString("\n") { it.name }}
            """
        }

        return ShardRequirements(shardClass, requiredHolderTypes)
    }

    override fun <T : Shard<T>, I : T> registerDefault(shardType: ShardType<T>, defaultImpl: Class<I>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
