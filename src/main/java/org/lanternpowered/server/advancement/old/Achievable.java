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
package org.lanternpowered.server.advancement.old;

import it.unimi.dsi.fastutil.objects.Object2LongMap;

import java.util.OptionalLong;

public abstract class Achievable {

    static final long INVALID_TIME = -1L;

    /**
     * Gets whether this {@link Achievable} is achieved.
     *
     * @return Is achieved
     */
    public boolean achieved() {
        return get().isPresent();
    }

    /**
     * Gets the time that the {@link Achievable} was achieved if present.
     *
     * @return The achieving time
     */
    public abstract OptionalLong get();

    /**
     * Achieves this {@link Achievable}, if achieved before
     * that time will be returned.
     *
     * @return The achieving time
     */
    public abstract long set();

    /**
     * Revokes the {@link Achievable} status. The time that the {@link Achievable}
     * was achieved before will be returned if present.
     *
     * @return The previous achieving time
     */
    public abstract OptionalLong revoke();

    abstract void resetDirtyState();

    abstract void fillDirtyProgress(Object2LongMap<String> progress);

    abstract void fillProgress(Object2LongMap<String> progress);
}
