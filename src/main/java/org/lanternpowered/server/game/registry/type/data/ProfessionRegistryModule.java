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

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.lanternpowered.server.data.type.LanternProfession;
import org.lanternpowered.server.game.registry.InternalPluginCatalogRegistryModule;
import org.lanternpowered.server.game.registry.forge.ForgeCatalogRegistryModule;
import org.lanternpowered.server.game.registry.forge.ForgeRegistryData;
import org.spongepowered.api.data.type.Profession;
import org.spongepowered.api.data.type.Professions;

public class ProfessionRegistryModule extends InternalPluginCatalogRegistryModule<Profession> implements ForgeCatalogRegistryModule<Profession> {

    public ProfessionRegistryModule() {
        super(Professions.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternProfession("minecraft", "farmer", 0));
        register(new LanternProfession("minecraft", "librarian", 1));
        register(new LanternProfession("minecraft", "priest", 2));
        register(new LanternProfession("minecraft", "blacksmith", 3));
        register(new LanternProfession("minecraft", "butcher", 4));
        register(new LanternProfession("minecraft", "nitwit", 5));
    }

    @Override
    public ForgeRegistryData getRegistryData() {
        final Object2IntMap<String> mappings = getRegistryDataMappings();
        // Forge uses a different profession id then in sponge, fix this mapping just for the client
        mappings.put("minecraft:smith", mappings.remove("minecraft:blacksmith"));
        return new ForgeRegistryData("minecraft:villagerprofessions", mappings);
    }
}
