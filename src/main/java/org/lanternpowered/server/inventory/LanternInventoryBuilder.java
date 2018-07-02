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
package org.lanternpowered.server.inventory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.data.property.PropertyKeySetter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public class LanternInventoryBuilder<T extends AbstractInventory> implements Inventory.Builder {

    public static LanternInventoryBuilder<AbstractInventory> create() {
        return new LanternInventoryBuilder<>();
    }

    private static class InventoryListeners implements EventListener<InteractInventoryEvent> {

        private final AbstractInventory inventory;
        private final Class<? extends InteractInventoryEvent> eventType;
        private final List<Consumer<InteractInventoryEvent>> consumers;

        @SuppressWarnings({"unchecked", "rawtypes"})
        InventoryListeners(AbstractInventory inventory,
                Class<? extends InteractInventoryEvent> eventType,
                List<Consumer<? extends InteractInventoryEvent>> consumers) {
            this.eventType = eventType;
            this.consumers = (List) ImmutableList.copyOf(consumers);
            this.inventory = inventory;
        }

        @Override
        public void handle(InteractInventoryEvent event) throws Exception {
            final LanternContainer container = (LanternContainer) event.getTargetInventory();
            if (container.containsInventory(this.inventory)) {
                for (Consumer<InteractInventoryEvent> consumer : this.consumers) {
                    consumer.accept(event);
                }
            }
        }
    }

    private static final Cache<AbstractInventory, List<InventoryListeners>> inventoryListeners =
            Caffeine.<AbstractInventory, List<InventoryListeners>>newBuilder()
                    .weakKeys()
                    .removalListener((RemovalListener<AbstractInventory, List<InventoryListeners>>) (key, value, cause) -> {
                        if (value != null) {
                            for (InventoryListeners listeners : value) {
                                Sponge.getEventManager().unregisterListeners(listeners);
                            }
                        }
                    })
                    .build();

    @Nullable private LanternInventoryArchetype<T> inventoryArchetype;
    @Nullable private AbstractBuilder builder;
    @Nullable private Carrier carrier;
    @Nullable private Class<? extends Carrier> forCarrierType;
    @Nullable private Carrier forCarrier;
    private final Map<Class<? extends InteractInventoryEvent>, List<Consumer<? extends InteractInventoryEvent>>> listeners = new HashMap<>();

    private LanternInventoryBuilder() {
    }

    @Override
    public LanternInventoryBuilder<T> from(Inventory value) {
        checkNotNull(value, "value");
        this.inventoryArchetype = (LanternInventoryArchetype<T>) value.getArchetype();
        this.listeners.clear();
        final List<InventoryListeners> listeners = inventoryListeners.getIfPresent(value);
        if (listeners != null) {
            listeners.forEach(l -> this.listeners.computeIfAbsent(l.eventType, type -> new ArrayList<>()).addAll(l.consumers));
        }
        this.builder = null; // Regenerate the builder if needed
        return this;
    }

    @Override
    public LanternInventoryBuilder<T> reset() {
        this.inventoryArchetype = null;
        this.listeners.clear();
        this.builder = null;
        this.carrier = null;
        return this;
    }

    @Override
    public LanternInventoryBuilder<T> of(InventoryArchetype archetype) {
        checkNotNull(archetype, "archetype");
        this.inventoryArchetype = (LanternInventoryArchetype<T>) archetype;
        this.builder = null; // Regenerate the builder if needed
        return this;
    }

    public <N extends AbstractInventory> LanternInventoryBuilder<N> of(LanternInventoryArchetype<N> archetype) {
        checkNotNull(archetype, "archetype");
        this.inventoryArchetype = (LanternInventoryArchetype<T>) archetype;
        archetype.getBuilder();
        this.builder = null; // Regenerate the builder if needed
        return (LanternInventoryBuilder<N>) this;
    }

    @Override
    public Inventory.Builder property(InventoryProperty<?, ?> property) {
        return property(property.getKey().toString(), property);
    }

    @Override
    public LanternInventoryBuilder property(String name, InventoryProperty property) {
        checkNotNull(property, "property");
        checkNotNull(name, "name");
        if (this.inventoryArchetype == null) {
            return this;
        }
        final InventoryProperty<String, ?> property1 = (InventoryProperty<String, ?>) property;
        PropertyKeySetter.setKey(property1, name); // Modify the name of the property
        if (this.builder == null) {
            this.builder = this.inventoryArchetype.getBuilder().copy();
        }
        this.builder.property(property1);
        return this;
    }

    @Override
    public LanternInventoryBuilder<T> withCarrier(Carrier carrier) {
        checkNotNull(carrier, "carrier");
        this.carrier = carrier;
        return this;
    }

    @Override
    public T build(Object plugin) {
        checkState(this.inventoryArchetype != null, "The inventory archetype must be set");
        AbstractBuilder builder = this.builder;
        LanternInventoryArchetype<T> inventoryArchetype = this.inventoryArchetype;
        if (builder == null) {
            builder = this.inventoryArchetype.getBuilder();
        }
        if (this.forCarrierType != null || this.forCarrier != null || this.carrier != null) {
            AbstractArchetypeBuilder builder1 = (AbstractArchetypeBuilder) builder;
            if (builder1.carrierTransformer != null) {
                final Supplier supplier = () -> {
                    final AbstractArchetypeBuilder copy = builder1.copy();
                    copy.carrierTransformer = null;
                    return copy;
                };
                final LanternInventoryArchetype archetype;
                if (this.forCarrier != null) {
                    archetype = builder1.carrierTransformer.apply(this.forCarrier, supplier);
                } else if (this.forCarrierType != null) {
                    archetype = builder1.carrierTransformer.apply(this.forCarrierType, supplier);
                } else {
                    // Fall back to actual carrier
                    archetype = builder1.carrierTransformer.apply(this.carrier, supplier);
                }
                if (archetype != null) {
                    builder = archetype.getBuilder();
                }
            }
        }
        final AbstractInventory inventory = builder.build(this.carrier != null, plugin, inventoryArchetype);
        if (inventory instanceof AbstractMutableInventory && this.carrier != null) {
            final AbstractMutableInventory mutableInventory = (AbstractMutableInventory) inventory;
            mutableInventory.setCarrier0(this.carrier);
        }
        if (this.carrier instanceof AbstractCarrier) {
            ((AbstractCarrier) this.carrier).setInventory((CarriedInventory<?>) inventory);
        }
        if (!this.listeners.isEmpty()) {
            final List<InventoryListeners> listenersList = new ArrayList<>();
            for (Map.Entry<Class<? extends InteractInventoryEvent>, List<Consumer<? extends InteractInventoryEvent>>> entry : this.listeners.entrySet()) {
                final InventoryListeners listeners = new InventoryListeners(inventory, entry.getKey(), entry.getValue());
                Sponge.getEventManager().registerListener(plugin, entry.getKey(), listeners);
            }
            inventoryListeners.put(inventory, listenersList);
        }
        return (T) inventory;
    }

    @Override
    public <E extends InteractInventoryEvent> LanternInventoryBuilder<T> listener(Class<E> type, Consumer<E> listener) {
        checkNotNull(type, "type");
        checkNotNull(listener, "listener");
        this.listeners.computeIfAbsent(type, type1 -> new ArrayList<>()).add(listener);
        return this;
    }

    @Override
    public LanternInventoryBuilder<T> forCarrier(Carrier carrier) {
        checkNotNull(carrier, "carrier");
        this.forCarrier = carrier;
        this.forCarrierType = null;
        return this;
    }

    @Override
    public LanternInventoryBuilder<T> forCarrier(Class<? extends Carrier> carrier) {
        checkNotNull(carrier, "carrier");
        this.forCarrierType = carrier;
        this.forCarrier = null;
        return this;
    }
}
