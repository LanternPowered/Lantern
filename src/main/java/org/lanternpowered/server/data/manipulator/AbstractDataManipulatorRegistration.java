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
package org.lanternpowered.server.data.manipulator;

import static com.google.common.base.Preconditions.checkArgument;

import org.lanternpowered.server.data.LanternDataRegistration;
import org.lanternpowered.server.data.manipulator.immutable.AbstractImmutableData;
import org.lanternpowered.server.data.manipulator.immutable.IImmutableDataManipulator;
import org.lanternpowered.server.data.manipulator.mutable.AbstractData;
import org.lanternpowered.server.data.manipulator.mutable.IDataManipulator;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.persistence.DataBuilder;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.Set;

import javax.annotation.Nullable;

public abstract class AbstractDataManipulatorRegistration<M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>>
        extends LanternDataRegistration<M, I> implements DataManipulatorRegistration<M, I> {

    private final Set<Key<?>> requiredKeys;
    private final DataBuilder<I> immutableDataBuilder;

    protected AbstractDataManipulatorRegistration(PluginContainer plugin, String id, String name,
            Class<M> manipulatorClass, Class<I> immutableClass) {
        this(plugin, id, name, manipulatorClass, immutableClass, null);
    }

    AbstractDataManipulatorRegistration(PluginContainer plugin, String id, String name,
            Class<M> manipulatorClass, Class<I> immutableClass, @Nullable Set<Key<?>> requiredKeys) {
        super(plugin, id, name, manipulatorClass, immutableClass, null);
        this.requiredKeys = requiredKeys == null ? createMutable().getKeys() : requiredKeys;
        this.immutableDataBuilder = new RegistrationImmutableManipulatorDataBuilder(immutableClass, 1);
    }

    @Override
    protected void register() {
        super.register();
    }

    @Override
    protected void validate() {
        final M manipulator = createMutable();
        checkArgument(manipulator instanceof IDataManipulator,
                "The mutable manipulator implementation must implement IDataManipulator.");
        //noinspection unchecked
        final Class<M> manipulatorType1 = ((IDataManipulator<M, I>) manipulator).getMutableType();
        checkArgument(manipulatorType1 == getManipulatorClass(),
                "The mutable data manipulator returns a different manipulator type, expected %s, but got %s",
                getManipulatorClass().getName(), manipulatorType1.getName());
        final I immutableManipulator = createImmutable();
        checkArgument(immutableManipulator instanceof IImmutableDataManipulator,
                "The immutable manipulator implementation must implement IImmutableData.");
        //noinspection unchecked
        final Class<I> immutableManipulatorType1 = ((IImmutableDataManipulator<I, M>) immutableManipulator).getImmutableType();
        checkArgument(immutableManipulatorType1 == getImmutableManipulatorClass(),
                "The immutable data manipulator returns a different manipulator type, expected %s, but got %s",
                getImmutableManipulatorClass().getName(), immutableManipulatorType1.getName());
        super.validate();
    }

    @Override
    public DataBuilder<I> getImmutableDataBuilder() {
        return this.immutableDataBuilder;
    }

    @Override
    public Set<Key<?>> getRequiredKeys() {
        return this.requiredKeys;
    }

    @Override
    protected DataManipulatorBuilder<M, I> createDataManipulatorBuilder() {
        return new RegistrationManipulatorDataBuilder(getManipulatorClass(), 1);
    }

    private final class RegistrationManipulatorDataBuilder extends AbstractData.AbstractManipulatorDataBuilder<M, I> {

        RegistrationManipulatorDataBuilder(Class<M> requiredClass, int supportedVersion) {
            super(requiredClass, supportedVersion);
        }

        @Override
        public M create() {
            return createMutable();
        }
    }

    private final class RegistrationImmutableManipulatorDataBuilder extends AbstractImmutableData.AbstractImmutableManipulatorDataBuilder<I, M> {

        RegistrationImmutableManipulatorDataBuilder(Class<I> requiredClass, int supportedVersion) {
            super(requiredClass, supportedVersion);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected I create() {
            return createImmutable();
        }
    }
}
