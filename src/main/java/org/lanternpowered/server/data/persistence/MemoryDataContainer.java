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
package org.lanternpowered.server.data.persistence;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;

import java.util.Optional;

/**
 * The default implementation of {@link DataContainer} that can be instantiated
 * for any use. This is the primary implementation of any {@link DataView} that
 * is used throughout both SpongeAPI and Sponge implementation.
 */
public final class MemoryDataContainer extends MemoryDataView implements DataContainer {

    /**
     * Creates a new {@link MemoryDataContainer} with a default
     * {@link org.spongepowered.api.data.persistence.DataView.SafetyMode} of
     * {@link org.spongepowered.api.data.persistence.DataView.SafetyMode#ALL_DATA_CLONED}.
     *
     */
    public MemoryDataContainer() {
        this(DataView.SafetyMode.ALL_DATA_CLONED);
    }

    /**
     * Creates a new {@link MemoryDataContainer} with the provided
     * {@link org.spongepowered.api.data.persistence.DataView.SafetyMode}.
     *
     * @param safety The safety mode to use
     * @see org.spongepowered.api.data.persistence.DataView.SafetyMode
     */
    public MemoryDataContainer(DataView.SafetyMode safety) {
        super(safety);
    }

    @Override
    public Optional<DataView> getParent() {
        return Optional.empty();
    }

    @Override
    public final DataContainer getContainer() {
        return this;
    }

    @Override
    public DataContainer set(DataQuery path, Object value) {
        return (DataContainer) super.set(path, value);
    }

    @Override
    public DataContainer remove(DataQuery path) {
        return (DataContainer) super.remove(path);
    }
}
