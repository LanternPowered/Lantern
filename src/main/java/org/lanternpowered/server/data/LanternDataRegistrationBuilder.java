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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.data.DataAlreadyRegisteredException;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.plugin.PluginContainer;

import javax.annotation.Nullable;

public final class LanternDataRegistrationBuilder<M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>>
        implements DataRegistration.Builder<M, I> {

    @Nullable Class<M> manipulatorClass;
    @Nullable Class<I> immutableClass;
    @Nullable DataManipulatorBuilder<M, I> manipulatorBuilder;
    @Nullable String id;
    @Nullable PluginContainer plugin;
    @Nullable String name;

    @SuppressWarnings("unchecked")
    @Override
    public  <D extends DataManipulator<D, C>, C extends ImmutableDataManipulator<C, D>> LanternDataRegistrationBuilder<D, C> dataClass(Class<D> manipulatorClass) {
        this.manipulatorClass = (Class<M>) checkNotNull(manipulatorClass, "DataManipulator class cannot be null!");
        return (LanternDataRegistrationBuilder<D, C>) this;
    }

    @Override
    public LanternDataRegistrationBuilder<M, I> immutableClass(Class<I> immutableDataClass) {
        checkState(this.manipulatorClass != null, "DataManipulator class must be set prior to setting the immutable variant!");
        this.immutableClass = checkNotNull(immutableDataClass, "ImmutableDataManipulator class cannot be null!");
        return this;
    }

    @Override
    public LanternDataRegistrationBuilder<M, I> manipulatorId(String id) {
        this.id = checkNotNull(id);
        checkArgument(!this.id.contains(":"), "Data ID must be formatted correctly!");
        checkArgument(!this.id.isEmpty(), "Data ID cannot be empty!");
        checkArgument(!this.id.contains(" "), "Data ID cannot contain spaces!");
        return this;
    }

    @Override
    public LanternDataRegistrationBuilder<M, I> dataName(String name) {
        this.name = checkNotNull(name);
        checkArgument(!this.name.isEmpty(), "Name cannot be empty!");
        return this;
    }

    @Override
    public LanternDataRegistrationBuilder<M, I> builder(DataManipulatorBuilder<M, I> builder) {
        this.manipulatorBuilder = checkNotNull(builder, "ManipulatorBuilder cannot be null!");
        return this;
    }

    @Override
    @SuppressWarnings("deprecation")
    public LanternDataRegistrationBuilder<M, I> from(DataRegistration<M, I> value) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot set a builder with already created DataRegistrations!");
    }

    @Override
    public LanternDataRegistrationBuilder<M, I> reset() {
        this.manipulatorClass = null;
        this.immutableClass = null;
        this.manipulatorBuilder = null;
        this.plugin = null;
        this.id = null;
        this.name = null;
        return this;
    }

    @Override
    public DataRegistration<M, I> buildAndRegister(PluginContainer container)
            throws IllegalStateException, IllegalArgumentException, DataAlreadyRegisteredException {
        this.plugin = checkNotNull(container, "container");
        checkState(this.manipulatorBuilder != null, "ManipulatorBuilder cannot be null!");
        checkState(this.manipulatorClass != null, "DataManipulator class cannot be null!");
        checkState(this.immutableClass != null, "ImmutableDataManipulator class cannot be null!");
        checkState(this.id != null, "Data ID cannot be null!");
        final LanternDataRegistration<M, I> registration = new LanternDataRegistration<>(this);
        registration.validate();
        registration.register();
        return registration;
    }
}
