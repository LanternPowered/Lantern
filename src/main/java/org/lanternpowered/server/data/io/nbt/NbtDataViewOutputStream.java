package org.lanternpowered.server.data.io.nbt;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.data.io.nbt.NbtConstants.*;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lanternpowered.server.data.io.DataViewOutput;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;

/**
 * A data output stream that serializes data views into the nbt format.
 */
public class NbtDataViewOutputStream implements Closeable, Flushable, DataViewOutput {

    private final DataOutputStream dos;

    /**
     * Creates a new nbt data view output stream.
     * 
     * @param dataOutputStream the data output stream
     */
    public NbtDataViewOutputStream(DataOutputStream dataOutputStream) {
        this.dos = checkNotNull(dataOutputStream, "dataOutputStream");
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
            this.dos.writeByte((Byte) object);
        } else if (type == BYTE_ARRAY) {
            Byte[] array0 = (Byte[]) object;
            byte[] array1 = new byte[array0.length];
            for (int i = 0; i < array0.length; i++) {
                array1[i] = array0[i];
            }
            this.dos.writeShort(array1.length);
            this.dos.write(array1);
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
            Integer[] array0 = (Integer[]) object;
            this.dos.writeShort(array0.length);
            for (int i = 0; i < array0.length; i++) {
                this.dos.writeInt(array0[i]);
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
        if (object instanceof Byte) {
            return BYTE;
        } else if (object instanceof Byte[]) {
            return BYTE_ARRAY;
        } else if (object instanceof Map || object instanceof DataView || object instanceof DataSerializable) {
            return COMPOUND;
        } else if (object instanceof Double) {
            return DOUBLE;
        } else if (object instanceof Float) {
            return FLOAT;
        } else if (object instanceof Integer) {
            return INT;
        } else if (object instanceof Integer[]) {
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
