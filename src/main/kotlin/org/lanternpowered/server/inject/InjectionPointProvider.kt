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
import com.google.inject.Binding
import com.google.inject.Module
import com.google.inject.Provider
import com.google.inject.matcher.AbstractMatcher
import com.google.inject.spi.DependencyAndSource
import com.google.inject.spi.ProviderInstanceBinding
import com.google.inject.spi.ProvisionListener
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.inject.InjectionPoint
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
                    is Field -> LanternInjectionPoint(source, member.genericType.typeToken, member.annotations)
                    is Executable -> {
                        val parameterAnnotations = member.parameterAnnotations
                        val parameterTypes = member.genericParameterTypes
                        val index = dependency.parameterIndex
                        LanternInjectionPoint(source, parameterTypes[index].typeToken, parameterAnnotations[index])
                    }
                    else -> throw IllegalStateException("Unsupported Member type: " + member.javaClass.name)
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
