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
package org.lanternpowered.server.advancement.layout;

import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector2d;
import com.google.common.base.MoreObjects;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.TreeLayoutElement;

public class LanternTreeLayoutElement implements TreeLayoutElement {

    private final Advancement advancement;
    private Vector2d position = Vector2d.ZERO;

    public LanternTreeLayoutElement(Advancement advancement) {
        this.advancement = advancement;
    }

    @Override
    public Advancement getAdvancement() {
        return this.advancement;
    }

    @Override
    public Vector2d getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(double x, double y) {
        this.position = new Vector2d(x, y);
    }

    @Override
    public void setPosition(Vector2d position) {
        checkNotNull(position, "position");
        this.position = position;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("advancement", this.advancement.getKey())
                .add("position", this.position)
                .toString();
    }
}
