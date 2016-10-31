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
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.util.annotation.NonnullByDefault;

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
        for (DataManipulator<?, ?> manipulator : manipulators) {
            builder.add(new MemoryDataContainer()
                    .set(DataQueries.DATA_CLASS, manipulator.getClass().getName())
                    .set(DataQueries.INTERNAL_DATA, manipulator.toContainer()));
        }
        return builder.build();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static ImmutableList<DataManipulator<?, ?>> deserializeManipulatorList(List<DataView> containers) {
        checkNotNull(containers);
        final ImmutableList.Builder<DataManipulator<?, ?>> builder = ImmutableList.builder();
        for (DataView view : containers) {
            final String clazzName = view.getString(DataQueries.DATA_CLASS).get();
            final DataView manipulatorView = view.getView(DataQueries.INTERNAL_DATA).get();
            try {
                final Class<?> clazz = Class.forName(clazzName);
                final Optional<DataManipulatorBuilder<?, ?>> optional = LanternDataManager.get().getBuilder((Class) clazz);
                if (optional.isPresent()) {
                    final Optional<? extends DataManipulator<?, ?>> manipulatorOptional = optional.get().build(manipulatorView);
                    if (manipulatorOptional.isPresent()) {
                        builder.add(manipulatorOptional.get());
                    }
                }
            } catch (Exception e) {
                new InvalidDataException("Could not deserialize " + clazzName
                        + "! Don't worry though, we'll try to deserialize the rest of the data.", e).printStackTrace();
            }
        }
        return builder.build();
    }

    private DataUtil() {
    }

}
