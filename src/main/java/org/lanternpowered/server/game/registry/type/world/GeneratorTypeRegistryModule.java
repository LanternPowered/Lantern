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
package org.lanternpowered.server.game.registry.type.world;

import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.game.registry.type.block.BlockStateRegistryModule;
import org.lanternpowered.server.world.gen.LanternGeneratorTypeNether;
import org.lanternpowered.server.world.gen.debug.DebugGeneratorType;
import org.lanternpowered.server.world.gen.flat.FlatGeneratorType;
import org.lanternpowered.server.world.gen.skylands.SkylandsGeneratorType;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.GeneratorTypes;

@RegistrationDependency({ BlockRegistryModule.class, BlockStateRegistryModule.class })
public final class GeneratorTypeRegistryModule extends AdditionalPluginCatalogRegistryModule<GeneratorType> {

    public GeneratorTypeRegistryModule() {
        super(GeneratorTypes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternGeneratorTypeNether("minecraft", "nether"));
        register(new FlatGeneratorType("minecraft", "flat"));
        register(new DebugGeneratorType("minecraft", "debug"));
        register(new SkylandsGeneratorType("sponge", "skylands"));
        // TODO: Add the misc generator types
        register(new FlatGeneratorType("minecraft", "default"));
        register(new FlatGeneratorType("minecraft", "overworld"));
        register(new FlatGeneratorType("minecraft", "the_end"));
        register(new FlatGeneratorType("minecraft", "large_biomes"));
        register(new FlatGeneratorType("minecraft", "amplified"));
    }
}
