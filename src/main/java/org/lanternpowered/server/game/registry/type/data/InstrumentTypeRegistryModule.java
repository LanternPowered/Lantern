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
package org.lanternpowered.server.game.registry.type.data;

import org.lanternpowered.server.data.type.LanternInstrumentType;
import org.lanternpowered.server.game.registry.PluginCatalogRegistryModule;
import org.spongepowered.api.data.type.InstrumentType;
import org.spongepowered.api.data.type.InstrumentTypes;

public class InstrumentTypeRegistryModule extends PluginCatalogRegistryModule<InstrumentType> {

    public InstrumentTypeRegistryModule() {
        super(InstrumentTypes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternInstrumentType("minecraft", "harp", 0));
        register(new LanternInstrumentType("minecraft", "bass_drum", 1));
        register(new LanternInstrumentType("minecraft", "snare", 2));
        register(new LanternInstrumentType("minecraft", "high_hat", 3));
        register(new LanternInstrumentType("minecraft", "bass_attack", 4));
        register(new LanternInstrumentType("minecraft", "flute", 5));
        register(new LanternInstrumentType("minecraft", "bell", 6));
        register(new LanternInstrumentType("minecraft", "guitar", 7));
        register(new LanternInstrumentType("minecraft", "chime", 8));
        register(new LanternInstrumentType("minecraft", "xylophone", 9));
    }
}
