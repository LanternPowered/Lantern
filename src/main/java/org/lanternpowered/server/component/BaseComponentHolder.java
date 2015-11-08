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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.inject.InjectorFactory;
import org.lanternpowered.server.inject.Injectors;
import org.lanternpowered.server.inject.Module;
import org.lanternpowered.server.inject.Modules;
import org.lanternpowered.server.inject.ObjectSuppliers;
import org.spongepowered.api.Game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class BaseComponentHolder implements ComponentHolder {

    private static final String HOLDER = "holder";
    private static final String ALLOWED = "ignore";

    @SuppressWarnings("unchecked")
    private static final Module MODULE = Modules.builder()
            .bind(ComponentHolder.class).toProvider((target, params, info) -> {
                return (ComponentHolder) params.get(HOLDER);
            })
            .bind(Component.class).toProvider((target, params, info) -> {
                Require req = info.getAnnotation(Require.class);
                BaseComponentHolder holder = (BaseComponentHolder) params.get(HOLDER);
                if (req != null && req.autoAttach()) {
                    return holder.addComponent(info.getType(), (List<Component>) params.get(ALLOWED));
                } else {
                    return holder.getComponent(info.getType()).orElse(null);
                }
            })
            .bind(Game.class).toInstance(LanternGame.get())
            .build();

    private final Map<Class<? extends Component>, Component> components = Maps.newConcurrentMap();

    @SuppressWarnings("unchecked")
    private <T extends Component> T addComponent(Class<T> type, List<Component> allowed) {
        T component = (T) this.components.get(type);
        if (component != null) {
            return component;
        }
        component = ObjectSuppliers.get().get(type, MODULE);
        if (allowed == null) {
            allowed = ImmutableList.copyOf(this.components.values());
        }
        this.components.put(type, component);
        InjectorFactory factory = Injectors.get();
        Map<String, Object> params = ImmutableMap.of(HOLDER, this, ALLOWED, allowed);
        factory.create(type, MODULE).injectFields(component, params);
        for (Entry<Class<? extends Component>, Component> entry : this.components.entrySet()) {
            if (allowed == null || allowed.contains(entry.getValue())) {
                factory.create(entry.getKey(), MODULE).injectFields(entry.getValue(), params);
            }
        }
        return component;
    }

    @Override
    public <T extends Component> T addComponent(Class<T> type) {
        return this.addComponent(type, null);
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
