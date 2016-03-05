/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
package org.lanternpowered.server.data;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.MapMaker;
import org.lanternpowered.server.data.persistence.SimpleDataTypeSerializerCollection;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.data.DataManager;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.ImmutableDataBuilder;
import org.spongepowered.api.data.ImmutableDataHolder;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.DataBuilder;
import org.spongepowered.api.data.persistence.DataContentUpdater;
import org.spongepowered.api.data.persistence.DataSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class LanternDataManager extends SimpleDataTypeSerializerCollection implements DataManager {

    private static final Comparator<DataContentUpdater> dataContentUpdaterComparator = (o1, o2) -> ComparisonChain.start()
                    .compare(o2.getInputVersion(), o1.getInputVersion())
                    .compare(o2.getOutputVersion(), o1.getOutputVersion())
                    .result();

    private static final LanternDataManager instance = new LanternDataManager();

    public static LanternDataManager getInstance() {
        return instance;
    }

    private final Map<Class<?>, DataBuilder<?>> builders = new HashMap<>();
    private final Map<Class<? extends DataManipulator<?, ?>>, DataManipulatorBuilder<?, ?>> builderMap =
            new MapMaker().concurrencyLevel(4).makeMap();
    private final Map<Class<? extends ImmutableDataHolder<?>>, ImmutableDataBuilder<?, ?>> immutableDataBuilderMap =
            new MapMaker().concurrencyLevel(4).makeMap();
    private final Map<Class<? extends ImmutableDataManipulator<?, ?>>, DataManipulatorBuilder<?, ?>> immutableBuilderMap =
            new MapMaker().concurrencyLevel(4).makeMap();
    private final Map<Class<? extends DataSerializable>, List<DataContentUpdater>> updatersMap = new IdentityHashMap<>();

    private boolean allowRegistrations = true;

    public LanternDataManager() {
    }

    @Override
    public <T extends ImmutableDataHolder<T>, B extends ImmutableDataBuilder<T, B>> void register(Class<T> manipulatorClass, B builder) {
        if (!this.immutableDataBuilderMap.containsKey(checkNotNull(manipulatorClass))) {
            this.immutableDataBuilderMap.put(manipulatorClass, checkNotNull(builder));
        } else {
            throw new IllegalStateException("Already registered the DataUtil for " + manipulatorClass.getCanonicalName());
        }
    }

    @Override
    public <T extends DataSerializable> void registerBuilder(Class<T> clazz, DataBuilder<T> builder) {
        checkNotNull(clazz);
        checkNotNull(builder);
        checkState(this.allowRegistrations);
        if (!this.builders.containsKey(clazz)) {
            if (!(builder instanceof AbstractDataBuilder)) {
                Lantern.getLogger().warn("A custom DataBuilder is not extending AbstractDataBuilder! It is recommended that "
                        + "the custom data builder does extend it to gain automated content versioning updates and maintain "
                        + "simplicity. The offending builder's class is: {}", builder.getClass());
            }
            this.builders.put(clazz, builder);
        } else {
            Lantern.getLogger().warn("A DataBuilder has already been registered for {}. Attempted to register {} instead.", clazz,
                    builder.getClass());
        }
    }

    @Override
    public <T extends DataSerializable> void registerContentUpdater(Class<T> clazz, DataContentUpdater updater) {
        checkNotNull(updater, "DataContentUpdater was null!");
        final List<DataContentUpdater> updaters = this.updatersMap.computeIfAbsent(checkNotNull(clazz, "DataSerializable class was null!"),
                key -> new ArrayList<>());
        updaters.add(updater);
        Collections.sort(updaters, dataContentUpdaterComparator);
    }

    @Override
    public <T extends DataSerializable> Optional<DataContentUpdater> getWrappedContentUpdater(Class<T> clazz, int fromVersion, int toVersion) {
        checkArgument(fromVersion != toVersion, "Attempting to convert to the same version!");
        checkArgument(fromVersion < toVersion, "Attempting to backwards convert data! This isn't supported!");
        final List<DataContentUpdater> updaters = this.updatersMap.get(checkNotNull(clazz, "DataSerializable class was null!"));
        if (updaters == null) {
            return Optional.empty();
        }
        ImmutableList.Builder<DataContentUpdater> builder = ImmutableList.builder();
        int version = fromVersion;
        for (DataContentUpdater updater : updaters) {
            if (updater.getInputVersion() == version) {
                if (updater.getOutputVersion() > toVersion) {
                    continue;
                }
                version = updater.getOutputVersion();
                builder.add(updater);
            }
        }
        if (version < toVersion || version > toVersion) { // There wasn't a registered updater for the version being requested
            Exception e = new IllegalStateException("The requested content version for: " + clazz.getSimpleName() + " was requested, "
                    + "\nhowever, the versions supplied: from " + fromVersion + " to " + toVersion + " is impossible"
                    + "\nas the latest version registered is: " + version + ". Please notify the developer of"
                    + "\nthe requested consumed DataSerializable of this error.");
            e.printStackTrace();
            return Optional.empty();
        }
        return Optional.of(new DataUpdaterDelegate(builder.build(), fromVersion, toVersion));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends DataSerializable> Optional<DataBuilder<T>> getBuilder(Class<T> objectClass) {
        checkNotNull(objectClass, "objectClass");
        if (this.builders.containsKey(objectClass)) {
            return Optional.of((DataBuilder<T>) this.builders.get(objectClass));
        } else if (this.builderMap.containsKey(objectClass)) {
            return Optional.of((DataBuilder<T>) this.builderMap.get(objectClass));
        } else if (this.immutableDataBuilderMap.containsKey(objectClass)) {
            return Optional.of((DataBuilder<T>) this.immutableDataBuilderMap.get(objectClass));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public <T extends DataSerializable> Optional<T> deserialize(Class<T> clazz, DataView dataView) {
        final Optional<DataBuilder<T>> optional = this.getBuilder(clazz);
        if (optional.isPresent()) {
            return optional.get().build(dataView);
        } else {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ImmutableDataHolder<T>, B extends ImmutableDataBuilder<T, B>> Optional<B> getImmutableBuilder(Class<T> holderClass) {
        return Optional.ofNullable((B) this.immutableDataBuilderMap.get(checkNotNull(holderClass)));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends DataManipulator<T, I>, I extends ImmutableDataManipulator<I, T>> void register(Class<? extends T> manipulatorClass,
            Class<? extends I> immutableManipulatorClass, DataManipulatorBuilder<T, I> builder) {
        checkState(allowRegistrations, "Registrations are no longer allowed!");
        if (!this.builderMap.containsKey(checkNotNull(manipulatorClass))) {
            this.builderMap.put(manipulatorClass, checkNotNull(builder));
            this.immutableBuilderMap.put(checkNotNull(immutableManipulatorClass), builder);
            this.registerBuilder((Class<T>) manipulatorClass, builder);
        } else {
            throw new IllegalStateException("Already registered the DataUtil for " + manipulatorClass.getCanonicalName());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends DataManipulator<T, I>, I extends ImmutableDataManipulator<I, T>> Optional<DataManipulatorBuilder<T, I>> getManipulatorBuilder(
            Class<T> manipulatorClass) {
        return Optional.ofNullable((DataManipulatorBuilder<T, I>) this.builderMap.get(checkNotNull(manipulatorClass)));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends DataManipulator<T, I>, I extends ImmutableDataManipulator<I, T>> Optional<DataManipulatorBuilder<T, I>>
            getImmutableManipulatorBuilder(Class<I> immutableManipulatorClass) {
        return Optional.ofNullable((DataManipulatorBuilder<T, I>) this.immutableBuilderMap.get(checkNotNull(immutableManipulatorClass)));
    }

    @Override
    public <T> void registerSerializer(Class<T> objectClass, DataSerializer<T> serializer) {
        checkState(this.allowRegistrations, "Registrations are no longer allowed");
        checkNotNull(objectClass, "objectClass");
        checkNotNull(serializer, "serializer");
        checkArgument(serializer.getToken().isAssignableFrom(objectClass),
                "DataSerializer is not compatible with the target object class: " +objectClass);
        this.registerSerializer(serializer);
    }

    @Override
    public <T> Optional<DataSerializer<T>> getSerializer(Class<T> objectClass) {
        return super.getSerializer(objectClass);
    }
}
