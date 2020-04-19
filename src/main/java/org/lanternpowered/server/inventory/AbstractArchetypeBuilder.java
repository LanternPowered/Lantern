/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.inventory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.data.Archetype;
import org.spongepowered.api.data.property.Property;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.InventoryArchetype;

import java.util.List;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("unchecked")
public abstract class AbstractArchetypeBuilder<R extends T, T extends AbstractInventory, B extends AbstractArchetypeBuilder<R, T, B>>
        extends AbstractBuilder<R, T, B> {

    @Nullable private LanternInventoryArchetype<R> cachedArchetype;
    @Nullable protected CarrierBasedTransformer<R, B> carrierTransformer;
    int slots = 0;

    void invalidateCachedArchetype() {
        this.cachedArchetype = null;
    }

    @Override
    public <N extends T> AbstractArchetypeBuilder<N, T, ?> type(Class<N> inventoryType) {
        super.type(inventoryType);
        invalidateCachedArchetype();
        return (AbstractArchetypeBuilder<N, T, ?>) this;
    }

    @Override
    public <V> B property(Property<V> property, V value) {
        super.property(property, value);
        invalidateCachedArchetype();
        return (B) this;
    }

    /**
     * Applies a transformer to this builder that can be used to
     * transform this {@link Archetype} for the target {@link Carrier}.
     * The second argument of the consumer will be a copy of this
     * {@link AbstractInventorySlot.Builder} but this applied transformer
     * will not be applied. Only one transformer may be applied
     * at the same time.
     *
     * @param transformer The transformer
     * @return This builder, for chaining
     */
    public B carrierBased(CarrierBasedTransformer<R, B> transformer) {
        checkNotNull(transformer, "transformer");
        this.carrierTransformer = transformer;
        return (B) this;
    }

    /**
     * Constructs a {@link LanternInventoryArchetype} from this builder.
     *
     * @return The inventory archetype
     */
    public LanternInventoryArchetype<R> buildArchetype() {
        final String pluginId = (this.pluginContainer == null ? Lantern.getImplementationPlugin() : this.pluginContainer).getId();
        return buildArchetype(CatalogKey.of(pluginId, UUID.randomUUID().toString()));
    }

    /**
     * Constructs a {@link LanternInventoryArchetype} from this builder.
     *
     * @param key The key
     * @return The inventory archetype
     */
    public LanternInventoryArchetype<R> buildArchetype(CatalogKey key) {
        checkState(this.constructor != null);
        if (this.cachedArchetype != null && this.cachedArchetype.getKey().equals(key)) {
            return this.cachedArchetype;
        }
        return this.cachedArchetype = new BuilderInventoryArchetype<>(key, copy());
    }

    protected void copyTo(B builder) {
        builder.constructor = this.constructor;
        builder.pluginContainer = this.pluginContainer;
        builder.properties.clear();
        builder.properties.putAll(this.properties);
        builder.cachedProperties = this.cachedProperties;
        builder.shiftClickBehavior = this.shiftClickBehavior;
        builder.translation = this.translation;
        builder.slots = this.slots;
        builder.carrierTransformer = this.carrierTransformer;
    }

    /**
     * Constructs a copy of this builder.
     *
     * @return The copy
     */
    protected final B copy() {
        final B copy = newBuilder();
        copyTo(copy);
        return copy;
    }

    protected abstract B newBuilder();

    /**
     * Gets a {@link List} with all the children {@link InventoryArchetype}s.
     *
     * @return The inventory archetypes
     */
    protected abstract List<InventoryArchetype> getArchetypes();
}
