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
package org.lanternpowered.server.data.persistence.mojangson;

import com.google.common.io.CharStreams;
import org.lanternpowered.server.data.persistence.AbstractStringDataFormat;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class MojangsonDataFormat extends AbstractStringDataFormat {

    private final int flags;

    public MojangsonDataFormat(String identifier, int flags) {
        super(identifier);
        this.flags = flags;
    }

    @Override
    public DataContainer read(String input) throws InvalidDataException {
        final Object value = Mojangson.parse(input, this.flags);
        if (value instanceof DataContainer) {
            return (DataContainer) value;
        }
        throw new MojangsonParseException("Expected a DataContainer but got: " + value.getClass().getName());
    }

    @Override
    public DataContainer readFrom(Reader input) throws InvalidDataException, IOException {
        return read(CharStreams.toString(input));
    }

    @Override
    public String write(DataView data) {
        return Mojangson.to(data, this.flags);
    }

    @Override
    public void writeTo(Writer output, DataView data) throws IOException {
        try (BufferedWriter writer = ensureBuffered(output)) {
            writer.write(write(data));
        }
    }
}
