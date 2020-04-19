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
package org.lanternpowered.server.data.persistence.nbt;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Lists;
import org.lanternpowered.server.data.persistence.MemoryDataContainer;
import org.lanternpowered.server.data.persistence.DataContainerInput;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.data.persistence.InvalidDataFormatException;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A data input stream that deserializes data views from the nbt format.
 */
public class NbtDataContainerInputStream implements Closeable, DataContainerInput {

    private static final boolean[] emptyBooleanArray = new boolean[0];
    private static final byte[] emptyByteArray = new byte[0];
    private static final short[] emptyShortArray = new short[0];
    private static final int[] emptyIntArray = new int[0];
    private static final long[] emptyLongArray = new long[0];
    private static final float[] emptyFloatArray = new float[0];
    private static final double[] emptyDoubleArray = new double[0];
    private static final String[] emptyStringArray = new String[0];
    private static final DataView[] emptyDataViewArray = new DataView[0];
    private static final Map[] emptyMapArray = new Map[0];

    private final DataInputStream dis;
    private final int maximumDepth;

    /**
     * Creates a new nbt data view input stream.
     *
     * @param dataInputStream the data input stream
     */
    public NbtDataContainerInputStream(DataInputStream dataInputStream) {
        this(dataInputStream, Integer.MAX_VALUE);
    }

    /**
     * Creates a new nbt data view input stream.
     * 
     * @param dataInputStream the data input stream
     * @param maximumDepth the maximum depth of the data contains
     */
    public NbtDataContainerInputStream(DataInputStream dataInputStream, int maximumDepth) {
        this.dis = checkNotNull(dataInputStream, "dataInputStream");
        this.maximumDepth = maximumDepth;
    }

    /**
     * Creates a new nbt data view input stream.
     * 
     * @param inputStream the data input stream
     */
    public NbtDataContainerInputStream(InputStream inputStream) {
        this(inputStream, Integer.MAX_VALUE);
    }

    /**
     * Creates a new nbt data view input stream.
     *
     * @param inputStream the data input stream
     * @param maximumDepth the maximum depth of the data contains
     */
    public NbtDataContainerInputStream(InputStream inputStream, int maximumDepth) {
        this(checkNotNull(inputStream, "inputStream") instanceof DataInputStream ?
                (DataInputStream) inputStream : new DataInputStream(inputStream), maximumDepth);
    }

    @Override
    public void close() throws IOException, InvalidDataFormatException {
        this.dis.close();
    }

    @Override
    public DataContainer read() throws IOException, InvalidDataFormatException {
        Entry entry = readEntry();
        if (entry == null) {
            throw new IOException("There is no more data to read.");
        }
        return (DataContainer) readObject(null, entry, 0);
    }

    private Object readObject(@Nullable DataView container, Entry entry, int depth)
            throws IOException, InvalidDataFormatException {
        return readPayload(container, entry.type, entry.listType, depth);
    }

    @Nullable
    private Entry readEntry() throws IOException {
        final byte type = this.dis.readByte();
        if (type == NbtType.END.type) {
            return null;
        }
        String name = this.dis.readUTF();
        int index = name.lastIndexOf('$');
        NbtType nbtType = NbtType.byIndex.get(type);
        if (nbtType == null) {
            throw new IOException("Unknown NBT Type with id: " + type);
        }
        NbtType listNbtType = null;
        if (index != -1) {
            final String suffix = name.substring(index + 1);
            name = name.substring(0, index);
            final NbtType nbtType1 = NbtType.bySuffix.get(suffix);
            if (nbtType1 != null) {
                if (nbtType == NbtType.LIST) {
                    index = name.lastIndexOf('$');
                    if (index != -1) {
                        final String li = name.substring(index + 1);
                        if (li.equals("List")) {
                            name = name.substring(0, index);
                            listNbtType = nbtType1;
                        }
                    }
                }
                if (listNbtType == null) {
                    nbtType = nbtType1;
                }
            }
        }
        return new Entry(name, nbtType, listNbtType);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Object readPayload(@Nullable DataView container, NbtType nbtType, @Nullable NbtType listNbtType,
            int depth) throws IOException, InvalidDataFormatException {
        if (depth > this.maximumDepth) {
            throw new IOException("Attempted to read a data container with too high complexity,"
                    + " exceeded the maximum depth of " + this.maximumDepth);
        }
        int length;
        switch (nbtType) {
            case BYTE:
                return this.dis.readByte();
            case BYTE_ARRAY:
                length = this.dis.readInt();
                if (length == 0) {
                    return emptyByteArray;
                }
                final byte[] byteArray = new byte[length];
                this.dis.read(byteArray);
                return byteArray;
            case SHORT:
                return this.dis.readShort();
            case SHORT_ARRAY:
                byte type = this.dis.readByte();
                length = this.dis.readInt();
                if (type == NbtType.END.type && length != 0) {
                    throw new IllegalStateException("Got a list tag with end tags which isn't empty.");
                } else if (type != NbtType.SHORT.type) {
                    throw new IOException("Attempted to deserialize a Short Array (List) but the list type wasn't a short.");
                }
                if (length == 0) {
                    return emptyShortArray;
                }
                final short[] shortArray = new short[length];
                for (int i = 0; i < shortArray.length; i++) {
                    shortArray[i] = this.dis.readShort();
                }
                return shortArray;
            case CHAR:
                return this.dis.readUTF().charAt(0);
            case CHAR_ARRAY:
                return this.dis.readUTF().toCharArray();
            case INT:
                return this.dis.readInt();
            case INT_ARRAY:
                length = this.dis.readInt();
                if (length == 0) {
                    return emptyIntArray;
                }
                final int[] intArray = new int[length];
                for (int i = 0; i < intArray.length; i++) {
                    intArray[i] = this.dis.readInt();
                }
                return intArray;
            case LONG:
                return this.dis.readLong();
            case LONG_ARRAY:
                length = this.dis.readInt();
                if (length == 0) {
                    return emptyLongArray;
                }
                final long[] longArray = new long[length];
                for (int i = 0; i < longArray.length; i++) {
                    longArray[i] = this.dis.readLong();
                }
                return longArray;
            case FLOAT:
                return this.dis.readFloat();
            case FLOAT_ARRAY:
                type = this.dis.readByte();
                length = this.dis.readInt();
                if (type == NbtType.END.type && length != 0) {
                    throw new IllegalStateException("Got a list tag with end tags which isn't empty.");
                } else if (type != NbtType.FLOAT.type) {
                    throw new IOException("Attempted to deserialize a Float Array (List) but the list type wasn't a float.");
                }
                if (length == 0) {
                    return emptyFloatArray;
                }
                final float[] floatArray = new float[length];
                for (int i = 0; i < floatArray.length; i++) {
                    floatArray[i] = this.dis.readFloat();
                }
                return floatArray;
            case DOUBLE:
                return this.dis.readDouble();
            case DOUBLE_ARRAY:
                type = this.dis.readByte();
                length = this.dis.readInt();
                if (type == NbtType.END.type && length != 0) {
                    throw new IllegalStateException("Got a list tag with end tags which isn't empty.");
                } else if (type != NbtType.DOUBLE.type) {
                    throw new IOException("Attempted to deserialize a Double Array (List) but the list type wasn't a double.");
                }
                if (length == 0) {
                    return emptyDoubleArray;
                }
                final double[] doubleArray = new double[length];
                for (int i = 0; i < doubleArray.length; i++) {
                    doubleArray[i] = this.dis.readDouble();
                }
                return doubleArray;
            case STRING:
                return this.dis.readUTF();
            case STRING_ARRAY:
                type = this.dis.readByte();
                length = this.dis.readInt();
                if (type == NbtType.END.type && length != 0) {
                    throw new IllegalStateException("Got a list tag with end tags which isn't empty.");
                } else if (type != NbtType.STRING.type) {
                    throw new IOException("Attempted to deserialize a String Array (List) but the list type wasn't a string.");
                }
                if (length == 0) {
                    return emptyStringArray;
                }
                final String[] stringArray = new String[length];
                for (int i = 0; i < stringArray.length; i++) {
                    stringArray[i] = this.dis.readUTF();
                }
                return stringArray;
            case BOOLEAN:
                return this.dis.readBoolean();
            case BOOLEAN_ARRAY:
                int bitBytes = this.dis.readInt() - 2;
                length = this.dis.readShort() & 0xffff;
                if (length == 0) {
                    return emptyBooleanArray;
                }
                final boolean[] booleanArray = new boolean[length];
                int j = 0;
                for (int i = 0; i < bitBytes; i++) {
                    final byte value = this.dis.readByte();
                    while (j < booleanArray.length) {
                        final int k = j % 8;
                        booleanArray[j++] = (value & (1 << k)) != 0;
                    }
                }
                return booleanArray;
            case LIST:
                final byte listType = this.dis.readByte();
                if (listNbtType == null) {
                    listNbtType = NbtType.byIndex.get(listType);
                    if (listNbtType == null) {
                        throw new IOException("Unknown NBT Type with id: " + listType);
                    }
                }
                final int size = this.dis.readInt();
                final List<Object> list = Lists.newArrayListWithExpectedSize(size);
                if (size == 0 || listNbtType == NbtType.END) {
                    return list;
                }
                int depth1 = depth + 1;
                for (int i = 0; i < size; i++) {
                    list.add(readPayload(null, listNbtType, null, depth1));
                }
                return list;
            case COMPOUND:
                if (container == null) {
                    container = new MemoryDataContainer(DataView.SafetyMode.NO_DATA_CLONED);
                }
                depth1 = depth + 1;
                Entry entry;
                while ((entry = readEntry()) != null) {
                    if (entry.type == NbtType.COMPOUND) {
                        readObject(container.createView(DataQuery.of(entry.name)), entry, depth1);
                    } else {
                        container.set(DataQuery.of(entry.name), readObject(null, entry, depth1));
                    }
                }
                return container;
            case COMPOUND_ARRAY:
                type = this.dis.readByte();
                length = this.dis.readInt();
                if (type == NbtType.END.type && length != 0) {
                    throw new IllegalStateException("Got a list tag with end tags which isn't empty.");
                } else if (type != NbtType.COMPOUND.type) {
                    throw new IOException("Attempted to deserialize a DataView Array (List) but the list type wasn't a data view.");
                }
                if (length == 0) {
                    return emptyDataViewArray;
                }
                depth1 = depth + 1;
                final DataView[] dataViewArray = new DataView[length];
                for (int i = 0; i < dataViewArray.length; i++) {
                    dataViewArray[i] = (DataView) readPayload(null, NbtType.COMPOUND, null, depth1);
                }
                return dataViewArray;
            case MAP:
                type = this.dis.readByte();
                length = this.dis.readInt();
                final Map<Object, Object> map = new HashMap<>();
                if (type == NbtType.END.type && length != 0) {
                    throw new IllegalStateException("Got a list tag with end tags which isn't empty.");
                } else if (type != NbtType.COMPOUND.type) {
                    throw new IOException("Attempted to deserialize a Map (List) but the list type wasn't a compound.");
                }
                depth1 = depth + 2; // We got two more levels, compounds within a list
                for (int i = 0; i < length; i++) {
                    Object key = null;
                    Object value = null;
                    // Read a compound tag, we only need a K and V entry
                    while ((entry = readEntry()) != null) {
                        final Object object = readObject(null, entry, depth1);
                        if (entry.name.equals(NbtType.mapKeyName)) {
                            key = object;
                        } else if (entry.name.equals(NbtType.mapValueName)) {
                            value = object;
                        }
                    }
                    if (key == null) {
                        throw new IOException("Map entry was missing a key entry.");
                    } else if (value == null) {
                        throw new IOException("Map entry was missing a value entry.");
                    }
                    map.put(key, value);
                }
                return map;
            case MAP_ARRAY:
                type = this.dis.readByte();
                length = this.dis.readInt();
                if (type == NbtType.END.type && length != 0) {
                    throw new IllegalStateException("Got a list tag with end tags which isn't empty.");
                } else if (type != NbtType.LIST.type) {
                    throw new IOException("Attempted to deserialize a Map Array (List) but the list type wasn't a list.");
                }
                if (length == 0) {
                    return emptyMapArray;
                }
                final Map[] mapArray = new Map[length];
                depth1 = depth + 1;
                for (int i = 0; i < mapArray.length; i++) {
                    mapArray[i] = (Map) readPayload(null, NbtType.MAP, null, depth1);
                }
                return mapArray;
            default:
                throw new InvalidDataFormatException("Attempt to deserialize a unknown nbt tag type: " + nbtType);
        }
    }

    private static class Entry {

        private final String name;
        private final NbtType type;
        @Nullable private final NbtType listType;

        public Entry(String name, NbtType type, @Nullable NbtType listType) {
            this.listType = listType;
            this.name = name;
            this.type = type;
        }
    }
}
