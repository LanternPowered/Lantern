package org.lanternpowered.server.catalog;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.spongepowered.api.CatalogType;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

public class LanternCatalogTypeRegistry<T extends CatalogType> implements CatalogTypeRegistry<T> {

    private final ConcurrentMap<String, T> types = Maps.newConcurrentMap();

    @Override
    public Set<T> getAll() {
        return ImmutableSet.copyOf(this.types.values());
    }

    @Override
    public void register(T catalogType) {
        String id = checkNotNull(catalogType, "catalogType").getId().toLowerCase();
        checkNotNullOrEmpty(id, "identifier");
        checkState(this.types.putIfAbsent(id, catalogType) == null, "identifier already present (" + id + ")");
    }

    @Override
    public Optional<T> get(String identifier) {
        return Optional.fromNullable(this.types.get(checkNotNullOrEmpty(identifier, "identifier")));
    }

    @Override
    public <V extends T> Optional<V> getOf(String identifier, Class<V> type) {
        T catalogType = this.types.get(checkNotNullOrEmpty(identifier, "identifier"));
        if (type.isInstance(catalogType)) {
            return Optional.<V>of(type.cast(catalogType));
        }
        return Optional.absent();
    }

    @Override
    public <V extends T> Set<V> getAllOf(Class<V> type) {
        ImmutableSet.Builder<V> builder = ImmutableSet.builder();
        for (T catalogType : this.types.values()) {
            if (type.isInstance(catalogType)) {
                builder.add(type.cast(catalogType));
            }
        }
        return builder.build();
    }

    @Override
    public Map<String, T> getDelegateMap() {
        return this.types;
    }

    @Override
    public boolean has(T catalogType) {
        return this.types.containsValue(checkNotNull(catalogType, "catalogType"));
    }

}
