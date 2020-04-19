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
package org.lanternpowered.server.data;

import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongList;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;

import java.util.Optional;

@SuppressWarnings("unchecked")
public final class DataViewHelper {

    /**
     * Attempts to get a byte array from the {@link DataView}
     * for the specific {@link DataQuery} path.
     *
     * @param dataView The data view
     * @param path The path
     * @return The byte array
     */
    public static Optional<byte[]> getByteArray(DataView dataView, DataQuery path) {
        final Optional<Object> optObject = dataView.get(path);
        if (optObject.isPresent() && optObject.get() instanceof byte[]) {
            return (Optional) optObject;
        }
        return dataView.getByteList(path).map(list -> {
            if (list instanceof ByteList) {
                return ((ByteList) list).toByteArray();
            }
            final byte[] array = new byte[list.size()];
            for (int i = 0; i < list.size(); i++) {
                array[i] = list.get(i);
            }
            return array;
        });
    }

    /**
     * Attempts to get a int array from the {@link DataView}
     * for the specific {@link DataQuery} path.
     *
     * @param dataView The data view
     * @param path The path
     * @return The int array
     */
    public static Optional<int[]> getIntArray(DataView dataView, DataQuery path) {
        final Optional<Object> optObject = dataView.get(path);
        if (optObject.isPresent() && optObject.get() instanceof int[]) {
            return (Optional) optObject;
        }
        return dataView.getIntegerList(path).map(list -> {
            if (list instanceof IntList) {
                return ((IntList) list).toIntArray();
            }
            final int[] array = new int[list.size()];
            for (int i = 0; i < list.size(); i++) {
                array[i] = list.get(i);
            }
            return array;
        });
    }

    /**
     * Attempts to get a long array from the {@link DataView}
     * for the specific {@link DataQuery} path.
     *
     * @param dataView The data view
     * @param path The path
     * @return The long array
     */
    public static Optional<long[]> getLongArray(DataView dataView, DataQuery path) {
        final Optional<Object> optObject = dataView.get(path);
        if (optObject.isPresent() && optObject.get() instanceof long[]) {
            return (Optional) optObject;
        }
        return dataView.getLongList(path).map(list -> {
            if (list instanceof LongList) {
                return ((LongList) list).toLongArray();
            }
            final long[] array = new long[list.size()];
            for (int i = 0; i < list.size(); i++) {
                array[i] = list.get(i);
            }
            return array;
        });
    }

    /**
     * Attempts to get a long array from the {@link DataView}
     * for the specific {@link DataQuery} path.
     *
     * @param dataView The data view
     * @param path The path
     * @return The long array
     */
    public static Optional<double[]> getDoubleArray(DataView dataView, DataQuery path) {
        final Optional<Object> optObject = dataView.get(path);
        if (optObject.isPresent() && optObject.get() instanceof double[]) {
            return (Optional) optObject;
        }
        return dataView.getDoubleList(path).map(list -> {
            if (list instanceof DoubleList) {
                return ((DoubleList) list).toDoubleArray();
            }
            final double[] array = new double[list.size()];
            for (int i = 0; i < list.size(); i++) {
                array[i] = list.get(i);
            }
            return array;
        });
    }

    /**
     * Attempts to get a long array from the {@link DataView}
     * for the specific {@link DataQuery} path.
     *
     * @param dataView The data view
     * @param path The path
     * @return The long array
     */
    public static Optional<float[]> getFloatArray(DataView dataView, DataQuery path) {
        final Optional<Object> optObject = dataView.get(path);
        if (optObject.isPresent() && optObject.get() instanceof float[]) {
            return (Optional) optObject;
        }
        return dataView.getFloatList(path).map(list -> {
            if (list instanceof FloatList) {
                return ((FloatList) list).toFloatArray();
            }
            final float[] array = new float[list.size()];
            for (int i = 0; i < list.size(); i++) {
                array[i] = list.get(i);
            }
            return array;
        });
    }

    private DataViewHelper() {
    }
}
