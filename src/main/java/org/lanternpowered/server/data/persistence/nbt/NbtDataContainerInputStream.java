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
package org.lanternpowered.server.data.persistence.nbt;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.data.persistence.nbt.NbtConstants.BOOLEAN;
import static org.lanternpowered.server.data.persistence.nbt.NbtConstants.BOOLEAN_IDENTIFER;
import static org.lanternpowered.server.data.persistence.nbt.NbtConstants.BOOLEAN_LIST;
import static org.lanternpowered.server.data.persistence.nbt.NbtConstants.BYTE;
import static org.lanternpowered.server.data.persistence.nbt.NbtConstants.BYTE_ARRAY;
import static org.lanternpowered.server.data.persistence.nbt.NbtConstants.COMPOUND;
import static org.lanternpowered.server.data.persistence.nbt.NbtConstants.DOUBLE;
import static org.lanternpowered.server.data.persistence.nbt.NbtConstants.END;
import static org.lanternpowered.server.data.persistence.nbt.NbtConstants.FLOAT;
import static org.lanternpowered.server.data.persistence.nbt.NbtConstants.INT;
import static org.lanternpowered.server.data.persistence.nbt.NbtConstants.INT_ARRAY;
import static org.lanternpowered.server.data.persistence.nbt.NbtConstants.LIST;
import static org.lanternpowered.server.data.persistence.nbt.NbtConstants.LONG;
import static org.lanternpowered.server.data.persistence.nbt.NbtConstants.SHORT;
import static org.lanternpowered.server.data.persistence.nbt.NbtConstants.STRING;

import com.google.common.collect.Lists;
import org.lanternpowered.server.data.persistence.DataContainerInput;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.persistence.InvalidDataFormatException;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.annotation.Nullable;

/**
 * A data input stream that deserializes data views from the nbt format.
 */
public class NbtDataContainerInputStream implements Closeable, DataContainerInput {

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

    /**
     * Creates a new nbt data view input stream.
     * 
     * @param inputStream the data input stream
     * @param compressed whether the content is compressed
     * @throws IOException 
     */
    public NbtDataContainerInputStream(InputStream inputStream, boolean compressed) throws IOException {
        this(inputStream, compressed, Integer.MAX_VALUE);
    }

    /**
     * Creates a new nbt data view input stream.
     *
     * @param inputStream the data input stream
     * @param compressed whether the content is compressed
     * @throws IOException
     */
    public NbtDataContainerInputStream(InputStream inputStream, boolean compressed, int maximumDepth) throws IOException {
        this(compressed ? new GZIPInputStream(checkNotNull(inputStream, "inputStream")) : inputStream, maximumDepth);
    }

    @Override
    public void close() throws IOException, InvalidDataFormatException {
        this.dis.close();
    }

    @Override
    public DataContainer read() throws IOException, InvalidDataFormatException {
        Entry entry = this.readEntry();
        if (entry == null) {
            throw new IOException("There is no more data to read.");
        }
        return (DataContainer) this.readObject(null, entry, 0);
    }

    private Object readObject(@Nullable DataView container, Entry entry, int depth) throws IOException, InvalidDataFormatException {
        return this.readPayload(container, entry.type, depth);
    }

    @Nullable
    private Entry readEntry() throws IOException {
        byte type = this.dis.readByte();
        if (type == END) {
            return null;
        }
        String name = this.dis.readUTF();
        int index = name.lastIndexOf(BOOLEAN_IDENTIFER);
        if (index != -1) {
            name = name.substring(0, index);
            type = type == LIST ? BOOLEAN_LIST : BOOLEAN;
        }
        return new Entry(name, type);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Object readPayload(@Nullable DataView container, byte type, int depth) throws IOException, InvalidDataFormatException {
        if (depth > this.maximumDepth) {
            throw new IOException("Attempted to read a data container with too high complexity,"
                    + " exceeded the maximum depth of " + this.maximumDepth);
        }
        if (type == BYTE) {
            return this.dis.readByte();
        } else if (type == BYTE_ARRAY) {
            byte[] array = new byte[this.dis.readInt()];
            for (int i = 0; i < array.length; i++) {
                array[i] = this.dis.readByte();
            }
            return array;
        } else if (type == COMPOUND) {
            if (container == null) {
                container = new MemoryDataContainer();
            }
            Entry entry;
            final int depth1 = depth + 1;
            while ((entry = this.readEntry()) != null) {
                if (entry.type == COMPOUND) {
                    this.readObject(container.createView(DataQuery.of(entry.name)), entry, depth1);
                } else {
                    container.set(DataQuery.of(entry.name), this.readObject(null, entry, depth1));
                }
            }
            return container;
        } else if (type == DOUBLE) {
            return this.dis.readDouble();
        } else if (type == FLOAT) {
            return this.dis.readFloat();
        } else if (type == INT) {
            return this.dis.readInt();
        } else if (type == INT_ARRAY) {
            int[] array = new int[this.dis.readInt()];
            for (int i = 0; i < array.length; i++) {
                array[i] = this.dis.readInt();
            }
            return array;
        } else if (type == LIST || type == BOOLEAN_LIST) {
            byte type0 = this.dis.readByte();
            if (type == BOOLEAN_LIST) {
                type0 = BOOLEAN;
            }
            int size = this.dis.readInt();
            List list = Lists.newArrayListWithExpectedSize(size);
            if (size == 0 || type0 == END) {
                return list;
            }
            final int depth1 = depth + 1;
            for (int i = 0; i < size; i++) {
                list.add(this.readPayload(null, type0, depth1));
            }
            return list;
        } else if (type == LONG) {
            return this.dis.readLong();
        } else if (type == SHORT) {
            return this.dis.readShort();
        } else if (type == STRING) {
            return this.dis.readUTF();
        } else if (type == BOOLEAN) {
            return this.dis.readByte() != 0;
        } else {
            throw new InvalidDataFormatException("Attempt to deserialize a unknown nbt tag type: " + type);
        }
    }

    private static class Entry {

        private final String name;
        private final byte type;

        public Entry(String name, byte type) {
            this.name = name;
            this.type = type;
        }
    }
}
