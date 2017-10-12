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
package org.lanternpowered.server.data.persistence.nbt;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.lanternpowered.server.data.MemoryDataContainer;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.GZIPOutputStream;

public class NbtDataContainerStreamsTest {

    private static final DataQuery A = DataQuery.of("A");
    private static final DataQuery B = DataQuery.of("B");
    private static final DataQuery C = DataQuery.of("C");
    private static final DataQuery D = DataQuery.of("D");
    private static final DataQuery E = DataQuery.of("E");
    private static final DataQuery F = DataQuery.of("F");
    private static final DataQuery G = DataQuery.of("G");
    private static final DataQuery H = DataQuery.of("H");
    private static final DataQuery I = DataQuery.of("I");
    private static final DataQuery J = DataQuery.of("J");
    private static final DataQuery K = DataQuery.of("K");
    private static final DataQuery L = DataQuery.of("L");
    private static final DataQuery M = DataQuery.of("M");
    private static final DataQuery N = DataQuery.of("N");
    private static final DataQuery O = DataQuery.of("O");
    private static final DataQuery P = DataQuery.of("P");
    private static final DataQuery Q = DataQuery.of("Q");
    private static final DataQuery R = DataQuery.of("R");
    private static final DataQuery S = DataQuery.of("S");

    @Test
    public void test() throws IOException {
        final DataContainer container = new MemoryDataContainer(DataView.SafetyMode.NO_DATA_CLONED);
        container.set(A, (byte) 54);
        container.set(B, (short) 5493);
        container.set(C, (int) 95601);
        container.set(D, (long) 950698203987L);
        container.set(E, (double) 9820.9843647895114d);
        container.set(F, (float) 9.5789f);
        container.createView(G)
                .set(A, "TestA")
                .set(B, 6904);
        container.set(H, Lists.newArrayList("A", "B", "C", "D"));
        container.set(I, new byte[] { 1, 2, 3, 4 });
        container.set(J, new int[] { 1, 2, 3, 4 });
        container.set(K, new double[] { 1, 2, 3, 4 });
        container.set(L, new float[] { 1, 2, 3, 4 });
        container.set(M, new long[] { 1, 2, 3, 4 });
        container.set(N, new char[] { 'A', 'B', 'C' });
        container.set(O, 'C');
        container.set(P, new short[] { 1, 2, 3, 4 });
        container.set(Q, true);
        container.set(R, new boolean[] { true, false, true, true });
        container.set(S, new String[] { "S", "s", "z" });

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        NbtDataContainerOutputStream ndcos = new NbtDataContainerOutputStream(dos);

        ndcos.write(container);
        ndcos.flush();
        byte[] content = baos.toByteArray();
        ndcos.close();

        OutputStream os = new GZIPOutputStream(Files.newOutputStream(Paths.get("test.nbt")));
        os.write(content);
        os.flush();
        os.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(content);
        DataInputStream dis = new DataInputStream(bais);
        NbtDataContainerInputStream ndcis = new NbtDataContainerInputStream(dis);

        DataContainer newContainer = ndcis.read();
        ndcis.close();

        assertEquals(container, newContainer);
    }
}
