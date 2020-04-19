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
package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.effect.sound.SoundCategory;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class MessagePlayOutStopSounds implements Message {

    @Nullable private final String sound;
    @Nullable private final SoundCategory soundCategory;

    public MessagePlayOutStopSounds(@Nullable String sound, @Nullable SoundCategory category) {
        this.soundCategory = category;
        this.sound = sound;
    }

    @Nullable
    public String getSound() {
        return this.sound;
    }

    @Nullable
    public SoundCategory getCategory() {
        return this.soundCategory;
    }
}
