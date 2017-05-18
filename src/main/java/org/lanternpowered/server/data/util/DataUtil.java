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
package org.lanternpowered.server.data.util;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.data.LanternDataManager;
import org.lanternpowered.server.data.manipulator.mutable.IDataManipulator;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.type.data.DataManipulatorRegistryModule;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.List;
import java.util.Optional;

public final class DataUtil {

    public static DataView checkDataExists(final DataView dataView, final DataQuery query) throws InvalidDataException {
        if (!checkNotNull(dataView).contains(checkNotNull(query))) {
            throw new InvalidDataException("Missing data for query: " + query.asString('.'));
        } else {
            return dataView;
        }
    }

    public static DataView getOrCreateView(final DataView dataView, final DataQuery query) {
        return dataView.getView(query).orElseGet(() -> dataView.createView(query));
    }

    public static List<DataView> getSerializedManipulatorList(Iterable<DataManipulator<?, ?>> manipulators) {
        checkNotNull(manipulators);
        final ImmutableList.Builder<DataView> builder = ImmutableList.builder();
        final LanternDataManager dataManager = Lantern.getGame().getDataManager();
        for (DataManipulator<?, ?> manipulator : manipulators) {
            Class<?> manipulatorType;
            if (manipulator instanceof IDataManipulator) {
                manipulatorType = ((IDataManipulator) manipulator).getMutableType();
            } else {
                manipulatorType = manipulator.getClass();
            }
            final Optional<DataRegistration> optRegistration = dataManager.get(manipulatorType);
            if (!optRegistration.isPresent()) {
                Lantern.getLogger().error("Could not serialize {}. No registration could be found.", manipulatorType.getName());
            } else {
                builder.add(DataContainer.createNew()
                        .set(DataQueries.DATA_CLASS, optRegistration.get().getId())
                        .set(DataQueries.INTERNAL_DATA, manipulator.toContainer()));
            }
        }
        // TODO: Save failed deserialized containers
        return builder.build();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static ImmutableList<DataManipulator<?, ?>> deserializeManipulatorList(List<DataView> containers) {
        checkNotNull(containers);
        final ImmutableList.Builder<DataManipulator<?, ?>> builder = ImmutableList.builder();
        final LanternDataManager dataManager = Lantern.getGame().getDataManager();
        // TODO: Save failed deserialized containers
        for (DataView view : containers) {
            final String id = view.getString(DataQueries.DATA_CLASS).get();
            final DataView manipulatorView = view.getView(DataQueries.INTERNAL_DATA).get();
            Optional<DataRegistration> optRegistration = DataManipulatorRegistryModule.get().getById(id);
            if (!optRegistration.isPresent()) {
                optRegistration = dataManager.getLegacyRegistration(id);
            }
            if (optRegistration.isPresent()) {
                try {
                    final Optional<DataManipulator> optManipulator = optRegistration.get().getDataManipulatorBuilder().build(manipulatorView);
                    if (optManipulator.isPresent()) {
                        builder.add(optManipulator.get());
                    }
                } catch (InvalidDataException e) {
                    Lantern.getLogger().error("Could not deserialize " + id
                            + "! Don't worry though, we'll try to deserialize the rest of the data.", e);
                }
            } else {
                Lantern.getLogger().warn("Missing DataRegistration for ID: " + id + ". Don't worry, the data will be kept safe.");
            }
        }
        return builder.build();
    }

    private DataUtil() {
    }

}
