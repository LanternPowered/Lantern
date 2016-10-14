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
package org.lanternpowered.server.game.registry.type.util;

import org.lanternpowered.server.game.registry.SimpleCatalogRegistryModule;
import org.lanternpowered.server.util.rotation.LanternRotation;
import org.spongepowered.api.util.rotation.Rotation;
import org.spongepowered.api.util.rotation.Rotations;

import java.util.Optional;

public final class RotationRegistryModule extends SimpleCatalogRegistryModule<Rotation> {

    public RotationRegistryModule() {
        super(Rotations.class);
    }

    @Override
    public void registerDefaults() {
        this.register(new LanternRotation("top", 0));
        this.register(new LanternRotation("top_right", 45));
        this.register(new LanternRotation("right", 90));
        this.register(new LanternRotation("bottom_right", 135));
        this.register(new LanternRotation("bottom", 180));
        this.register(new LanternRotation("bottom_left", 225));
        this.register(new LanternRotation("left", 270));
        this.register(new LanternRotation("top_left", 315));
    }

    public Optional<Rotation> getRotationFromDegree(int degrees) {
        while (degrees < 0) {
            degrees += 360;
        }
        while (degrees > 360) {
            degrees -= 360;
        }
        int angle = Math.round(degrees / 360 * 8) * 45;
        for (Rotation rotation : this.getAll()) {
            if (rotation.getAngle() == angle) {
                return Optional.of(rotation);
            }
        }
        return Optional.empty();
    }
}
