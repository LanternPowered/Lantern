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
package org.lanternpowered.server.inventory.property;

public final class SmeltingProgress {

    private final int maxBurnTime;
    private final int elapsedBurnTime;

    private final int maxSmeltTime;
    private final int elapsedSmeltTime;

    public SmeltingProgress(int maxBurnTime, int elapsedBurnTime, int maxSmeltTime, int elapsedSmeltTime) {
        this.maxBurnTime = maxBurnTime;
        this.elapsedBurnTime = elapsedBurnTime;
        this.maxSmeltTime = maxSmeltTime;
        this.elapsedSmeltTime = elapsedSmeltTime;
    }

    public int getMaxBurnTime() {
        return this.maxBurnTime;
    }

    public int getElapsedBurnTime() {
        return this.elapsedBurnTime;
    }

    public int getMaxSmeltTime() {
        return this.maxSmeltTime;
    }

    public int getElapsedSmeltTime() {
        return this.elapsedSmeltTime;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SmeltingProgress)) {
            return false;
        }
        final SmeltingProgress o1 = (SmeltingProgress) o;
        return o1.elapsedBurnTime == this.elapsedBurnTime &&
                o1.elapsedSmeltTime == this.elapsedSmeltTime &&
                o1.maxBurnTime == this.maxBurnTime &&
                o1.maxSmeltTime == this.maxSmeltTime;
    }
}
