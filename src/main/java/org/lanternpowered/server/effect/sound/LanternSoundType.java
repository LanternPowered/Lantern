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
package org.lanternpowered.server.effect.sound;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector3d;
import org.lanternpowered.server.catalog.PluginCatalogType;
import org.lanternpowered.server.catalog.VirtualCatalogType;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutNamedSoundEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSoundEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSoundEffectBase;
import org.spongepowered.api.effect.sound.SoundCategory;
import org.spongepowered.api.effect.sound.SoundType;

public class LanternSoundType extends PluginCatalogType.Base implements SoundType {

    private final int eventId;

    public LanternSoundType(String pluginId, String id, String name, int eventId) {
        super(pluginId, id, name);
        checkArgument(eventId >= 0);
        this.eventId = eventId;
    }

    private LanternSoundType(String pluginId, String id, String name) {
        super(pluginId, id, name);
        this.eventId = -1;
    }

    /**
     * Gets the event id. Returns {@code -1} when virtual (file name based).
     *
     * @return The event id
     */
    public int getEventId() {
        return this.eventId;
    }

    public MessagePlayOutSoundEffectBase createMessage(Vector3d position, SoundCategory soundCategory,
            float volume, float pitch) {
        checkNotNull(soundCategory, "soundCategory");
        checkNotNull(position, "position");
        if (this.eventId != -1) {
            return new MessagePlayOutSoundEffect(this.eventId, position, soundCategory, volume, pitch);
        } else {
            return new MessagePlayOutNamedSoundEffect(getName(), position, soundCategory, volume, pitch);
        }
    }

    static class Virtual extends LanternSoundType implements VirtualCatalogType {

        Virtual(String pluginId, String id, String name) {
            super(pluginId, id, name);
        }
    }
}
