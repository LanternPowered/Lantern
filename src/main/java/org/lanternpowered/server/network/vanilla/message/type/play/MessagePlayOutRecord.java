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
import org.spongepowered.api.effect.sound.music.MusicDisc;
import org.spongepowered.math.vector.Vector3i;

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class MessagePlayOutRecord implements Message {

    private final Vector3i position;
    @Nullable private final MusicDisc musicDisc;

    public MessagePlayOutRecord(Vector3i position, @Nullable MusicDisc musicDisc) {
        this.position = position;
        this.musicDisc = musicDisc;
    }

    public Vector3i getPosition() {
        return this.position;
    }

    public Optional<MusicDisc> getMusicDisc() {
        return Optional.ofNullable(this.musicDisc);
    }
}
