/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
import org.lanternpowered.server.inject.Injector;
import org.lanternpowered.server.inject.Injectors;
import org.lanternpowered.server.inject.MethodSpec;
import org.lanternpowered.server.inject.Modules;
import org.lanternpowered.server.inject.ParameterSpec;
import org.spongepowered.api.Game;
import org.spongepowered.api.util.GuavaCollectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class BaseComponentHolder implements ComponentHolder {

    private static final String HOLDER = "holder";
    // The allowed values is passed through to avoid
    // injecting already injected objects
    private static final String ALLOWED = "allowed";

    private static final MethodSpec<Void> ON_ATTACH = MethodSpec.ofAnnotated(Void.class, OnAttach.class);
    private static final MethodSpec<Void> ON_DETACH = MethodSpec.ofAnnotated(Void.class, OnDetach.class);

    @SuppressWarnings("unchecked")
    private static final Injector INJECTOR = Injectors.get().create(Modules.builder()
            .bind(ParameterSpec.of(ComponentHolder.class), (target, params, info) -> {
                return (ComponentHolder) params.get(HOLDER);
            })
            .bind(ParameterSpec.of(Component.class), (target, params, info) -> {
                Require req = info.getAnnotation(Require.class);
                BaseComponentHolder holder = (BaseComponentHolder) params.get(HOLDER);
                if (req != null && req.autoAttach()) {
                    return holder.addComponent(info.getType(), (List<Component>) params.get(ALLOWED));
                } else {
                    return holder.getComponent(info.getType()).orElse(null);
                }
            })
            .bindInstance(ParameterSpec.of(Game.class), LanternGame.get())
            .bind(ON_ATTACH)
            .bind(ON_DETACH)
            .build());

    private final Map<Class<? extends Component>, Component> components = Maps.newConcurrentMap();

    @SuppressWarnings("unchecked")
    private <T extends Component> T addComponent(Class<? extends T> type, List<Component> allowed) {
        T component = (T) this.components.get(type);
        if (component != null) {
            return component;
        }
        component = INJECTOR.instantiate(type);
        if (allowed == null) {
            allowed = ImmutableList.copyOf(this.components.values());
        }
        // Check if a component was created while instantiating a new component instance
        T component0 = (T) this.components.putIfAbsent(type, component);
        if (component0 != null) {
            return component0;
        }
        Map<String, Object> params = ImmutableMap.of(HOLDER, this, ALLOWED, allowed);
        INJECTOR.injectObjects(component, params);
        for (Entry<Class<? extends Component>, Component> entry : this.components.entrySet()) {
            if (allowed == null || allowed.contains(entry.getValue())) {
                INJECTOR.injectObjects(entry.getValue(), params, Component.class);
            }
        }
        INJECTOR.injectMethod(component, ON_ATTACH);
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
        return this.components.values().stream().filter(component -> type.isInstance(component))
                .map(component -> (T) component).collect(GuavaCollectors.toImmutableList());
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
