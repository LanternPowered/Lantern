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
package org.lanternpowered.server.network.vanilla.message.type.play;

import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.server.block.action.BlockActionData;
import org.lanternpowered.server.network.message.Message;

public final class MessagePlayOutBlockAction implements Message, BlockActionData {

    private final Vector3i position;
    private final int blockType;
    private final int[] parameters = new int[2];

    public MessagePlayOutBlockAction(Vector3i position, int blockType) {
        this.blockType = blockType;
        this.position = position;
    }

    public Vector3i getPosition() {
        return this.position;
    }

    public int getBlockType() {
        return this.blockType;
    }

    public int[] getParameters() {
        return this.parameters;
    }

    @Override
    public void set(int index, int data) {
        if (index >= 0 && index < this.parameters.length) {
            this.parameters[index] = data;
        }
    }
}
