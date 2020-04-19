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
package org.lanternpowered.server.game.registry.type.text

import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.text.selector.LanternArgumentHolder
import org.spongepowered.api.entity.EntityType
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.registry.RegistryModule
import org.spongepowered.api.registry.util.RegisterCatalog
import org.spongepowered.api.registry.util.RegistrationDependency
import org.spongepowered.api.text.selector.ArgumentHolder
import org.spongepowered.api.text.selector.ArgumentType
import org.spongepowered.api.text.selector.ArgumentTypes
import org.spongepowered.math.vector.Vector3d
import org.spongepowered.math.vector.Vector3i
import java.util.HashMap

@RegistrationDependency(SelectorFactoryRegistryModule::class)
class ArgumentTypeRegistryModule : RegistryModule {

    @RegisterCatalog(ArgumentTypes::class)
    private val argumentTypeMap = HashMap<String, ArgumentHolder<*>>()

    override fun registerDefaults() {
        @Suppress("DEPRECATION")
        val factory = Lantern.getGame().registry.selectorFactory

        // POSITION
        val x = factory.createArgumentType("x", Int::class.java)
        val y = factory.createArgumentType("y", Int::class.java)
        val z = factory.createArgumentType("z", Int::class.java)
        val position = LanternArgumentHolder.LanternVector3(x, y, z, Vector3i::class.java)
        this.argumentTypeMap["position"] = position

        // RADIUS
        val rMin = factory.createArgumentType("rm", Int::class.java)
        val rMax = factory.createArgumentType("r", Int::class.java)
        val radius = LanternArgumentHolder.LanternLimit<ArgumentType<Int>>(rMin, rMax)
        this.argumentTypeMap["radius"] = radius

        // GAME_MODE
        this.argumentTypeMap["game_mode"] = factory.createInvertibleArgumentType("m", GameMode::class.java)

        // COUNT
        this.argumentTypeMap["count"] = factory.createArgumentType("c", Int::class.java)

        // LEVEL
        val lMin = factory.createArgumentType("lm", Int::class.java)
        val lMax = factory.createArgumentType("l", Int::class.java)
        val level = LanternArgumentHolder.LanternLimit<ArgumentType<Int>>(lMin, lMax)
        this.argumentTypeMap["level"] = level

        // TEAM
        this.argumentTypeMap["team"] = factory.createInvertibleArgumentType("team", String::class.java)

        // NAME
        this.argumentTypeMap["name"] = factory.createInvertibleArgumentType("name", String::class.java)

        // DIMENSION
        val dx = factory.createArgumentType("dx", Int::class.java)
        val dy = factory.createArgumentType("dy", Int::class.java)
        val dz = factory.createArgumentType("dz", Int::class.java)
        val dimension = LanternArgumentHolder.LanternVector3(dx, dy, dz, Vector3i::class.java)
        this.argumentTypeMap["dimension"] = dimension

        // ROTATION
        val rotXMin = factory.createArgumentType("rxm", Double::class.java)
        val rotYMin = factory.createArgumentType("rym", Double::class.java)
        val rotZMin = factory.createArgumentType("rzm", Double::class.java)
        val rotMin = LanternArgumentHolder.LanternVector3(rotXMin, rotYMin, rotZMin, Vector3d::class.java)
        val rotXMax = factory.createArgumentType("rx", Double::class.java)
        val rotYMax = factory.createArgumentType("ry", Double::class.java)
        val rotZMax = factory.createArgumentType("rz", Double::class.java)
        val rotMax = LanternArgumentHolder.LanternVector3(rotXMax, rotYMax, rotZMax, Vector3d::class.java)
        val rot = LanternArgumentHolder.LanternLimit<ArgumentHolder.Vector3<Vector3d, Double>>(rotMin, rotMax)
        this.argumentTypeMap["rotation"] = rot

        // ENTITY_TYPE
        this.argumentTypeMap["entity_type"] = factory.createInvertibleArgumentType("type", EntityType::class.java)
    }
}
