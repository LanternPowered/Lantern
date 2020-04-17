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
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.inject.property.InjectedProperty
import org.lanternpowered.api.util.Named
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
