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
package org.lanternpowered.server.util

import org.lanternpowered.api.util.type.TypeToken
import org.lanternpowered.api.util.uncheckedCast
import java.lang.reflect.GenericArrayType
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import java.lang.reflect.WildcardType

@Suppress("DuplicatedCode", "UnnecessaryVariable", "MoveVariableDeclarationIntoWhen")
object TypeTokenHelper {

    fun isAssignable(type: TypeToken<*>, toType: TypeToken<*>): Boolean =
            isAssignable(type.type, toType.type)

    fun isAssignable(type: Type, toType: Type): Boolean =
            isAssignable(type, toType, null, 0)

    private fun isAssignable(type: Type, toType: Type, parent: Type?, index: Int): Boolean {
        return when {
            type == toType -> true
            toType is Class<*> -> isAssignable(type, toType, parent, index)
            toType is ParameterizedType -> isAssignable(type, toType, parent, index)
            toType is TypeVariable<*> -> isAssignable(type, toType, parent, index)
            toType is WildcardType -> isAssignable(type, toType, parent, index)
            toType is GenericArrayType -> isAssignable(type, toType, parent, index)
            else -> throw IllegalStateException("Unsupported type: $type")
        }
    }

    private fun isAssignable(type: Type, toType: Class<*>, parent: Type?, index: Int): Boolean {
        if (type is Class<*>) {
            val other = type
            val toEnclosing = toType.enclosingClass
            if (toEnclosing != null && !Modifier.isStatic(toType.modifiers)) {
                val otherEnclosing = other.enclosingClass
                if (otherEnclosing == null || !isAssignable(otherEnclosing, toEnclosing, null, 0)) {
                    return false
                }
            }
            return toType.isAssignableFrom(other)
        }
        if (type is ParameterizedType) {
            val other = type
            val toEnclosing = toType.enclosingClass
            if (toEnclosing != null && !Modifier.isStatic(toType.modifiers)) {
                val otherEnclosing = other.ownerType
                if (otherEnclosing == null || !isAssignable(otherEnclosing, toEnclosing, null, 0)) {
                    return false
                }
            }
            return toType.isAssignableFrom(other.rawType as Class<*>)
        }
        if (type is TypeVariable<*>) {
            return allSupertypes(toType, type.bounds)
        }
        if (type is WildcardType) {
            val other = type
            return allWildcardSupertypes(toType, other.upperBounds, parent, index) &&
                    allAssignable(toType, other.lowerBounds)
        }
        if (type is GenericArrayType) {
            return toType == Any::class.java || toType.isArray &&
                    isAssignable(type.genericComponentType, toType.componentType, parent, index)
        }
        throw IllegalStateException("Unsupported type: $type")
    }

    private fun isAssignable(type: Type, toType: ParameterizedType, parent: Type?, index: Int): Boolean {
        if (type is Class<*>) {
            val otherRaw = type
            val toRaw: Class<Any> = toType.rawType.uncheckedCast()
            if (!toRaw.isAssignableFrom(otherRaw))
                return false
            val toEnclosing = toType.ownerType
            if (toEnclosing != null && !Modifier.isStatic(toRaw.modifiers)) {
                val otherEnclosing = otherRaw.enclosingClass
                if (otherEnclosing == null || !isAssignable(otherEnclosing, toEnclosing, null, 0))
                    return false
            }
            // Check if the default generic parameters match the parameters
            // of the parameterized type
            val toTypes: Array<out Type> = toType.actualTypeArguments
            val types = if (otherRaw == toRaw) {
                otherRaw.typeParameters.uncheckedCast()
            } else {
                // Get the type parameters based on the super class
                val other = TypeToken.of(type).getSupertype(toRaw).type as ParameterizedType
                other.actualTypeArguments
            }
            if (types.size != toTypes.size)
                return false
            for (i in types.indices) {
                if (!isAssignable(types[i], toTypes[i], type, i))
                    return false
            }
            return true
        }
        if (type is ParameterizedType) {
            var other = type
            val otherRaw = other.rawType as Class<*>
            val toRaw: Class<Any> = toType.rawType.uncheckedCast()
            if (!toRaw.isAssignableFrom(otherRaw))
                return false
            val toEnclosing = toType.ownerType
            if (toEnclosing != null && !Modifier.isStatic(toRaw.modifiers)) {
                val otherEnclosing = other.ownerType
                if (otherEnclosing == null || !isAssignable(otherEnclosing, toEnclosing, null, 0))
                    return false
            }
            val types: Array<Type>
            if (otherRaw == toRaw) {
                types = other.actualTypeArguments
            } else {
                // Get the type parameters based on the super class
                other = TypeToken.of(other).getSupertype(toRaw).type as ParameterizedType
                types = other.actualTypeArguments
            }
            val toTypes = toType.actualTypeArguments
            if (types.size != toTypes.size)
                return false
            for (i in types.indices) {
                if (!isAssignable(types[i], toTypes[i], other, i))
                    return false
            }
            return true
        }
        if (type is TypeVariable<*>)
            return allSupertypes(toType, type.bounds)
        if (type is WildcardType) {
            val other = type
            return allWildcardSupertypes(toType, other.upperBounds, parent, index) &&
                    allAssignable(toType, other.lowerBounds)
        }
        if (type is GenericArrayType) {
            val rawType = toType.rawType as Class<*>
            return rawType == Any::class.java || rawType.isArray &&
                    isAssignable(type.genericComponentType, rawType.componentType, parent, index)
        }
        throw IllegalStateException("Unsupported type: $type")
    }

    private fun isAssignable(type: Type, toType: TypeVariable<*>, parent: Type?, index: Int): Boolean {
        return allAssignable(type, toType.bounds)
    }

    private fun isAssignable(type: Type, toType: WildcardType, parent: Type?, index: Int): Boolean {
        return allWildcardAssignable(type, toType.upperBounds, parent, index) &&
                allSupertypes(type, toType.lowerBounds)
    }

    private fun isAssignable(type: Type, toType: GenericArrayType, parent: Type?, index: Int): Boolean {
        if (type is Class<*>) {
            val other = type
            return other.isArray && isAssignable(other.componentType, toType.genericComponentType, parent, index)
        }
        if (type is ParameterizedType) {
            val rawType = type.rawType as Class<*>
            return rawType.isArray && isAssignable(rawType.componentType, toType.genericComponentType, parent, index)
        }
        if (type is TypeVariable<*>)
            return allSupertypes(toType, type.bounds)
        if (type is WildcardType) {
            val other = type
            return allWildcardSupertypes(toType, other.upperBounds, parent, index) &&
                    allAssignable(toType, other.lowerBounds)
        }
        if (type is GenericArrayType)
            return isAssignable(type.genericComponentType, toType.genericComponentType, parent, index)
        throw IllegalStateException("Unsupported type: $type")
    }

    private fun processBounds(bounds: Array<Type>, parent: Type?, index: Int): Array<Type> {
        @Suppress("NAME_SHADOWING")
        var bounds = bounds
        if (bounds.isEmpty() ||
                bounds.size == 1 && bounds[0] == Any::class.java) {
            var theClass: Class<*>? = null
            if (parent is Class<*>) {
                theClass = parent
            } else if (parent is ParameterizedType) {
                theClass = parent.rawType as Class<*>
            }
            if (theClass != null) {
                val typeVariables: Array<out TypeVariable<out Class<out Any>>> = theClass.typeParameters
                bounds = typeVariables[index].bounds
                // Strip the new bounds down
                for (i in bounds.indices) {
                    if (bounds[i] is TypeVariable<*> ||
                            bounds[i] is WildcardType) {
                        bounds[i] = Any::class.java
                    } else if (bounds[i] is ParameterizedType) {
                        bounds[i] = (bounds[i] as ParameterizedType).rawType
                    } else if (bounds[i] is GenericArrayType) {
                        val component = (bounds[i] as GenericArrayType).genericComponentType
                        val componentClass = when (component) {
                            is Class<*> -> component
                            is ParameterizedType -> component.rawType as Class<*> // Is this even possible?
                            else -> Any::class.java
                        }
                        bounds[i] = if (componentClass == Any::class.java) Array<Any>::class.java else
                            java.lang.reflect.Array.newInstance(componentClass, 0).javaClass // Get the array class
                    }
                }
            }
        }
        return bounds
    }

    private fun allWildcardSupertypes(type: Type, bounds: Array<Type>, parent: Type?, index: Int): Boolean =
            allSupertypes(type, processBounds(bounds, parent, index))

    private fun allWildcardAssignable(type: Type, bounds: Array<Type>, parent: Type?, index: Int): Boolean =
            allAssignable(type, processBounds(bounds, parent, index))

    private fun allAssignable(type: Type, bounds: Array<Type>): Boolean {
        for (toType in bounds) {
            // Skip the Object class
            if (!isAssignable(type, toType, null, 0))
                return false
        }
        return true
    }

    private fun allSupertypes(type: Type, bounds: Array<Type>): Boolean {
        for (toType in bounds) {
            // Skip the Object class
            if (!isAssignable(toType, type, null, 0))
                return false
        }
        return true
    }
}
