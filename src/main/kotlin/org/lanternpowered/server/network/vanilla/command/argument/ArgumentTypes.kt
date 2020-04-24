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
package org.lanternpowered.server.network.vanilla.command.argument

object ArgumentTypes {

    val BOOL = argumentTypeOf("brigadier:bool")
    val GAME_PROFILE = argumentTypeOf("minecraft:game_profile")
    val BLOCK_POS = argumentTypeOf("minecraft:block_pos")
    val VEC3 = argumentTypeOf("minecraft:vec3")
    val VEC2 = argumentTypeOf("minecraft:vec2")
    val BLOCK = argumentTypeOf("minecraft:block")
    val ITEM = argumentTypeOf("minecraft:item")
    val COLOR = argumentTypeOf("minecraft:color")
    val COMPONENT = argumentTypeOf("minecraft:component")
    val MESSAGE = argumentTypeOf("minecraft:message")
    val NBT = argumentTypeOf("minecraft:nbt")
    val NBT_PATH = argumentTypeOf("minecraft:nbt_path")
    val OBJECTIVE = argumentTypeOf("minecraft:objective")
    val OBJECTIVE_CRITERIA = argumentTypeOf("minecraft:objective_criteria")
    val OPERATION = argumentTypeOf("minecraft:operation")
    val PARTICLE = argumentTypeOf("minecraft:particle")
    val ROTATION = argumentTypeOf("minecraft:rotation")
    val SCOREBOARD_SLOT = argumentTypeOf("minecraft:scoreboard_slot")
    val SWIZZLE = argumentTypeOf("minecraft:swizzle")
    val TEAM = argumentTypeOf("minecraft:team")
    val ITEM_SLOT = argumentTypeOf("minecraft:item_slot")
    val RESOURCE_LOCATION = argumentTypeOf("minecraft:resource_location")
    val MOB_EFFECT = argumentTypeOf("minecraft:mob_effect")

    val DOUBLE = numberArgumentTypeOf<DoubleArgument, Double>("brigadier:double") { writeDouble(it) }
    val FLOAT = numberArgumentTypeOf<FloatArgument, Float>("brigadier:float") { writeFloat(it) }
    val INTEGER = numberArgumentTypeOf<IntArgument, Int>("brigadier:integer") { writeInt(it) }
    val LONG = numberArgumentTypeOf<LongArgument, Long>("brigadier:long") { writeLong(it) }

    @JvmField
    val STRING = argumentTypeOf<StringArgument>("brigadier:string") { buf, message ->
        buf.writeVarInt(message.type.ordinal)
    }

    val ENTITY = argumentTypeOf<EntityArgument>("minecraft:entity") { buf, message ->
        var flags = 0
        if (!message.allowMultipleEntities)
            flags += 0x1
        if (!message.allowOnlyPlayers)
            flags += 0x2
        buf.writeByte(flags.toByte())
    }

    val SCORE_HOLDER = argumentTypeOf<ScoreHolderArgument>("minecraft:score_holder") { buf, message ->
        var flags = 0
        if (message.hasUnknownFlag)
            flags += 0x1
        if (message.allowMultipleEntities)
            flags += 0x2
        buf.writeByte(flags.toByte())
    }
}
