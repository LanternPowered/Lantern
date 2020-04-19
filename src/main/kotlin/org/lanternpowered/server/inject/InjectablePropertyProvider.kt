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
package org.lanternpowered.server.inject

import com.google.inject.Binder
import com.google.inject.BindingAnnotation
import com.google.inject.Injector
import com.google.inject.Key
import com.google.inject.MembersInjector
import com.google.inject.Module
import com.google.inject.TypeLiteral
import com.google.inject.matcher.Matchers
import com.google.inject.name.Names
import com.google.inject.spi.TypeEncounter
import com.google.inject.spi.TypeListener
import org.lanternpowered.api.inject.property.InjectedProperty
import org.lanternpowered.api.util.Named
import org.lanternpowered.api.util.type.typeLiteral
import org.lanternpowered.api.util.type.typeToken
import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.lmbda.LambdaFactory
import org.lanternpowered.lmbda.kt.privateLookupIn
import java.lang.invoke.MethodHandles
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaType

class InjectablePropertyProvider : Module, TypeListener {

    override fun configure(binder: Binder) {
        binder.bindListener(Matchers.any(), this)
    }

    override fun <I : Any> hear(type: TypeLiteral<I>, encounter: TypeEncounter<I>) {
        var javaTarget = type.rawType.uncheckedCast<Class<*>>()
        while (javaTarget != Any::class.java && !javaTarget.isArray && !javaTarget.isPrimitive) {
            try {
                val target = javaTarget.kotlin
                val lookup by lazy { lookup.privateLookupIn(javaTarget) }

                target.declaredMemberProperties.forEach { property ->
                    val field = property.javaField
                    if (field != null && InjectedProperty::class.java.isAssignableFrom(field.type)) {
                        // Found a valid field, register a member injector

                        // Extract the value type from the property
                        val valueType = property.returnType.javaType.typeToken.typeLiteral

                        // Search for binding annotations
                        val bindingAnnotations = property.annotations.filter { it.annotationClass.findAnnotation<BindingAnnotation>() != null }
                        val key = when {
                            bindingAnnotations.size > 1 -> throw IllegalStateException("Only one BindingAnnotation is allowed on: $property")
                            bindingAnnotations.size == 1 -> {
                                var bindingAnnotation = bindingAnnotations[0]
                                // Translate the kotlin named to the guice one
                                if (bindingAnnotation is Named) {
                                    bindingAnnotation = Names.named(bindingAnnotation.value)
                                }
                                Key.get(valueType, bindingAnnotation)
                            }
                            else -> Key.get(valueType)
                        }

                        val getterHandle = lookup.unreflectGetter(field)
                        val getter = LambdaFactory.createFunction<Any, Any>(getterHandle)

                        val injectorProvider = encounter.getProvider(Injector::class.java)
                        encounter.register(MembersInjector {
                            val injector = injectorProvider.get()
                            val propInstance = getter.apply(it).uncheckedCast<InjectedProperty<Any>>()
                            propInstance.inject { injector.getInstance(key) }
                        })
                    }
                }
            } catch (e: UnsupportedOperationException) {
                // Class doesn't support kotlin metadata, so assume
                // that it's not injectable.
            }
            javaTarget = javaTarget.superclass
        }
    }

    companion object {

        private val lookup = MethodHandles.lookup()
    }
}
