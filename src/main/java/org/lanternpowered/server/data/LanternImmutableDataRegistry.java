package org.lanternpowered.server.data;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.data.ImmutableDataBuilder;
import org.spongepowered.api.data.ImmutableDataHolder;
import org.spongepowered.api.data.ImmutableDataRegistry;

import com.google.common.collect.MapMaker;

public final class LanternImmutableDataRegistry implements ImmutableDataRegistry {

    private final static LanternImmutableDataRegistry instance = new LanternImmutableDataRegistry();

    public static LanternImmutableDataRegistry getInstance() {
        return instance;
    }

    private final Map<Class<? extends ImmutableDataHolder<?>>, ImmutableDataBuilder<?, ?>> builderMap =
            new MapMaker().concurrencyLevel(4).makeMap();

    private LanternImmutableDataRegistry() {
    }

    @Override
    public <T extends ImmutableDataHolder<T>, B extends ImmutableDataBuilder<T, B>> void register(Class<T> manipulatorClass, B builder) {
        if (!this.builderMap.containsKey(checkNotNull(manipulatorClass))) {
            this.builderMap.put(manipulatorClass, checkNotNull(builder));
        } else {
            throw new IllegalStateException("Already registered the DataBuilder for " + manipulatorClass.getCanonicalName());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ImmutableDataHolder<T>, B extends ImmutableDataBuilder<T, B>> Optional<B> getBuilder(Class<T> manipulatorClass) {
        return Optional.ofNullable((B) this.builderMap.get(checkNotNull(manipulatorClass)));
    }
}
