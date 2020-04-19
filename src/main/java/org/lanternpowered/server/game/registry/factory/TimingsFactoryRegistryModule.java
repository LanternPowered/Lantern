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

import co.aikar.timings.Timings;
import co.aikar.timings.TimingsFactory;
import org.spongepowered.api.registry.FactoryRegistry;

public class TimingsFactoryRegistryModule implements FactoryRegistry<TimingsFactory, Timings> {

    @Override
    public Class<Timings> getFactoryOwner() {
        return Timings.class;
    }

    @Override
    public TimingsFactory provideFactory() {
        return Holder.INSTANCE;
    }

    @Override
    public void initialize() {
        // Ignore
    }

    private static final class Holder {
        static final DummyLanternTimingsFactory INSTANCE = new DummyLanternTimingsFactory();
    }

}
