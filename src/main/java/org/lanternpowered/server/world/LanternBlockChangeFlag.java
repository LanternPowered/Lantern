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
package org.lanternpowered.server.world;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import org.spongepowered.api.world.BlockChangeFlag;

import java.util.Objects;

public final class LanternBlockChangeFlag implements BlockChangeFlag {

    private final static byte MASK_NEIGHBOR = 0x1;
    private final static byte MASK_PHYSICS = 0x2;
    private final static byte MASK_OBSERVER = 0x4;

    private final static byte MASK_ALL =
            MASK_NEIGHBOR | MASK_PHYSICS | MASK_OBSERVER;

    private final static LanternBlockChangeFlag[] FLAGS = new LanternBlockChangeFlag[MASK_ALL + 1];

    public static final LanternBlockChangeFlag NONE;
    public static final LanternBlockChangeFlag ALL;
    public static final LanternBlockChangeFlag NEIGHBOR;
    public static final LanternBlockChangeFlag PHYSICS;
    public static final LanternBlockChangeFlag OBSERVER;

    static {
        for (int i = 0; i < FLAGS.length; i++) {
            FLAGS[i] = new LanternBlockChangeFlag((byte) i);
        }
        NONE = FLAGS[0];
        ALL = FLAGS[MASK_ALL];
        NEIGHBOR = FLAGS[MASK_NEIGHBOR];
        PHYSICS = FLAGS[MASK_PHYSICS];
        OBSERVER = FLAGS[MASK_OBSERVER];
    }

    private final byte flags;

    private LanternBlockChangeFlag(byte flags) {
        this.flags = flags;
    }

    @Override
    public boolean updateNeighbors() {
        return (this.flags & MASK_NEIGHBOR) != 0;
    }

    @Override
    public boolean performBlockPhysics() {
        return (this.flags & MASK_PHYSICS) != 0;
    }

    @Override
    public boolean notifyObservers() {
        return (this.flags & MASK_OBSERVER) != 0;
    }

    @Override
    public LanternBlockChangeFlag withUpdateNeighbors(boolean updateNeighbors) {
        return updateNeighbors ? andNotFlag(MASK_NEIGHBOR) : andNotFlag(MASK_NEIGHBOR);
    }

    @Override
    public LanternBlockChangeFlag withPhysics(boolean performBlockPhysics) {
        return performBlockPhysics ? andNotFlag(MASK_PHYSICS) : andNotFlag(MASK_PHYSICS);
    }

    @Override
    public LanternBlockChangeFlag withNotifyObservers(boolean notifyObservers) {
        return notifyObservers ? andNotFlag(MASK_OBSERVER) : andNotFlag(MASK_OBSERVER);
    }

    @Override
    public LanternBlockChangeFlag inverse() {
        return FLAGS[~this.flags & MASK_ALL];
    }

    @Override
    public LanternBlockChangeFlag andFlag(BlockChangeFlag flag) {
        checkNotNull(flag, "flag");
        return andFlag(((LanternBlockChangeFlag) flag).flags);
    }

    private LanternBlockChangeFlag andFlag(byte flags) {
        return FLAGS[this.flags | flags];
    }

    @Override
    public LanternBlockChangeFlag andNotFlag(BlockChangeFlag flag) {
        checkNotNull(flag, "flag");
        return andNotFlag(((LanternBlockChangeFlag) flag).flags);
    }

    private LanternBlockChangeFlag andNotFlag(byte flags) {
        return FLAGS[this.flags & ~flags];
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("updateNeighbors", updateNeighbors())
                .add("performBlockPhysics", performBlockPhysics())
                .add("notifyObservers", notifyObservers())
                .toString();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof LanternBlockChangeFlag && ((LanternBlockChangeFlag) other).flags == this.flags;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.flags);
    }
}
