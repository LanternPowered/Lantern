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
package org.lanternpowered.server.data.manipulator.gen;

import static org.lanternpowered.server.data.manipulator.gen.TypeGenerator.newInternalName;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.lanternpowered.server.data.ValueCollection;
import org.lanternpowered.server.data.manipulator.DataManipulatorRegistration;
import org.lanternpowered.server.data.DataHelper;
import org.lanternpowered.server.data.manipulator.immutable.AbstractImmutableData;
import org.lanternpowered.server.data.manipulator.mutable.AbstractData;
import org.lanternpowered.server.util.DefineableClassLoader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.manipulator.immutable.ImmutableListData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableVariantData;
import org.spongepowered.api.data.manipulator.mutable.ListData;
import org.spongepowered.api.data.manipulator.mutable.VariantData;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.plugin.PluginContainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;

/**
 * This generator will attempt to auto generate {@link DataManipulator} classes
 * with the proper constructors, methods, etc. So that they can be registered
 * as a {@link DataManipulatorRegistration}.
 */
public final class DataManipulatorGenerator {

    private final DefineableClassLoader classLoader = new DefineableClassLoader();
    private final DataManipulatorRegistrationGenerator registrationGenerator = new DataManipulatorRegistrationGenerator();

    private final AbstractDataTypeGenerator abstractDataTypeGenerator = new AbstractDataTypeGenerator();
    private final AbstractListDataTypeGenerator abstractListDataTypeGenerator = new AbstractListDataTypeGenerator();
    private final AbstractVariantDataTypeGenerator abstractVariantDataTypeGenerator = new AbstractVariantDataTypeGenerator();

    @SuppressWarnings("unchecked")
    public <M extends VariantData<E, M, I>, I extends ImmutableVariantData<E, I, M>, E> DataManipulatorRegistration<M, I> newVariantRegistrationFor(
            PluginContainer pluginContainer, String id, String name, Class<M> manipulatorType, Class<I> immutableManipulatorType,
            Key<Value<E>> key, E defaultValue) {
        final Base<M, I> base = generateBase(this.abstractVariantDataTypeGenerator, pluginContainer, id, name,
                manipulatorType, immutableManipulatorType, null, null, null, null);
        try {
            base.mutableManipulatorTypeImpl.getField(AbstractVariantDataTypeGenerator.KEY).set(null, key);
            base.mutableManipulatorTypeImpl.getField(AbstractVariantDataTypeGenerator.VALUE).set(null, defaultValue);
            base.immutableManipulatorTypeImpl.getField(AbstractVariantDataTypeGenerator.KEY).set(null, key);
            base.immutableManipulatorTypeImpl.getField(AbstractVariantDataTypeGenerator.VALUE).set(null, defaultValue);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return base.supplier.get();
    }

    @SuppressWarnings("unchecked")
    public <M extends ListData<E, M, I>, I extends ImmutableListData<E, I, M>, E> DataManipulatorRegistration<M, I> newListRegistrationFor(
            PluginContainer pluginContainer, String id, String name, Class<M> manipulatorType, Class<I> immutableManipulatorType,
            Key<ListValue<E>> key, Supplier<List<E>> listSupplier) {
        final Base<M, I> base = generateBase(this.abstractListDataTypeGenerator, pluginContainer, id, name,
                manipulatorType, immutableManipulatorType, null, null, null, null);
        try {
            base.mutableManipulatorTypeImpl.getField(AbstractListDataTypeGenerator.KEY).set(null, key);
            base.mutableManipulatorTypeImpl.getField(AbstractListDataTypeGenerator.LIST_SUPPLIER).set(null, listSupplier);
            base.immutableManipulatorTypeImpl.getField(AbstractListDataTypeGenerator.KEY).set(null, key);
            base.immutableManipulatorTypeImpl.getField(AbstractListDataTypeGenerator.LIST_SUPPLIER).set(null,
                    (Supplier<List>) () -> ImmutableList.copyOf(listSupplier.get()));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return base.supplier.get();
    }

    @SuppressWarnings("unchecked")
    public <M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> DataManipulatorRegistration<M, I> newRegistrationFor(
            PluginContainer pluginContainer, String id, String name,
            Class<M> manipulatorType, Class<I> immutableManipulatorType,
            @Nullable Class<? extends M> mutableExpansion, @Nullable Class<? extends I> immutableExpansion,
            @Nullable Consumer<ValueCollection> registrationConsumer) {
        Class<?>[] classes = mutableExpansion == null ?
                new Class[] { AbstractData.class, manipulatorType } :
                new Class[] { AbstractData.class, manipulatorType, mutableExpansion };
        final List<Method> mutableMethods = findValueMethods(classes);
        classes = mutableExpansion == null ?
                new Class[] { AbstractImmutableData.class, immutableManipulatorType } :
                new Class[] { AbstractImmutableData.class, immutableManipulatorType, immutableExpansion };
        final List<Method> immutableMethods = findValueMethods(classes);

        final Base<M, I> base = generateBase(this.abstractDataTypeGenerator, pluginContainer, id, name,
                manipulatorType, immutableManipulatorType, mutableExpansion, immutableExpansion, mutableMethods, immutableMethods);
        try {
            base.mutableManipulatorTypeImpl.getField(AbstractDataTypeGenerator.REGISTRATION_CONSUMER).set(null, registrationConsumer);
            base.immutableManipulatorTypeImpl.getField(AbstractDataTypeGenerator.REGISTRATION_CONSUMER).set(null, registrationConsumer);

            final DataManipulatorRegistration<M, I> registration = base.supplier.get();
            final Set<Key<?>> requiredKeys = registration.getRequiredKeys();

            base.mutableManipulatorTypeImpl.getField(TypeGenerator.KEYS).set(null, findKeyMatches(mutableMethods, requiredKeys));
            base.immutableManipulatorTypeImpl.getField(TypeGenerator.KEYS).set(null, findKeyMatches(immutableMethods, requiredKeys));

            return registration;
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Key[] findKeyMatches(List<Method> methods, Set<Key<?>> requiredKeys) {
        Key[] keys = new Key[methods.size()];
        for (int i = 0; i < methods.size(); i++) {
            final Method method = methods.get(i);
            final String methodName = DataHelper.camelToSnake(method.getName());

            int closestDistance = Integer.MAX_VALUE;
            Key closestKey = null;

            for (Key key : requiredKeys) {
                String keyId = key.getId();
                final int index = keyId.indexOf(':');
                if (index != -1) {
                    keyId = keyId.substring(index + 1);
                }
                final int distance = StringUtils.getLevenshteinDistance(methodName, keyId);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestKey = key;
                }
            }
            if (closestKey == null) {
                throw new IllegalStateException("No key match could be found for the method: " + method);
            }

            keys[i] = closestKey;
        }
        return keys;
    }

    private static List<Method> findValueMethods(Class<?>... targetClasses) {
        final Set<Method> methods = new HashSet<>();
        for (Class<?> targetClass : targetClasses) {
            for (Method method : targetClass.getMethods()) {
                if (!Modifier.isAbstract(method.getModifiers())) {
                    continue;
                }
                if (BaseValue.class.isAssignableFrom(method.getReturnType()) &&
                        method.getParameterTypes().length == 0) {
                    boolean add = true;
                    for (Class<?> clazz : targetClasses) {
                        if (clazz != targetClass) {
                            try {
                                final Method method1 = clazz.getMethod(method.getName(), method.getParameterTypes());
                                if (!Modifier.isAbstract(method1.getModifiers())) {
                                    add = false;
                                    break;
                                }
                            } catch (NoSuchMethodException ignored) {
                            }
                        }
                    }
                    if (add) {
                        methods.add(method);
                    }
                }
            }
        }
        return new ArrayList<>(methods);
    }

    private static final class Base<M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> {

        private final Supplier<DataManipulatorRegistration<M, I>> supplier;
        private final Class<? extends M> mutableManipulatorTypeImpl;
        private final Class<? extends M> immutableManipulatorTypeImpl;

        private Base(Supplier<DataManipulatorRegistration<M, I>> supplier, Class<? extends M> mutableManipulatorTypeImpl,
                Class<? extends M> immutableManipulatorTypeImpl) {
            this.immutableManipulatorTypeImpl = immutableManipulatorTypeImpl;
            this.mutableManipulatorTypeImpl = mutableManipulatorTypeImpl;
            this.supplier = supplier;
        }
    }

    @SuppressWarnings("unchecked")
    private <M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> Base<M, I> generateBase(
            TypeGenerator typeGenerator,
            PluginContainer pluginContainer, String id, String name,
            Class<M> manipulatorType, Class<I> immutableManipulatorType,
            @Nullable Class<? extends M> mutableExpansion, @Nullable Class<? extends I> immutableExpansion,
            @Nullable List<Method> methods,
            @Nullable List<Method> immutableMethods) {
        final ClassWriter cwM = new ClassWriter(Opcodes.V1_8);
        final ClassWriter cwI = new ClassWriter(Opcodes.V1_8);

        final String mutableImplTypeName = newInternalName(manipulatorType);
        final String immutableImplTypeName = newInternalName(immutableManipulatorType);

        final String mutableImplClassName = mutableImplTypeName.replace('/', '.');
        final String immutableImplClassName = immutableImplTypeName.replace('/', '.');

        typeGenerator.generateClasses(cwM, cwI, mutableImplTypeName, immutableImplTypeName,
                manipulatorType, immutableManipulatorType, mutableExpansion, immutableExpansion, methods, immutableMethods);

        cwM.visitEnd();
        cwI.visitEnd();

        byte[] bytes = cwM.toByteArray();
        final Class<?> manipulatorTypeImpl = this.classLoader.defineClass(mutableImplClassName, bytes);
        bytes = cwI.toByteArray();
        final Class<?> immutableManipulatorTypeImpl = this.classLoader.defineClass(immutableImplClassName, bytes);

        final ClassWriter cw = new ClassWriter(Opcodes.V1_8);
        final String className = this.registrationGenerator.generate(cw,
                (Class) manipulatorType, (Class) manipulatorTypeImpl, (Class) immutableManipulatorType, (Class) immutableManipulatorTypeImpl);
        bytes = cw.toByteArray();
        final Class<?> registrationClass = this.classLoader.defineClass(className, bytes);
        return new Base(() -> {
            try {
                return registrationClass
                        .getConstructor(PluginContainer.class, String.class, String.class)
                        .newInstance(pluginContainer, id, name);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }, manipulatorTypeImpl, immutableManipulatorTypeImpl);
    }
}
