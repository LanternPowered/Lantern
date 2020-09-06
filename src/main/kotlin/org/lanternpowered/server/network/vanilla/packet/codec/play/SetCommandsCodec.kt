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
package org.lanternpowered.server.network.vanilla.packet.codec.play

import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.command.ArgumentNode
import org.lanternpowered.server.network.vanilla.command.LiteralNode
import org.lanternpowered.server.network.vanilla.command.Node
import org.lanternpowered.server.network.vanilla.command.argument.Argument
import org.lanternpowered.server.network.vanilla.command.argument.ArgumentAndType
import org.lanternpowered.server.network.vanilla.packet.type.play.SetCommandsPacket

object SetCommandsCodec : PacketEncoder<SetCommandsPacket> {

    private const val ABSENT_VALUE = -1

    private fun collectChildren(node: Node, nodeToIndexMap: Object2IntMap<Node>, nodes: MutableList<Node>) {
        val index = nodeToIndexMap.size
        if (nodeToIndexMap.putIfAbsent(node, index) == ABSENT_VALUE)
            nodes.add(node)
        val redirect = node.redirect
        if (redirect != null)
            collectChildren(redirect, nodeToIndexMap, nodes)
        for (child in node.children)
            collectChildren(child, nodeToIndexMap, nodes)
    }

    override fun encode(context: CodecContext, packet: SetCommandsPacket): ByteBuffer {
        val buf = context.byteBufAlloc().buffer()
        val rootNode = packet.rootNode

        // Collect all the commands nested within the root node
        val nodeToIndexMap = Object2IntOpenHashMap<Node>()
        nodeToIndexMap.defaultReturnValue(ABSENT_VALUE)
        val nodes = mutableListOf<Node>()
        collectChildren(rootNode, nodeToIndexMap, nodes)

        // Write all the nodes
        buf.writeVarInt(nodes.size)
        for (node in nodes) {
            var flags = 0
            val redirect = node.redirect
            if (redirect != null)
                flags += 0x8
            val command = node.command
            if (command != null)
                flags += 0x4
            if (node is ArgumentNode) {
                flags += 0x2
                if (node.customSuggestions != null)
                    flags += 0x10
            } else if (node is LiteralNode) {
                flags += 0x1
            }
            // Writes the flags
            buf.writeByte(flags.toByte())
            // Write the children indexes
            val children = node.children
            buf.writeVarInt(children.size) // The amount of children
            for (child in children)
                buf.writeVarInt(nodeToIndexMap.getInt(child))
            // Write the redirect node index
            if (redirect != null)
                buf.writeVarInt(nodeToIndexMap.getInt(redirect))
            if (node is ArgumentNode) {
                val argumentAndType = node.argumentAndType.uncheckedCast<ArgumentAndType<Argument>>()
                buf.writeString(node.name)
                // Write the type of the argument
                buf.writeString(argumentAndType.type.id)
                // Write extra argument flags
                argumentAndType.type.codec.encode(buf, argumentAndType.argument)
                val suggestions = node.customSuggestions
                if (suggestions != null)
                    buf.writeString(suggestions.id)
            } else if (node is LiteralNode) {
                buf.writeString(node.literal)
            }
        }
        buf.writeVarInt(0) // The root should always be on index 0
        return buf
    }
}
