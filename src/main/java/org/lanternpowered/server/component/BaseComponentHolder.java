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
package org.lanternpowered.server.component;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.lanternpowered.server.component.injector.Injector;
import org.lanternpowered.server.component.injector.reflect.ReflectInjectorFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

public class BaseComponentHolder implements ComponentHolder {

    private final Map<Class<? extends Component>, Component> components = Maps.newConcurrentMap();

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Component> T addComponent(Class<T> type) {
        T component = (T) this.components.get(type);
        if (component != null) {
            return component;
        }
        Injector injector = ReflectInjectorFactory.instance().create(type);
        component = (T) injector.create();
        this.components.put(type, component);
        injector.inject(component, this);
        for (Entry<Class<? extends Component>, Component> entry : this.components.entrySet()) {
            ReflectInjectorFactory.instance().create(entry.getKey()).inject(entry.getValue(), component);
        }
        injector.attach(component);
        return component;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Component> Optional<T> getComponent(Class<T> type) {
        T component = (T) this.components.get(type);
        if (component == null) {
            for (Component component0 : this.components.values()) {
                if (type.isInstance(component0)) {
                    component = (T) component0;
                    break;
                }
            }
        }
        return Optional.ofNullable(component);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Component> Collection<T> getAllComponents(Class<T> type) {
        ImmutableList.Builder<T> builder = ImmutableList.builder();
        for (Component component : this.components.values()) {
            if (type.isInstance(component)) {
                builder.add((T) component);
            }
        }
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Component> Optional<T> getExactComponent(Class<T> type) {
        return Optional.ofNullable((T) this.components.get(type));
    }

    @Override
    public <T extends Component> Optional<T> removeComponent(Class<T> type) {
        // TODO Auto-generated method stub
        return null;
    }
}
