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
import com.google.inject.Binding
import com.google.inject.Module
import com.google.inject.Provider
import com.google.inject.matcher.AbstractMatcher
import com.google.inject.spi.DependencyAndSource
import com.google.inject.spi.ProviderInstanceBinding
import com.google.inject.spi.ProvisionListener
import org.lanternpowered.api.inject.InjectionPoint
import org.lanternpowered.api.util.type.typeToken
import java.lang.reflect.Executable
import java.lang.reflect.Field

/**
 * Allows injecting the [InjectionPoint] in [Provider]s.
 */
class InjectionPointProvider : AbstractMatcher<Binding<*>>(), Module, ProvisionListener, Provider<InjectionPoint> {

    private var injectionPoint: InjectionPoint? = null

    override fun get(): InjectionPoint? = this.injectionPoint
    override fun matches(binding: Binding<*>) = binding is ProviderInstanceBinding<*> && binding.userSuppliedProvider === this

    override fun <T> onProvision(provision: ProvisionListener.ProvisionInvocation<T>) {
        try {
            this.injectionPoint = findInjectionPoint(provision.dependencyChain)
            provision.provision()
        } finally {
            this.injectionPoint = null
        }
    }

    private fun findInjectionPoint(dependencyChain: List<DependencyAndSource>): InjectionPoint? {
        if (dependencyChain.size < 3) {
            AssertionError("Provider is not included in the dependency chain").printStackTrace()
        }

        // @Inject InjectionPoint is the last, so we can skip it
        for (i in dependencyChain.size - 2 downTo 0) {
            val dependency = dependencyChain[i].dependency ?: return null
            val spiInjectionPoint = dependency.injectionPoint
            if (spiInjectionPoint != null) {
                val source = spiInjectionPoint.declaringType.type.typeToken
                val member = spiInjectionPoint.member
                return when (member) {
                    is Field -> LanternInjectionPoint.Field(source, member.genericType.typeToken, member.annotations, member)
                    is Executable -> {
                        val parameterAnnotations = member.parameterAnnotations
                        val parameterTypes = member.genericParameterTypes
                        val index = dependency.parameterIndex
                        LanternInjectionPoint.Parameter(source, parameterTypes[index].typeToken,
                                parameterAnnotations[index], member, index)
                    }
                    else -> throw IllegalStateException("Unsupported Member type: ${member.javaClass.name}")
                }
            }
        }

        return null
    }

    override fun configure(binder: Binder) {
        binder.bind(InjectionPoint::class.java).toProvider(this)
        binder.bindListener(this, this)
    }
}
