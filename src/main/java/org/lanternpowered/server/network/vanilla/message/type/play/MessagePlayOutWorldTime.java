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
package org.lanternpowered.server.network.vanilla.message.type.play;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.data.world.MoonPhase;
import org.lanternpowered.server.network.message.Message;

public final class MessagePlayOutWorldTime implements Message {

    // The current moon phase
    private final MoonPhase moonPhase;

    // The time of the day
    private final int time;

    // Whether the sky should rotate
    private final boolean enabled;

    /**
     * Creates a new world time message.
     * 
     * @param moonPhase the moon phase of the world
     * @param time the time of the world
     * @param enabled whether the time is enabled
     */
    public MessagePlayOutWorldTime(MoonPhase moonPhase, int time, boolean enabled) {
        this.moonPhase = checkNotNull(moonPhase, "moonPhase");
        this.enabled = enabled;
        this.time = time;
    }

    /**
     * Gets the moon phase of the world.
     * 
     * @return the moon phase
     */
    public MoonPhase getMoonPhase() {
        return this.moonPhase;
    }

    /**
     * Gets the time of the world. Scales between 0 and 24000.
     * 
     * @return the time
     */
    public int getTime() {
        return this.time;
    }

    /**
     * Gets whether the time enabled is, this will freeze the sky animation.
     * 
     * @return enabled
     */
    public boolean getEnabled() {
        return this.enabled;
    }

}
