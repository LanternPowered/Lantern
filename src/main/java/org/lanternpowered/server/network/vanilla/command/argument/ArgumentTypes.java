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
package org.lanternpowered.server.network.vanilla.command.argument;

public final class ArgumentTypes {

    public static final ArgumentType<Argument> BOOL = ArgumentType.of("brigadier:bool");

    public static final ArgumentType<DoubleArgument> DOUBLE = ArgumentType.of("brigadier:double",
            (buf, argument) -> {
                final Double max = argument.getMax();
                final Double min = argument.getMin();
                byte flags = 0;
                if (min != null) {
                    flags |= 0x1;
                }
                if (max != null) {
                    flags |= 0x2;
                }
                buf.writeByte(flags);
                if (min != null) {
                    buf.writeDouble(min);
                }
                if (max != null) {
                    buf.writeDouble(max);
                }
            });

    public static final ArgumentType<FloatArgument> FLOAT = ArgumentType.of("brigadier:float",
            (buf, argument) -> {
                final Float max = argument.getMax();
                final Float min = argument.getMin();
                byte flags = 0;
                if (min != null) {
                    flags |= 0x1;
                }
                if (max != null) {
                    flags |= 0x2;
                }
                buf.writeByte(flags);
                if (min != null) {
                    buf.writeFloat(min);
                }
                if (max != null) {
                    buf.writeFloat(max);
                }
            });

    public static final ArgumentType<IntArgument> INTEGER = ArgumentType.of("brigadier:integer",
            (buf, argument) -> {
                final Integer max = argument.getMax();
                final Integer min = argument.getMin();
                byte flags = 0;
                if (min != null) {
                    flags |= 0x1;
                }
                if (max != null) {
                    flags |= 0x2;
                }
                buf.writeByte(flags);
                if (min != null) {
                    buf.writeInteger(min);
                }
                if (max != null) {
                    buf.writeInteger(max);
                }
            });

    public static final ArgumentType<StringArgument> STRING = ArgumentType.of("brigadier:string",
            (buf, argument) -> buf.writeVarInt(argument.getType().ordinal()));

    public static final ArgumentType<EntityArgument> ENTITY = ArgumentType.of("minecraft:entity",
            (buf, argument) -> {
                byte flags = 0;
                if (!argument.allowMultipleEntities()) {
                    flags |= 0x1;
                }
                if (!argument.allowOnlyPlayers()) {
                    flags |= 0x2;
                }
                buf.writeByte(flags);
            });

    public static final ArgumentType<Argument> GAME_PROFILE = ArgumentType.of("minecraft:game_profile");

    public static final ArgumentType<Argument> BLOCK_POS = ArgumentType.of("minecraft:block_pos");

    public static final ArgumentType<Argument> VEC3 = ArgumentType.of("minecraft:vec3");

    public static final ArgumentType<Argument> VEC2 = ArgumentType.of("minecraft:vec2");

    public static final ArgumentType<Argument> BLOCK = ArgumentType.of("minecraft:block");

    public static final ArgumentType<Argument> ITEM = ArgumentType.of("minecraft:item");

    public static final ArgumentType<Argument> COLOR = ArgumentType.of("minecraft:color");

    public static final ArgumentType<Argument> COMPONENT = ArgumentType.of("minecraft:component");

    public static final ArgumentType<Argument> MESSAGE = ArgumentType.of("minecraft:message");

    public static final ArgumentType<Argument> NBT = ArgumentType.of("minecraft:nbt");

    public static final ArgumentType<Argument> NBT_PATH = ArgumentType.of("minecraft:nbt_path");

    public static final ArgumentType<Argument> OBJECTIVE = ArgumentType.of("minecraft:objective");

    public static final ArgumentType<Argument> OBJECTIVE_CRITERIA = ArgumentType.of("minecraft:objective_criteria");

    public static final ArgumentType<Argument> OPERATION = ArgumentType.of("minecraft:operation");

    public static final ArgumentType<Argument> PARTICLE = ArgumentType.of("minecraft:particle");

    public static final ArgumentType<Argument> ROTATION = ArgumentType.of("minecraft:rotation");

    public static final ArgumentType<Argument> SCOREBOARD_SLOT = ArgumentType.of("minecraft:scoreboard_slot");

    public static final ArgumentType<ScoreHolderArgument> SCORE_HOLDER = ArgumentType.of("minecraft:score_holder",
            (buf, argument) -> {
                byte flags = 0;
                if (argument.hasUnknownFlag()) {
                    flags |= 0x1;
                }
                if (argument.allowMultipleEntities()) {
                    flags |= 0x2;
                }
                buf.writeByte(flags);
            });

    public static final ArgumentType<Argument> SWIZZLE = ArgumentType.of("minecraft:swizzle");

    public static final ArgumentType<Argument> TEAM = ArgumentType.of("minecraft:team");

    public static final ArgumentType<Argument> ITEM_SLOT = ArgumentType.of("minecraft:item_slot");

    public static final ArgumentType<Argument> RESOURCE_LOCATION = ArgumentType.of("minecraft:resource_location");

    public static final ArgumentType<Argument> MOB_EFFECT = ArgumentType.of("minecraft:mob_effect");
}
