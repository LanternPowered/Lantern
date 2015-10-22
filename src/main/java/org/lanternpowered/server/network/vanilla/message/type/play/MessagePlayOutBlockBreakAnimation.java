/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
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

import org.lanternpowered.server.network.message.Message;

import com.flowpowered.math.vector.Vector3i;

public final class MessagePlayOutBlockBreakAnimation implements Message {

    private final Vector3i position;
    private final int state;
    private final int id;

    /**
     * Creates a new block break animation message. The id must be unique for
     * every break animation and the state must be between 0-9 in order to
     * create/update the animation, and any other value will remove it.
     * 
     * @param position the position
     * @param id the id
     * @param state the state
     */
    public MessagePlayOutBlockBreakAnimation(Vector3i position, int id, int state) {
        this.position = position;
        this.state = state;
        this.id = id;
    }

    /**
     * Gets the position of the block animation.
     * 
     * @return the position
     */
    public Vector3i getPosition() {
        return this.position;
    }

    /**
     * Gets the id of the block animation.
     * 
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets the state of the block animation.
     * 
     * @return the state
     */
    public int getState() {
        return this.state;
    }
}
