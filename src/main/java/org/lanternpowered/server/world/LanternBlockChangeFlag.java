/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
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
        return updateNeighbors ? andFlag(MASK_NEIGHBOR) : andNotFlag(MASK_NEIGHBOR);
    }

    @Override
    public LanternBlockChangeFlag withPhysics(boolean performBlockPhysics) {
        return performBlockPhysics ? andFlag(MASK_PHYSICS) : andNotFlag(MASK_PHYSICS);
    }

    @Override
    public LanternBlockChangeFlag withNotifyObservers(boolean notifyObservers) {
        return notifyObservers ? andFlag(MASK_OBSERVER) : andNotFlag(MASK_OBSERVER);
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
