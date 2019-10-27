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
package org.lanternpowered.server.block

import org.lanternpowered.api.data.property.Property
import org.lanternpowered.api.data.property.PropertyHolder
import org.lanternpowered.api.data.property.PropertyProvider
import org.lanternpowered.server.data.property.PropertyProviderBuilder
import org.lanternpowered.server.data.property.PropertyRegistry
import org.spongepowered.api.block.BlockState
import org.spongepowered.api.block.BlockType

class LanternBlockTypePropertyRegistryBuilder(
        val backing: PropertyRegistry<BlockType>
) : BlockTypePropertyRegistryBuilder() {

    override fun forStates(fn: PropertyRegistry<BlockState>.() -> Unit) {
        forHolder(fn)
    }

    override fun <H : PropertyHolder> forHolder(holderType: Class<H>) = this.backing.forHolder(holderType)

    override val providers: Map<Property<*>, PropertyProvider<*>>
        get() = this.backing.providers

    override fun <V : Any> register(property: Property<V>, constant: V) {
        this.backing.register(property, constant)
    }

    override fun <V : Any> registerProvider(
            property: Property<V>, propertyProvider: PropertyProvider<V>) {
        this.backing.registerProvider(property, propertyProvider)
    }

    override fun <V : Any> registerProvider(
            property: Property<V>, fn: PropertyProviderBuilder<V, BlockType>.(property: Property<V>) -> Unit) {
        this.backing.registerProvider(property, fn)
    }

    override fun <V : Any> getProvider(property: Property<V>) = this.backing.getProvider(property)
    override fun getIntProvider(property: Property<Int>) = this.backing.getIntProvider(property)
    override fun getDoubleProvider(property: Property<Double>) = this.backing.getDoubleProvider(property)
}
