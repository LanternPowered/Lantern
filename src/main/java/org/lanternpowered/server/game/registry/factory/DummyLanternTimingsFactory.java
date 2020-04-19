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
import co.aikar.timings.TimingsFactory;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.channel.MessageChannel;

import org.checkerframework.checker.nullness.qual.Nullable;

public class DummyLanternTimingsFactory implements TimingsFactory {

    private static final Timing DUMMY_TIMING = new DummyLanternTiming();

    public void init() {
        // Ignore
    }

    @Override
    public Timing of(PluginContainer plugin, String name, @Nullable Timing groupHandler) {
        return DUMMY_TIMING;
    }

    @Override
    public boolean isTimingsEnabled() {
        return false;
    }

    @Override
    public void setTimingsEnabled(boolean enabled) {
        // Ignore
    }

    @Override
    public boolean isVerboseTimingsEnabled() {
        return false;
    }

    @Override
    public void setVerboseTimingsEnabled(boolean enabled) {
        // Ignore
    }

    @Override
    public int getHistoryInterval() {
        return 0;
    }

    @Override
    public void setHistoryInterval(int interval) {
        // Ignore
    }

    @Override
    public int getHistoryLength() {
        return 0;
    }

    @Override
    public void setHistoryLength(int length) {
        // Ignore
    }

    @Override
    public void reset() {
        // Ignore
    }

    @Override
    public void generateReport(MessageChannel channel) {
        // Ignore
    }
}
