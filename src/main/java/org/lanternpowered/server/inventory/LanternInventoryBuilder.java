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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.lanternpowered.server.data.property.PropertyKeySetter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

import java.lang.ref.WeakReference;
import java.util.Collection;
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

        private final WeakReference<AbstractInventory> inventory;
        private final List<Consumer<? super Event>> consumers;

        @SuppressWarnings({"unchecked", "rawtypes"})
        InventoryListeners(AbstractInventory inventory, Collection<Consumer<? super Event>> consumers) {
            this.consumers = ImmutableList.copyOf(consumers);
            this.inventory = new WeakReference<>(inventory);
        }

        @Override
        public void handle(InteractInventoryEvent event) {
            final AbstractContainer container = (AbstractContainer) event.getTargetInventory();
            final AbstractInventory inventory = this.inventory.get();
            if (inventory != null) {
                if (container.containsInventory(inventory)) {
                    for (Consumer<? super Event> consumer : this.consumers) {
                        consumer.accept(event);
                    }
                }
            } else {
                // Remove the listeners if the inventory is no longer in use
                Sponge.getEventManager().unregisterListeners(this);
            }
        }
    }

    @Nullable private LanternInventoryArchetype<T> inventoryArchetype;
    @Nullable private AbstractBuilder builder;
    @Nullable private Carrier carrier;
    @Nullable private Class<? extends Carrier> forCarrierType;
    @Nullable private Carrier forCarrier;

    private final Multimap<Class<? extends Event>, Consumer<? super Event>> listeners = LinkedHashMultimap.create();

    private LanternInventoryBuilder() {
    }

    @Override
    public LanternInventoryBuilder<T> from(Inventory value) {
        checkNotNull(value, "value");
        final AbstractInventory inventory = (AbstractInventory) value;
        this.inventoryArchetype = (LanternInventoryArchetype<T>) value.getArchetype();
        this.listeners.clear();
        final Multimap<Class<? extends Event>, Consumer<? super Event>> listeners = inventory.getEventListeners();
        if (listeners != null) {
            this.listeners.putAll(listeners);
        }
        this.builder = null; // Regenerate the builder if needed
        this.carrier = null;
        this.forCarrier = null;
        this.forCarrierType = null;
        final CarrierReference<Carrier> carrierReference = ((AbstractInventory) value).getCarrierReference();
        if (carrierReference != null) {
            carrierReference.get().ifPresent(carrier -> this.carrier = carrier);
        }
        return this;
    }

    @Override
    public LanternInventoryBuilder<T> reset() {
        this.inventoryArchetype = null;
        this.listeners.clear();
        this.builder = null;
        this.carrier = null;
        this.forCarrierType = null;
        this.forCarrier = null;
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
            mutableInventory.setCarrier(this.carrier, false);
        }
        if (this.carrier instanceof AbstractCarrier) {
            ((AbstractCarrier) this.carrier).setInventory((CarriedInventory<?>) inventory);
        }
        if (!this.listeners.isEmpty()) {
            inventory.setEventListeners(ImmutableMultimap.copyOf(this.listeners));
            for (Map.Entry<Class<? extends Event>, Collection<Consumer<? super Event>>> entry : this.listeners.asMap().entrySet()) {
                final InventoryListeners listeners = new InventoryListeners(inventory, entry.getValue());
                Sponge.getEventManager().registerListener(plugin, (Class<? extends InteractInventoryEvent>) entry.getKey(), listeners);
            }
        }
        return (T) inventory;
    }

    @Override
    public <E extends InteractInventoryEvent> LanternInventoryBuilder<T> listener(Class<E> type, Consumer<E> listener) {
        checkNotNull(type, "type");
        checkNotNull(listener, "listener");
        this.listeners.put(type, (Consumer) listener);
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
