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

class ArgumentAndType<A : Argument> private constructor(val type: ArgumentType<A>, val argument: A) {

    companion object {

        @JvmStatic
        fun <A : Argument> of(type: ArgumentType<A>, argument: A): ArgumentAndType<A> = ArgumentAndType(type, argument)

        @JvmStatic
        fun of(type: ArgumentType<EmptyArgument>): ArgumentAndType<EmptyArgument> = ArgumentAndType(type, EmptyArgument)
    }
}
