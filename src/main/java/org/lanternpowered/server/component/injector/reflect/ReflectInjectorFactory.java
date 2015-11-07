/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.component.injector.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javassist.Modifier;

import org.lanternpowered.server.component.Component;
import org.lanternpowered.server.component.ComponentHolder;
import org.lanternpowered.server.component.Inject;
import org.lanternpowered.server.component.OnAttach;
import org.lanternpowered.server.component.OnDetach;
import org.lanternpowered.server.component.Require;
import org.lanternpowered.server.component.injector.Injector;
import org.lanternpowered.server.component.injector.InjectorFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public final class ReflectInjectorFactory implements InjectorFactory {

    private final static InjectorFactory INSTANCE = new ReflectInjectorFactory();

    public static InjectorFactory instance() {
        return INSTANCE;
    }

    private final LoadingCache<Class<? extends Component>, ReflectInjector> cache =
            CacheBuilder.newBuilder().weakKeys().build(new CacheLoader<Class<? extends Component>, ReflectInjector>() {
                @Override
                public ReflectInjector load(Class<? extends Component> key) throws Exception {
                    return new ReflectInjector(key);
                }
            });

    @Override
    public ReflectInjector create(Class<? extends Component> type) {
        try {
            return this.cache.get(type);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    private final class ReflectInjector implements Injector {

        private final Constructor<?> constr;
        private final Class<? extends Component> superType;
        private final List<FieldEntry> entries;
        private final List<Field> holderFields;

        private final List<Method> attachMethods;
        private final List<Method> detachMethods;
        private final List<Method> ignoreSuperAttachMethods;
        private final List<Method> ignoreSuperDetachMethods;

        @SuppressWarnings("unchecked")
        public ReflectInjector(Class<? extends Component> type) throws Exception {
            this.constr = type.getDeclaredConstructor();
            this.constr.setAccessible(true);
            Class<?> superType = type.getSuperclass();
            this.superType = Component.class.isAssignableFrom(superType) ?
                    (Class<? extends Component>) superType : null;

            ImmutableList.Builder<FieldEntry> builder = ImmutableList.builder();
            ImmutableList.Builder<Field> holderFieldsBuilder = ImmutableList.builder();
            for (Field field : type.getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    Class<?> fieldType = field.getType();
                    if (Component.class.isAssignableFrom(fieldType)) {
                        builder.add(new FieldEntry(field, field.isAnnotationPresent(Require.class) &&
                                field.getAnnotation(Require.class).autoAttach()));
                    } else if (ComponentHolder.class.isAssignableFrom(fieldType)) {
                        field.setAccessible(true);
                        holderFieldsBuilder.add(field);
                    }
                }
            }
            this.entries = builder.build();
            this.holderFields = holderFieldsBuilder.build();
            ImmutableList.Builder<Method> attachMethodsBuilder = ImmutableList.builder();
            ImmutableList.Builder<Method> detachMethodsBuilder = ImmutableList.builder();
            ImmutableList.Builder<Method> ignoreSuperAttachMethodsBuilder = ImmutableList.builder();
            ImmutableList.Builder<Method> ignoreSuperDetachMethodsBuilder = ImmutableList.builder();
            for (Method method : type.getDeclaredMethods()) {
                if (method.isAnnotationPresent(OnAttach.class) && method.getParameterTypes().length == 0) {
                    method.setAccessible(true);
                    attachMethodsBuilder.add(method);
                    // Avoid that overridden methods will be called multiple times.
                    if (this.superType != null && !Modifier.isPrivate(method.getModifiers())) {
                        try {
                            Method method0 = this.superType.getDeclaredMethod(method.getName());
                            ignoreSuperAttachMethodsBuilder.add(method0);
                        } catch (NoSuchMethodException e) {
                        }
                    }
                }
                if (method.isAnnotationPresent(OnDetach.class) && method.getParameterTypes().length == 0) {
                    method.setAccessible(true);
                    attachMethodsBuilder.add(method);
                    // Avoid that overridden methods will be called multiple times.
                    if (this.superType != null && !Modifier.isPrivate(method.getModifiers())) {
                        try {
                            Method method0 = this.superType.getDeclaredMethod(method.getName());
                            ignoreSuperDetachMethodsBuilder.add(method0);
                        } catch (NoSuchMethodException e) {
                        }
                    }
                }
            }
            this.attachMethods = attachMethodsBuilder.build();
            this.detachMethods = detachMethodsBuilder.build();
            this.ignoreSuperAttachMethods = ignoreSuperAttachMethodsBuilder.build();
            this.ignoreSuperDetachMethods = ignoreSuperDetachMethodsBuilder.build();
        }

        @Override
        public Component create() {
            try {
                return (Component) this.constr.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public void inject(Component component, ComponentHolder holder) {
            for (FieldEntry entry : this.entries) {
                Component componentToInject;
                if (entry.autoAttach) {
                    componentToInject = holder.addComponent((Class) entry.type);
                } else {
                    componentToInject = (Component) holder.getComponent((Class) entry.type).orElse(null);
                }
                try {
                    entry.field.set(component, componentToInject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (Field field : this.holderFields) {
                try {
                    field.set(component, holder);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (this.superType != null) {
                ReflectInjectorFactory.this.create(this.superType).inject(component, holder);
            }
        }

        @Override
        public void inject(Component component, Component componentToInject) {
            for (FieldEntry entry : this.entries) {
                if (entry.type.isInstance(componentToInject)) {
                    try {
                        entry.field.set(component, componentToInject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (this.superType != null) {
                ReflectInjectorFactory.this.create(this.superType).inject(component, componentToInject);
            }
        }

        @Override
        public void attach(Component component) {
            try {
                this.attach(component, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void attach(Component component, List<Method> ignore) throws Exception {
            for (Method method : this.attachMethods) {
                if (ignore != null && ignore.contains(method)) {
                    continue;
                }
                method.invoke(component);
            }
            if (this.superType != null) {
                if (ignore == null) {
                    ignore = Lists.newArrayList(this.ignoreSuperAttachMethods);
                } else {
                    ignore.addAll(this.ignoreSuperAttachMethods);
                }
                ReflectInjectorFactory.this.create(this.superType).attach(component, ignore);
            }
        }

        @Override
        public void detach(Component component) {
            try {
                this.detach(component, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void detach(Component component, List<Method> ignore) throws Exception {
            for (Method method : this.detachMethods) {
                if (ignore != null && ignore.contains(method)) {
                    continue;
                }
                method.invoke(component);
            }
            if (this.superType != null) {
                if (ignore == null) {
                    ignore = Lists.newArrayList(this.ignoreSuperDetachMethods);
                } else {
                    ignore.addAll(this.ignoreSuperDetachMethods);
                }
                ReflectInjectorFactory.this.create(this.superType).detach(component, ignore);
            }
        }

        private class FieldEntry {

            final Field field;
            final Class<?> type;
            final boolean autoAttach;

            public FieldEntry(Field field, boolean autoAttach) {
                this.type = field.getType();
                this.autoAttach = autoAttach;
                this.field = field;
                this.field.setAccessible(true);
            }
        }
    }
}
