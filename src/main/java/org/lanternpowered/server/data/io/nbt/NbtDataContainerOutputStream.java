/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
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
import java.io.DataOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;

import org.lanternpowered.server.data.io.DataContainerOutput;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;

/**
 * A data output stream that serializes data views into the nbt format.
 */
public class NbtDataContainerOutputStream implements Closeable, Flushable, DataContainerOutput {

    private final DataOutputStream dos;

    /**
     * Creates a new nbt data view output stream.
     * 
     * @param dataOutputStream the data output stream
     */
    public NbtDataContainerOutputStream(DataOutputStream dataOutputStream) {
        this.dos = checkNotNull(dataOutputStream, "dataOutputStream");
    }

    /**
     * Creates a new nbt data view output stream.
     * 
     * @param outputStream the output stream
     */
    public NbtDataContainerOutputStream(OutputStream outputStream) {
        this(checkNotNull(outputStream, "outputStream") instanceof DataOutputStream ?
                (DataOutputStream) outputStream : new DataOutputStream(outputStream));
    }

    /**
     * Creates a new nbt data view output stream.
     * 
     * @param outputStream the output stream
     * @param compressed whether the content is compressed
     * @throws IOException 
     */
    public NbtDataContainerOutputStream(OutputStream outputStream, boolean compressed) throws IOException {
        this(compressed ? new GZIPOutputStream(checkNotNull(outputStream, "outputStream")) : outputStream);
    }

    @Override
    public void close() throws IOException {
        this.dos.close();
    }

    @Override
    public void flush() throws IOException {
        this.dos.flush();
    }

    @Override
    public void write(DataView dataView) throws IOException {
        this.writeEntry("", checkNotNull(dataView, "dataView"));
    }

    @SuppressWarnings("unchecked")
    private void writePayload(byte type, Object object) throws IOException {
        if (type == BYTE) {
            if (object instanceof Boolean) {
                object = (byte) (((Boolean) object) ? 1 : 0);
            }
            this.dos.writeByte((Byte) object);
        } else if (type == BYTE_ARRAY) {
            byte[] array0;
            if (object instanceof byte[]) {
                array0 = (byte[]) object;
            } else {
                Byte[] array1 = (Byte[]) object;
                array0 = new byte[array1.length];
                for (int i = 0; i < array0.length; i++) {
                    array1[i] = array0[i];
                }
            }
            this.dos.writeShort(array0.length);
            this.dos.write(array0);
        } else if (type == COMPOUND) {
            // Convert the object in something we can serialize
            if (object instanceof DataView) {
                object = ((DataView) object).getValues(false);
            } else if (object instanceof DataSerializable) {
                object = ((DataSerializable) object).toContainer().getValues(false);
            }
            for (Entry<DataQuery, Object> entry : ((Map<DataQuery, Object>) object).entrySet()) {
                this.writeEntry(entry.getKey().asString('.'), entry.getValue());
            }
            this.dos.writeByte(END);
        } else if (type == DOUBLE) {
            this.dos.writeDouble((Double) object);
        } else if (type == FLOAT) {
            this.dos.writeFloat((Float) object);
        } else if (type == INT) {
            this.dos.writeInt((Integer) object);
        } else if (type == INT_ARRAY) {
            if (object instanceof int[]) {
                int[] array0 = (int[]) object;
                this.dos.writeShort(array0.length);
                for (int i = 0; i < array0.length; i++) {
                    this.dos.writeInt(array0[i]);
                }
            } else {
                Integer[] array0 = (Integer[]) object;
                this.dos.writeShort(array0.length);
                for (int i = 0; i < array0.length; i++) {
                    this.dos.writeInt(array0[i]);
                }
            }
        } else if (type == LIST) {
            List<Object> list = (List<Object>) object;
            byte type0 = END;
            if (!list.isEmpty()) {
                type0 = this.typeFor(list.get(0));
            }
            this.dos.writeByte(type0);
            this.dos.writeInt(list.size());
            for (Object object0 : list) {
                this.writePayload(type0, object0);
            }
        } else if (type == LONG) {
            this.dos.writeLong((Long) object);
        } else if (type == SHORT) {
            this.dos.writeShort((Short) object);
        } else if (type == STRING) {
            this.dos.writeUTF((String) object);
        }
    }

    private void writeEntry(String key, Object object) throws IOException {
        byte type = this.typeFor(object);
        this.dos.writeByte(type);
        this.dos.writeUTF(key);
        this.writePayload(type, object);
    }

    private byte typeFor(Object object) {
        if (object instanceof Byte || object instanceof Boolean) {
            return BYTE;
        } else if (object instanceof Byte[] || object instanceof byte[]) {
            return BYTE_ARRAY;
        } else if (object instanceof Map || object instanceof DataView || object instanceof DataSerializable) {
            return COMPOUND;
        } else if (object instanceof Double) {
            return DOUBLE;
        } else if (object instanceof Float) {
            return FLOAT;
        } else if (object instanceof Integer) {
            return INT;
        } else if (object instanceof Integer[] || object instanceof int[]) {
            return INT_ARRAY;
        } else if (object instanceof List) {
            return LIST;
        } else if (object instanceof Long) {
            return LONG;
        } else if (object instanceof Short) {
            return SHORT;
        } else if (object instanceof String) {
            return STRING;
        }
        return 0;
    }

}
