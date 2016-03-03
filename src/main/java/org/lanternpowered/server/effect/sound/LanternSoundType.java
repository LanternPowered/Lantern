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
package org.lanternpowered.server.effect.sound;

import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector3d;
import org.lanternpowered.server.catalog.LanternCatalogType;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutNamedSoundEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSoundEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSoundEffectBase;
import org.lanternpowered.server.util.OptInt;
import org.spongepowered.api.effect.sound.SoundCategoryType;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.OptionalInt;

import javax.annotation.Nullable;

@NonnullByDefault
public final class LanternSoundType extends LanternCatalogType implements SoundType {

    private final OptionalInt eventId;
    private final SoundCategoryType defaultCategory;

    public LanternSoundType(String identifier, String name, SoundCategoryType defaultCategory,
            @Nullable Integer eventId) {
        super(identifier, name);
        this.eventId = OptInt.ofNullable(eventId);
        this.defaultCategory = checkNotNull(defaultCategory, "defaultCategory");
    }

    public MessagePlayOutSoundEffectBase createMessage(Vector3d position, SoundCategoryType soundCategory,
            float volume, float pitch) {
        checkNotNull(soundCategory, "soundCategory");
        checkNotNull(position, "position");
        if (this.eventId.isPresent()) {
            return new MessagePlayOutSoundEffect(this.eventId.getAsInt(), position,
                    soundCategory, volume, pitch);
        } else {
            return new MessagePlayOutNamedSoundEffect(this.getName(), position,
                    soundCategory, volume, pitch);
        }
    }

    public SoundCategoryType getDefaultCategory() {
        return this.defaultCategory;
    }
}
