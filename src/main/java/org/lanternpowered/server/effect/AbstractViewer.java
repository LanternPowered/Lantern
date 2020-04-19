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
package org.lanternpowered.server.effect;

import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.effect.sound.SoundCategory;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.math.vector.Vector3d;

public interface AbstractViewer extends Viewer {

    @Override
    default void playSound(SoundType sound, SoundCategory category, Vector3d position, double volume) {
        playSound(sound, category, position, volume, 1.0);
    }

    @Override
    default void playSound(SoundType sound, SoundCategory category, Vector3d position, double volume, double pitch) {
        playSound(sound, category, position, volume, pitch, 0.0);
    }

}
