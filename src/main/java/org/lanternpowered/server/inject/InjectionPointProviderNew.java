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
package org.lanternpowered.server.inject;

import com.google.common.reflect.TypeToken;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.HasDependencies;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.spi.ProvisionListener;
import io.netty.util.concurrent.FastThreadLocal;
import org.lanternpowered.server.util.FastThreadLocals;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

public final class InjectionPointProviderNew implements Module, ProvisionListener, Provider<InjectionPoint> {

    private final FastThreadLocal<List<Binding<?>>> stack = FastThreadLocals.withInitial(ArrayList::new);

    @Nullable
    @Override
    public InjectionPoint get() {
        final List<Binding<?>> stack = this.stack.get();
        Binding<?> binding = stack.get(0);
        // This should be impossible
        if (!(binding instanceof ProviderInstanceBinding)) {
            return null;
        }
        System.out.println("InjectionPoint [");
        System.out.println("  Stack [");
        stack.forEach(e -> System.out.println("    " + e));
        System.out.println("  ]");
        // Get the binding where our injection point is,
        // now how to get the target field or parameter
        // from the binding?
        // You can retrieve all the dependencies from the binding
        // (class binding, instance provider binding, ...),
        // but it is not possible to know in which order they will
        // be injected making it impossible to know which field
        // or parameter will be injected next
        binding = stack.get(2);
        // Must have dependencies, otherwise not supported
        if (!(binding instanceof HasDependencies)) {
            new IllegalStateException("Attempted to retrieve a InjectionPoint from a Binding without dependencies").printStackTrace();
            return null;
        }
        final Set<Dependency<?>> dependencies = ((HasDependencies) binding).getDependencies();
        System.out.println("  Dependencies [");
        dependencies.forEach(e -> System.out.println("    " + e));
        System.out.println("  ]");
        System.out.println("]");
        // TODO: Which Dependency in the Set do we need?
        return constructInjectionPoint(dependencies.iterator().next());
    }

    @Override
    public <T> void onProvision(ProvisionInvocation<T> provision) {
        final List<Binding<?>> stack = this.stack.get();
        stack.add(0, provision.getBinding());
        try {
            provision.provision();
        } finally {
            stack.remove(0);
        }
    }

    @Nullable
    private static InjectionPoint constructInjectionPoint(Dependency<?> dependency) {
        final com.google.inject.spi.InjectionPoint spiInjectionPoint = dependency.getInjectionPoint();
        if (spiInjectionPoint == null) {
            return null;
        }
        final TypeToken<?> source = TypeToken.of(spiInjectionPoint.getDeclaringType().getType());
        final Member member = spiInjectionPoint.getMember();
        final InjectionPoint injectionPoint;
        if (member instanceof Field) {
            final Field field = (Field) member;
            injectionPoint = new InjectionPoint(source, TypeToken.of(field.getGenericType()), field.getAnnotations());
        } else if (member instanceof Executable) {
            final Executable executable = (Executable) member;
            final Annotation[][] parameterAnnotations = executable.getParameterAnnotations();
            final Type[] parameterTypes = executable.getGenericParameterTypes();
            final int index = dependency.getParameterIndex();
            injectionPoint = new InjectionPoint(source, TypeToken.of(parameterTypes[index]), parameterAnnotations[index]);
        } else {
            throw new IllegalStateException("Unsupported Member type: " + member.getClass().getName());
        }
        return injectionPoint;
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(InjectionPoint.class).toProvider(this);
        binder.bindListener(Matchers.any(), this);
    }
}
