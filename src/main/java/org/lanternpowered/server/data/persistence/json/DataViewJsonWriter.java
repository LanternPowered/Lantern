package org.lanternpowered.server.data.persistence.json;

import static com.google.common.base.Preconditions.checkState;

import com.google.gson.stream.JsonWriter;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A {@link JsonWriter} that serializes to a {@link DataContainer}
 * instead of a JSON string.
 *
 * Inspired by GSON's {@link com.google.gson.internal.bind.JsonTreeWriter}.
 */
public final class DataViewJsonWriter extends JsonWriter {

    private static final Writer UNWRITABLE_WRITER = new Writer() {
        @Override public void write(char[] buffer, int offset, int counter) {
            throw new AssertionError();
        }
        @Override public void flush() throws IOException {
            throw new AssertionError();
        }
        @Override public void close() throws IOException {
            throw new AssertionError();
        }
    };

    private final List<Object> stack = new ArrayList<>();
    @Nullable private DataQuery pendingKey;
    private DataContainer result = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);

    public DataViewJsonWriter() {
        super(UNWRITABLE_WRITER);
    }

    public DataContainer getResult() throws IOException {
        close();
        return this.result;
    }

    private Object peek() {
        return this.stack.get(this.stack.size() - 1);
    }

    private Object pop() {
        return this.stack.remove(this.stack.size() - 1);
    }

    @SuppressWarnings("unchecked")
    private void put(@Nullable Object value) {
        if (this.pendingKey != null) {
            ((DataView) peek()).set(this.pendingKey, value);
            this.pendingKey = null;
        } else {
            ((List<Object>) peek()).add(value);
        }
    }

    @Override
    public JsonWriter beginArray() {
        List<Object> list = new ArrayList<>();
        put(list);
        this.stack.add(list);
        return this;
    }

    @Override
    public JsonWriter endArray() {
        checkState(!this.stack.isEmpty() && this.pendingKey == null && pop() instanceof List);
        return this;
    }

    @Override
    public JsonWriter beginObject() {
        if (this.stack.isEmpty()) {
            this.stack.add(this.result);
            return this;
        }

        Object parent = peek();
        if (parent instanceof DataView) {
            checkState(this.pendingKey != null);
            ((DataView) parent).createView(this.pendingKey);
            this.pendingKey = null;
            return this;
        }

        put(DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED));
        return this;
    }

    @Override
    public JsonWriter endObject() {
        checkState(!this.stack.isEmpty() && this.pendingKey == null && pop() instanceof DataView);
        return this;
    }

    @Override
    public JsonWriter name(String name) {
        checkState(!this.stack.isEmpty() && this.pendingKey == null && peek() instanceof DataView);
        this.pendingKey = DataQuery.of(name);
        return this;
    }

    @Override
    public JsonWriter value(String value) {
        put(value);
        return this;
    }

    @Override
    public JsonWriter nullValue() {
        put(null);
        return this;
    }

    @Override
    public JsonWriter value(boolean value) {
        put(value);
        return this;
    }

    @Override
    public JsonWriter value(double value) {
        put(value);
        return this;
    }

    @Override
    public JsonWriter value(long value) {
        put(value);
        return this;
    }

    @Override
    public JsonWriter value(Number value) {
        put(value);
        return this;
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws IOException {
        if (!this.stack.isEmpty()) {
            throw new IOException("Incomplete document");
        }
    }

}
