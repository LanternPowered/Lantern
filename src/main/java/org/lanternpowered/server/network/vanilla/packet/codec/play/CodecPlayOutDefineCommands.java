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
package org.lanternpowered.server.network.vanilla.packet.codec.play;

import io.netty.handler.codec.CodecException;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.command.ArgumentNode;
import org.lanternpowered.server.network.vanilla.command.LiteralNode;
import org.lanternpowered.server.network.vanilla.command.Node;
import org.lanternpowered.server.network.vanilla.command.RootNode;
import org.lanternpowered.server.network.vanilla.command.SuggestionType;
import org.lanternpowered.server.network.vanilla.command.argument.ArgumentAndType;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutDefineCommands;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public final class CodecPlayOutDefineCommands implements Codec<PacketPlayOutDefineCommands> {

    private static final int ABSENT_VALUE = -1;

    @Override
    public ByteBuffer encode(CodecContext context, PacketPlayOutDefineCommands message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        final RootNode rootNode = message.getRootNode();

        // Collect all the commands nested within the root node
        final Object2IntMap<Node> nodeToIndexMap = new Object2IntOpenHashMap<>();
        nodeToIndexMap.defaultReturnValue(ABSENT_VALUE);
        final List<Node> nodes = new ArrayList<>();
        collectChildren(rootNode, nodeToIndexMap, nodes);

        // Write all the nodes
        buf.writeVarInt(nodes.size());
        for (Node node : nodes) {
            byte flags = 0;
            final Node redirect = node.redirect;
            if (redirect != null) {
                flags |= 0x8;
            }
            final String command = node.command;
            if (command != null) {
                flags |= 0x4;
            }
            if (node instanceof ArgumentNode) {
                final ArgumentNode argumentNode = (ArgumentNode) node;
                flags |= 0x2;
                if (argumentNode.getCustomSuggestions() != null) {
                    flags |= 0x10;
                }
            } else if (node instanceof LiteralNode) {
                flags |= 0x1;
            }
            // Writes the flags
            buf.writeByte(flags);
            // Write the children indexes
            final List<Node> children = node.children;
            buf.writeVarInt(children.size()); // The amount of children
            children.forEach(child -> buf.writeVarInt(nodeToIndexMap.getInt(child)));
            // Write the redirect node index
            if (redirect != null) {
                buf.writeVarInt(nodeToIndexMap.getInt(redirect));
            }
            if (node instanceof ArgumentNode) {
                final ArgumentNode argumentNode = (ArgumentNode) node;
                final ArgumentAndType argumentAndType = argumentNode.getArgumentAndType();
                buf.writeString(argumentNode.getName());
                // Write the type of the argument
                buf.writeString(argumentAndType.getType().id);
                // Write extra argument flags
                argumentAndType.getType().codec.encode(buf, argumentAndType.getArgument());
                final SuggestionType suggestions = argumentNode.getCustomSuggestions();
                if (suggestions != null) {
                    buf.writeString(suggestions.getId());
                }
            } else if (node instanceof LiteralNode) {
                final LiteralNode literalNode = (LiteralNode) node;
                buf.writeString(literalNode.getLiteral());
            }
        }
        buf.writeVarInt(0); // The root should always be on index 0
        return buf;
    }

    private static void collectChildren(Node node, Object2IntMap<Node> nodeToIndexMap, List<Node> nodes) {
        final int index = nodeToIndexMap.size();
        if (nodeToIndexMap.putIfAbsent(node, index) == ABSENT_VALUE) {
            nodes.add(node);
        }
        final Node redirect = node.redirect;
        if (redirect != null) {
            collectChildren(redirect, nodeToIndexMap, nodes);
        }
        node.children.forEach(child -> collectChildren(child, nodeToIndexMap, nodes));
    }
}
