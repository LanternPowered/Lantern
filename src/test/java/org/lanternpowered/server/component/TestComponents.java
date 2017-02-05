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
package org.lanternpowered.server.component;

import org.junit.Test;
import org.lanternpowered.server.component.test.TestComponent;

public class TestComponents {

    @Test
    public void test() {
        //long time = System.currentTimeMillis();

        ComponentHolder[] holders = new BaseComponentHolder[10000];
        for (int i = 0; i < holders.length; i++) {
            holders[i] = new BaseComponentHolder();
        }

        final long start = System.nanoTime();
        for (int i = 0; i < holders.length; i++) {
            holders[i].addComponent(TestComponent.class);
        }
        System.out.println("Took " + (System.nanoTime() - start) / (1000 * holders.length) + "ms");
        /*
        BaseComponentHolder holder = new BaseComponentHolder();


        System.out.println("Took: " + (System.currentTimeMillis() - time) + "ms");
        time = System.currentTimeMillis();
        TestComponent test = holder.addComponent(TestComponent.class);
        System.out.println("Took: " + (System.currentTimeMillis() - time) + "ms");
        time = System.currentTimeMillis();
        assertNotNull(test.holder);
        System.out.println(test.holder.getClass());
        assertNotNull(test.other);
        System.out.println(test.other.getClass());
        assertNotNull(test.another);
        System.out.println(test.another.getClass());
        holder = new BaseComponentHolder();
        System.out.println("Took: " + (System.currentTimeMillis() - time) + "ms");
        time = System.currentTimeMillis();
        holder.addComponent(TestComponent.class);
        System.out.println("Took: " + (System.currentTimeMillis() - time) + "ms");*/
    }
}
