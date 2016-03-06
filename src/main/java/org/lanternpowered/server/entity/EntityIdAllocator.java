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
package org.lanternpowered.server.entity;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public final class EntityIdAllocator {

    private static final EntityIdAllocator INSTANCE = new EntityIdAllocator();

    public static EntityIdAllocator get() {
        return INSTANCE;
    }

    private final Queue<Integer> reusableIds = new LinkedBlockingQueue<>();
    private final AtomicInteger idCounter = new AtomicInteger();

    /**
     * Polls a new id from the allocator.
     *
     * @return the id
     */
    public int poll() {
        Integer id = this.reusableIds.poll();
        if (id != null) {
            return id;
        }
        return this.idCounter.getAndIncrement();
    }

    public int[] poll(int count) {
        return this.poll(new int[count]);
    }

    public int[] poll(int[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = this.poll();
        }
        return array;
    }

    /**
     * Pushes a id back to be reused.
     *
     * <p>WARNING: Do not push ids back twice or
     * when they are still in use, it may cause
     * some unforeseen issues.</p>
     */
    public void push(int id) {
        this.reusableIds.offer(id);
    }
}