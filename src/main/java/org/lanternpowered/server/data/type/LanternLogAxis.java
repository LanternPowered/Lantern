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
package org.lanternpowered.server.data.type;

import com.google.common.collect.Maps;
import org.lanternpowered.server.catalog.InternalCatalogType;
import org.lanternpowered.server.catalog.SimpleCatalogType;
import org.spongepowered.api.data.type.LogAxis;
import org.spongepowered.api.util.Axis;
import org.spongepowered.api.util.Direction;

import java.util.Map;

import javax.annotation.Nullable;

public enum LanternLogAxis implements LogAxis, SimpleCatalogType, InternalCatalogType {

    Y           ("y", Axis.Y) {
        @Override
        public LogAxis cycleNext() {
            return Z;
        }
    },
    X           ("x", Axis.X) {
        @Override
        public LogAxis cycleNext() {
            return Y;
        }
    },
    Z           ("z", Axis.Z) {
        @Override
        public LogAxis cycleNext() {
            return X;
        }
    },
    NONE        ("none", null) {
        @Override
        public LogAxis cycleNext() {
            return NONE;
        }
    },
    ;

    private final static Map<Axis, LanternLogAxis> lookupByAxis = Maps.newEnumMap(Axis.class);

    private final String identifier;
    @Nullable private final Axis axis;

    LanternLogAxis(String identifier, @Nullable Axis axis) {
        this.identifier = identifier;
        this.axis = axis;
    }

    @Override
    public String getId() {
        return this.identifier;
    }

    @Override
    public int getInternalId() {
        return this.ordinal();
    }

    static {
        for (LanternLogAxis axis : values()) {
            if (axis.axis != null) {
                lookupByAxis.put(axis.axis, axis);
            }
        }
    }

    /**
     * Gets the log axis for the specified axis.
     *
     * @param axis the axis
     * @return the log axis
     */
    public static LanternLogAxis fromAxis(Axis axis) {
        return lookupByAxis.get(axis);
    }

    /**
     * Gets the log axis for the the specified direction.
     *
     * @param direction the direction
     * @return the log axis
     */
    public static LanternLogAxis fromDirection(Direction direction) {
        return fromAxis(Axis.getClosest(direction.asOffset()));
    }

}
