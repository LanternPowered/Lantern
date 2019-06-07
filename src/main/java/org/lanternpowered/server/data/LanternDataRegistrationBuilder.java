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
package org.lanternpowered.server.data;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.catalog.AbstractCatalogBuilder;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.translation.Translation;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class LanternDataRegistrationBuilder<M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>>
        extends AbstractCatalogBuilder<DataRegistration<M, I>, DataRegistration.Builder<M, I>> implements DataRegistration.Builder<M, I> {

    @Nullable Class<M> manipulatorClass;
    @Nullable Class<? extends M> implementationClass;
    @Nullable Class<I> immutableClass;
    @Nullable Class<? extends I> immutableImplementationClass;
    @Nullable DataManipulatorBuilder<M, I> manipulatorBuilder;

    @SuppressWarnings("unchecked")
    @Override
    public  <D extends DataManipulator<D, C>, C extends ImmutableDataManipulator<C, D>> LanternDataRegistrationBuilder<D, C> dataClass(Class<D> manipulatorClass) {
        this.manipulatorClass = (Class<M>) checkNotNull(manipulatorClass,
                "DataManipulator class cannot be null!");
        return (LanternDataRegistrationBuilder<D, C>) this;
    }

    @Override
    public LanternDataRegistrationBuilder<M, I> immutableClass(Class<I> immutableDataClass) {
        checkState(this.manipulatorClass != null,
                "DataManipulator class must be set prior to setting the immutable variant!");
        this.immutableClass = checkNotNull(immutableDataClass,
                "ImmutableDataManipulator class cannot be null!");
        return this;
    }

    @Override
    public LanternDataRegistrationBuilder<M, I> dataImplementation(Class<? extends M> implementationClass) throws IllegalStateException {
        checkState(this.manipulatorClass != null,
                "DataManipulator class must be set prior to setting the implementation!");
        this.implementationClass = checkNotNull(implementationClass,
                "DataManipulator implementation class cannot be null!");
        return this;
    }

    @Override
    public LanternDataRegistrationBuilder<M, I> immutableImplementation(Class<? extends I> immutableImplementationClass)
            throws IllegalStateException {
        checkState(this.immutableClass != null,
                "ImmutableDataManipulator class must be set prior to setting the implementation!");
        this.immutableImplementationClass = checkNotNull(immutableImplementationClass,
                "ImmutableDataManipulator implementation class cannot be null!");
        return this;
    }

    @Override
    public LanternDataRegistrationBuilder<M, I> builder(DataManipulatorBuilder<M, I> builder) {
        this.manipulatorBuilder = checkNotNull(builder, "ManipulatorBuilder cannot be null!");
        return this;
    }

    @Override
    public LanternDataRegistrationBuilder<M, I> reset() {
        super.reset();
        this.manipulatorClass = null;
        this.immutableClass = null;
        this.manipulatorBuilder = null;
        return this;
    }

    @Override
    protected DataRegistration<M, I> build(CatalogKey key, Translation name) {
        checkState(this.manipulatorBuilder != null, "ManipulatorBuilder cannot be null!");
        checkState(this.manipulatorClass != null, "DataManipulator class cannot be null!");
        checkState(this.immutableClass != null, "ImmutableDataManipulator class cannot be null!");
        final PluginContainer pluginContainer = Sponge.getPluginManager().getPlugin(key.getNamespace())
                .orElseThrow(() -> new IllegalArgumentException("Unable to find the plugin: " + key.getNamespace()));
        return new LanternDataRegistration<>(key, name, pluginContainer, this.manipulatorClass, this.implementationClass,
                this.immutableClass, this.immutableImplementationClass, this.manipulatorBuilder);
    }
}
