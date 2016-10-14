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

import org.lanternpowered.server.data.type.LanternOcelotType;
import org.lanternpowered.server.game.registry.PluginCatalogRegistryModule;
import org.spongepowered.api.data.type.OcelotType;
import org.spongepowered.api.data.type.OcelotTypes;

public class OcelotTypeRegistryModule extends PluginCatalogRegistryModule<OcelotType> {

    public OcelotTypeRegistryModule() {
        super(OcelotTypes.class);
    }

    @Override
    public void registerDefaults() {
        this.register(new LanternOcelotType("minecraft", "wild_ocelot", 0));
        this.register(new LanternOcelotType("minecraft", "black_cat", 1));
        this.register(new LanternOcelotType("minecraft", "red_cat", 2));
        this.register(new LanternOcelotType("minecraft", "siamese_cat", 3));
    }
}
