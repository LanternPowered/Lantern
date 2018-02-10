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
package org.lanternpowered.server.shards.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.shards.Shard;
import org.lanternpowered.server.shards.ShardHolder;
import org.lanternpowered.server.shards.InjectionRegistry;
import org.lanternpowered.server.shards.dependency.Requirement;
import org.lanternpowered.server.shards.event.Shardevent;
import org.lanternpowered.server.shards.event.ShardeventBus;
import org.lanternpowered.server.shards.internal.event.LanternShardeventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * A simple container to hold {@link Shard}s
 * and handle {@link Shardevent}s.
 */
@SuppressWarnings({"unchecked", "ConstantConditions"})
public final class ComponentContainer {

    private final ShardHolder holder;
    private final ShardeventBus shardeventBus = new LanternShardeventBus();
    private final Map<Class<?>, Shard> components = new ConcurrentHashMap<>();

    /**
     * A cache to lookup implementations based on a
     * {@link Shard} interface or super class.
     */
    private final LoadingCache<Class<?>, List<Shard>> componentCache = Caffeine.newBuilder().build(key -> {
        List<Shard> list = null;
        for (Shard component : this.components.values()) {
            if (key.isInstance(component)) {
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(component);
            }
        }
        return list == null ? Collections.emptyList() : ImmutableList.copyOf(list);
    });

    /**
     * Constructs a new {@link ComponentContainer}
     * for the given {@link ShardHolder}.
     *
     * @param holder The component holder
     */
    public ComponentContainer(ShardHolder holder) {
        checkNotNull(holder, "holder");
        this.holder = holder;
    }

    /**
     * Gets the {@link ShardeventBus}.
     *
     * @return The shardevent bus
     */
    public ShardeventBus getShardeventBus() {
        return this.shardeventBus;
    }

    /**
     * Gets the {@link ShardHolder}.
     *
     * @return The component holder
     */
    public ShardHolder getHolder() {
        return holder;
    }

    private static class ComponentEntry<T extends Shard> {

        private final ComponentType<T> componentType;
        private final Supplier<T> supplier;

        private ComponentEntry(ComponentType<T> componentType, Supplier<T> supplier) {
            this.componentType = componentType;
            this.supplier = supplier;
        }
    }


    /**
     * Attempts to attach a {@link Shard} of the given type to the {@link ShardHolder},
     * if there is already a component of the given type, then that instance will be returned.
     * <p>
     * This method expects that when the {@link Shard} is either abstract or an interface,
     * a default implementation is provided through the {@link InjectionRegistry}.
     *
     * @param type The component type to attach
     * @return The component instance
     *//*
    public <T extends Shard> Optional<T> addComponent(Class<T> type) {
        return addComponent(ComponentType.get(type), LanternInjectionRegistry.get().getSupplier(type));
    }

    /**
     * Attempts to attach the given {@link Shard} to this {@link ShardHolder}. The method
     * will return {@code true} if it was successful, adding a component will be successful if there
     * isn't a {@link Shard} with the same type.
     *
     * @param component The component to attach
     * @return Whether the attachment was successful
     * @throws IllegalArgumentException If the given component instance is already attached
     *//*
    public boolean addComponent(Shard component) throws IllegalArgumentException {
        return addComponent(ComponentType.get(component.getClass()), (Supplier) (() -> component)).isPresent();
    }

    private <T extends Shard> Optional<T> addComponent(ComponentType<T> type, Supplier<T> supplier) {
        // Check if the holder can be attached
        if (!isHolderCompatible(type)) {
            return Optional.empty();
        }
        final List<Supplier> suppliers = new ArrayList<>();
        // Initialize all the dependencies of the component
        for (DependencySpec dependencySpec : type.getDependencies()) {
            final ComponentType dependencyComponentType = ComponentType.get(dependencySpec.getType());
            // Check if the dependency is required
            if (dependencySpec.getDependencyType() != Requirement.OPTIONAL) {
                // Check if there is already a component attached of this type
                final Optional<? extends Shard> optComponent = getComponent(dependencySpec.getType());
                // We already have a component of this type, no need to attach a new one
                if (optComponent.isPresent()) {
                    continue;
                }
                // No auto attach, so we need to fail
                if (!dependencySpec.getAutoAttach()) {
                    return Optional.empty();
                }
            } else if (!dependencySpec.getAutoAttach() || // Skip if it's optional and not auto attach
                    !isHolderCompatible(dependencyComponentType)) { // Or optional and incompatible holder requirements
                continue;
            }
            // Construct the dependency component and add it
            final Supplier dependencyComponentSupplier = LanternInjectionRegistry.get().getSupplier(dependencySpec.getType());
            suppliers.add(dependencyComponentSupplier);
        }
        // Construct all the components
        final T component = supplier.get();
        this.components.put(component.getClass(), component);
        for (Supplier componentSupplier : suppliers) {
            final Shard dependencyComponent = (Shard) componentSupplier.get();
            this.components.putIfAbsent(dependencyComponent.getClass(), dependencyComponent);
        }
        // Invalidate the cache
        this.componentCache.invalidateAll();
        return Optional.of(component);
    }

    private boolean isHolderCompatible(ComponentType<?> componentType) {
        for (Class<?> holderRequirement : componentType.getHolderRequirements()) {
            if (!holderRequirement.isInstance(this.holder)) {
                return false;
            }
        }
        return true;
    }

    /*
    private List<Component> addComponents(List<ComponentEntry> componentEntries) {
        // Check if the holder can be attached
        for (Class<?> holderRequirement : type.getHolderRequirements()) {
            if (!holderRequirement.isInstance(this.holder)) {
                return Optional.empty();
            }
        }
        // Initialize all the dependencies of the component
        for (DependencySpec dependencySpec : type.getDependencies()) {

        }
        return Optional.empty();
    }*/

    /**
     * Attempts to replace the {@link Shard} attached to the given type. If there are multiple
     * ones found, only the first possible one will be replaced. If there were none found, the
     * {@link Shard} will just be attached to this holder.
     *
     * @param type The component type to replace
     * @param component The new component instance
     * @return Whether the replacement was successful
     * @throws IllegalArgumentException If the given component instance is already attached
     */
    public <T extends Shard, I extends T> boolean replaceComponent(Class<T> type, I component) throws IllegalArgumentException {
        return false;
    }

    /**
     * Attempts to replace the {@link Shard} attached to the given type. If there are multiple
     * ones found, only the first possible one will be replaced. If there were none found, the
     * {@link Shard} will just be attached to this holder.
     *
     * @param type The component type to replace
     * @param component The new component type
     * @return Whether the replacement was successful
     * @throws IllegalArgumentException If the given component instance is already attached
     */
    public <T extends Shard, I extends T> boolean replaceComponent(Class<T> type, Class<I> component) throws IllegalArgumentException {
        return false;
    }

    /**
     * Gets the {@link Shard} of the given type if present, otherwise {@link Optional#empty()}.
     * <p>
     * Only the first {@link Shard} will be returned if there
     * are multiple ones for the given type.
     *
     * @param type The component type
     * @return The component instance if present
     */
    public <T extends Shard> Optional<T> getComponent(Class<T> type) {
        final List<T> components = (List<T>) this.componentCache.get(type);
        return components.isEmpty() ? Optional.empty() : Optional.of(components.get(0));
    }

    /**
     * Gets a {@link Collection} with all the {@link Shard}s
     * of the given type.
     *
     * @param type The component type
     * @return A collection with the components
     */
    public <T extends Shard> Collection<T> getComponents(Class<T> type) {
        return (List<T>) this.componentCache.get(type);
    }

    /**
     * Attempts to remove all the {@link Shard}s that match the given type, all the components
     * that were removed will be present in the result {@link Collection}.
     *
     * @param type The component type
     * @return A collection with the removed components
     */
    public <T extends Shard> Collection<T> removeComponents(Class<T> type) {
        return Collections.emptySet();
    }
}
