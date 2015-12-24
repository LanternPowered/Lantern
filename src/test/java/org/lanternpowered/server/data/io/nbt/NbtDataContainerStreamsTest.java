/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.MemoryDataContainer;

import com.google.common.collect.Lists;

public class NbtDataContainerStreamsTest {

    private static final DataQuery A = DataQuery.of("A");
    private static final DataQuery B = DataQuery.of("B");
    private static final DataQuery C = DataQuery.of("C");
    private static final DataQuery D = DataQuery.of("D");
    private static final DataQuery E = DataQuery.of("E");
    private static final DataQuery F = DataQuery.of("F");
    private static final DataQuery G = DataQuery.of("G");
    private static final DataQuery H = DataQuery.of("H");

    @Test
    public void test() throws IOException {
        DataContainer container = new MemoryDataContainer();
        container.set(A, (byte) 54);
        container.set(B, (short) 5493);
        container.set(C, (int) 95601);
        container.set(D, (long) 950698203987L);
        container.set(E, (double) 9820.9843647895114d);
        container.set(F, (float) 9.5789f);
        container.createView(G)
                .set(A, "TestA")
                .set(B, 6904);
        List<String> entries = Lists.newArrayList("A", "B", "C", "D");
        container.set(H, entries);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        NbtDataContainerOutputStream ndcos = new NbtDataContainerOutputStream(dos);

        ndcos.write(container);
        ndcos.flush();
        byte[] content = baos.toByteArray();
        ndcos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(content);
        DataInputStream dis = new DataInputStream(bais);
        NbtDataContainerInputStream ndcis = new NbtDataContainerInputStream(dis);

        DataContainer newContainer = ndcis.read();
        ndcis.close();

        assertEquals(container, newContainer);
    }
}
