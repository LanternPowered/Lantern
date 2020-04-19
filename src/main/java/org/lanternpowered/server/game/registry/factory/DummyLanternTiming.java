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
package org.lanternpowered.server.game.registry.factory;

import co.aikar.timings.Timing;

public class DummyLanternTiming implements Timing {

    @Override
    public Timing startTiming() {
        return this;
    }

    @Override
    public void stopTiming() {
        // Ignore
    }

    @Override
    public void startTimingIfSync() {
        // Ignore
    }

    @Override
    public void stopTimingIfSync() {
        // Ignore
    }

    @Override
    public void abort() {
        // Ignore
    }

    @Override
    public void close() {
        // Ignore
    }
}
