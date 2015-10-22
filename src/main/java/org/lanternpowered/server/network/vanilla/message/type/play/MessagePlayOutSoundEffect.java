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

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.message.Message;

import com.flowpowered.math.vector.Vector3d;

public final class MessagePlayOutSoundEffect implements Message {

    private final String name;
    private final Vector3d position;

    private final float volume;
    private final float pitch;

    /**
     * Creates a new sound effect message.
     * 
     * @param name The effect name
     * @param x The x coordinate
     * @param y The y coordinate
     * @param z The z coordinate
     * @param volume The volume
     * @param pitch The pitch value
     */
    public MessagePlayOutSoundEffect(String name, Vector3d position, float volume, float pitch) {
        this.position = checkNotNull(position, "position");
        this.name = checkNotNull(name, "name");
        this.volume = volume;
        this.pitch = pitch;
    }

    /**
     * Gets the volume of the sound effect.
     * 
     * @return The volume
     */
    public float getVolume() {
        return this.volume;
    }

    /**
     * Gets the pitch of the sound effect.
     * 
     * @return The pitch value
     */
    public float getPitch() {
        return this.pitch;
    }

    /**
     * Gets the name of the sound effect.
     * 
     * @return The name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the position of the effect.
     * 
     * @return The position
     */
    public Vector3d getPosition() {
        return this.position;
    }

}
