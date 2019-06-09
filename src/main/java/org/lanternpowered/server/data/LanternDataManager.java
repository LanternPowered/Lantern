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
package org.lanternpowered.server.data;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.lanternpowered.server.data.persistence.MemoryDataContainer;
import org.lanternpowered.server.game.registry.type.data.DataSerializerRegistry;
import org.slf4j.Logger;
import org.spongepowered.api.data.DataManager;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.DataBuilder;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataContentUpdater;
import org.spongepowered.api.data.persistence.DataSerializable;
import org.spongepowered.api.data.persistence.DataTranslator;
import org.spongepowered.api.data.persistence.DataView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Singleton
public final class LanternDataManager implements DataManager {

    private static final Comparator<DataContentUpdater> dataContentUpdaterComparator = (o1, o2) -> ComparisonChain.start()
                    .compare(o2.getInputVersion(), o1.getInputVersion())
                    .compare(o2.getOutputVersion(), o1.getOutputVersion())
                    .result();

    private final Map<Class<?>, DataBuilder<?>> builders = new HashMap<>();
    private final Map<Class<? extends DataSerializable>, List<DataContentUpdater>> updatersMap = new IdentityHashMap<>();
    private final Map<Class<?>, DataRegistration> registrations = new HashMap<>();
    private final Map<String, DataRegistration> legacyRegistrations = new HashMap<>();

    private final Logger logger;

    private boolean allowRegistrations = true;

    @Inject
    private LanternDataManager(Logger logger) {
        this.logger = logger;
    }

    @Override
    public <T extends DataSerializable> void registerBuilder(Class<T> clazz, DataBuilder<T> builder) {
        checkNotNull(clazz);
        checkNotNull(builder);
        checkState(this.allowRegistrations);
        if (!this.builders.containsKey(clazz)) {
            if (!(builder instanceof AbstractDataBuilder)) {
                this.logger.warn("A custom DataBuilder is not extending AbstractDataBuilder! It is recommended that "
                        + "the custom data builder does extend it to gain automated content versioning updates and maintain "
                        + "simplicity. The offending builder's class is: {}", builder.getClass());
            }
            this.builders.put(clazz, builder);
        } else {
            this.logger.warn("A DataBuilder has already been registered for {}. Attempted to register {} instead.", clazz,
                    builder.getClass());
        }
    }

    @Override
    public <T extends DataSerializable> void registerContentUpdater(Class<T> clazz, DataContentUpdater dataContentUpdater) {
        checkNotNull(dataContentUpdater, "dataContentUpdater");
        checkNotNull(clazz, "clazz");
        final List<DataContentUpdater> updaters = this.updatersMap.computeIfAbsent(clazz, key -> new ArrayList<>());
        updaters.add(dataContentUpdater);
        updaters.sort(dataContentUpdaterComparator);
    }

    @Override
    public <T extends DataSerializable> Optional<DataContentUpdater> getWrappedContentUpdater(Class<T> clazz, int fromVersion, int toVersion) {
        checkArgument(fromVersion != toVersion, "Attempting to convert to the same version!");
        checkArgument(fromVersion < toVersion, "Attempting to backwards convert data! This isn't supported!");
        final List<DataContentUpdater> updaters = this.updatersMap.get(checkNotNull(clazz, "DataSerializable class was null!"));
        if (updaters == null) {
            return Optional.empty();
        }
        final ImmutableList.Builder<DataContentUpdater> builder = ImmutableList.builder();
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
            final Exception e = new IllegalStateException("The requested content version for: " + clazz.getSimpleName() + " was requested, "
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
        } else {
            return Optional.empty();
        }
    }

    @Override
    public <T extends DataSerializable> Optional<T> deserialize(Class<T> clazz, DataView dataView) {
        checkNotNull(dataView, "dataView");
        final Optional<DataBuilder<T>> optional = getBuilder(clazz);
        return optional.flatMap(builder -> builder.build(dataView));
    }

    Optional<DataRegistration> getLegacyRegistration(String legacyId) {
        checkNotNull(legacyId, "legacyId");
        return Optional.ofNullable(this.legacyRegistrations.get(legacyId));
    }

    public Optional<DataRegistration> get(Class<?> type) {
        checkNotNull(type, "type");
        return Optional.ofNullable(this.registrations.get(type));
    }

    @Override
    public <T> Optional<DataTranslator<T>> getTranslator(Class<T> objectClass) {
        return DataSerializerRegistry.INSTANCE.getTranslator(objectClass);
    }

    @Override
    public DataContainer createContainer() {
        return new MemoryDataContainer();
    }

    @Override
    public DataContainer createContainer(DataView.SafetyMode safety) {
        return new MemoryDataContainer(safety);
    }
}
