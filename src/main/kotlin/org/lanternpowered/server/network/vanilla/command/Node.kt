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
package org.lanternpowered.server.network.vanilla.command

import org.lanternpowered.server.network.vanilla.command.argument.ArgumentAndType

sealed class Node(
        val children: List<Node>,
        val command: String?,
        val redirect: Node?
)

class LiteralNode(
        children: List<Node>, val literal: String, command: String?, redirect: Node?
) : Node(children, command, redirect)

class RootNode(
        children: List<Node>, command: String?, redirect: Node?
) : Node(children, command, redirect)

class ArgumentNode(
        children: List<Node>,
        val name: String,
        val argumentAndType: ArgumentAndType<*>,
        command: String?,
        redirect: Node?,
        val customSuggestions: SuggestionType?
) : Node(children, command, redirect)
