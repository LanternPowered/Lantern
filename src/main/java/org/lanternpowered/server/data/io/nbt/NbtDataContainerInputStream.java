/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
package org.lanternpowered.server.data.io.nbt;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.data.io.nbt.NbtConstants.*;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;
import javax.annotation.Nullable;

import org.lanternpowered.server.data.io.DataContainerInput;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;

import com.google.common.collect.Lists;

/**
 * A data input stream that deserializes data views from the nbt format.
 */
public class NbtDataContainerInputStream implements Closeable, DataContainerInput {

    private final DataInputStream dis;

    /**
     * Creates a new (nbt) tag input stream.
     * 
     * @param dataInputStream the data input stream
     */
    public NbtDataContainerInputStream(DataInputStream dataInputStream) {
        this.dis = checkNotNull(dataInputStream, "dataInputStream");
    }

    /**
     * Creates a new nbt data view input stream.
     * 
     * @param inputStream the data input stream
     */
    public NbtDataContainerInputStream(InputStream inputStream) {
        this(checkNotNull(inputStream, "inputStream") instanceof DataInputStream ?
                (DataInputStream) inputStream : new DataInputStream(inputStream));
    }

    /**
     * Creates a new nbt data view input stream.
     * 
     * @param inputStream the data input stream
     * @param compressed whether the content is compressed
     * @throws IOException 
     */
    public NbtDataContainerInputStream(InputStream inputStream, boolean compressed) throws IOException {
        this(compressed ? new GZIPInputStream(checkNotNull(inputStream, "inputStream")) : inputStream);
    }

    @Override
    public void close() throws IOException {
        this.dis.close();
    }

    @Override
    public DataContainer read() throws IOException {
        return (DataContainer) this.readObject(null, this.readEntry());
    }

    private Object readObject(@Nullable DataView container, Entry entry) throws IOException {
        if (entry == null || entry.type == END) {
            return null;
        }
        return this.readPayload(container, entry.type);
    }

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
    private Object readPayload(@Nullable DataView container, byte type) throws IOException {
        if (type == BYTE) {
            return this.dis.readByte();
        } else if (type == BYTE_ARRAY) {
            byte[] array = new byte[this.dis.readInt()];
            this.dis.read(array);
            return array;
        } else if (type == COMPOUND) {
            if (container == null) {
                container = new MemoryDataContainer();
            }
            Entry entry;
            while ((entry = this.readEntry()) != null) {
                if (entry.type == COMPOUND) {
                    this.readObject(container.createView(DataQuery.of(entry.name)), entry);
                } else {
                    container.set(DataQuery.of('.', entry.name), this.readObject(null, entry));
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
            for (int i = 0; i < size; i++) {
                Object payload = this.readPayload(null, type0);
                if (payload != null) {
                    list.add(payload);
                }
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
        }
        return null;
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
