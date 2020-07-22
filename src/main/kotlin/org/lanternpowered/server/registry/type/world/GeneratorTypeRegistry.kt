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
package org.lanternpowered.server.registry.type.world

import org.lanternpowered.api.namespace.minecraftKey
import org.lanternpowered.api.namespace.spongeKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.api.world.generator.GeneratorType
import org.lanternpowered.server.world.gen.DelegateGeneratorType
import org.lanternpowered.server.world.gen.debug.DebugGeneratorType
import org.lanternpowered.server.world.gen.flat.FlatNetherGeneratorType
import org.lanternpowered.server.world.gen.flat.FlatOverworldGeneratorType
import org.lanternpowered.server.world.gen.flat.FlatTheEndGeneratorType
import org.lanternpowered.server.world.gen.thevoid.TheVoidGeneratorType

val GeneratorTypeRegistry = catalogTypeRegistry<GeneratorType> {
    val flat = FlatOverworldGeneratorType(minecraftKey("flat"))
    val flatNether = FlatNetherGeneratorType(minecraftKey("flat_the_nether"))
    val flatTheEnd = FlatTheEndGeneratorType(minecraftKey("flat_the_end"))

    // Default inbuilt generator types
    register(flat)
    register(flatNether)
    register(flatTheEnd)
    register(DebugGeneratorType(minecraftKey("debug")))

    // Plugin provided generator types, these will fall back
    // to flat if missing
    register(DelegateGeneratorType(minecraftKey("default"), flat))
    register(DelegateGeneratorType(minecraftKey("overworld"), flat))
    register(DelegateGeneratorType(minecraftKey("large_biomes"), flat))
    register(DelegateGeneratorType(minecraftKey("amplified"), flat))
    register(DelegateGeneratorType(minecraftKey("the_nether"), flatNether))
    register(DelegateGeneratorType(minecraftKey("the_end"), flatTheEnd))

    // Sponge
    register(TheVoidGeneratorType(spongeKey("the_void")))

    // TODO: Reintroduce the loading of default generator types, again through the
    //  'default-world-gen.json'? Or maybe as an event or service instead.
}
