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
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutBlockEntity;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.math.vector.Vector3i;

import java.util.Arrays;
import java.util.List;

public final class CodecPlayOutUpdateBlockEntity implements Codec<PacketPlayOutBlockEntity> {

    private static final DataQuery idQuery = DataQuery.of("id");
    private static final DataQuery xQuery = DataQuery.of("x");
    private static final DataQuery yQuery = DataQuery.of("y");
    private static final DataQuery zQuery = DataQuery.of("z");

    // The inbuilt block entity types, to send updates
    private static final List<String> hardcodedTypes = Arrays.asList(
            "minecraft:mob_spawner",
            "minecraft:command_block",
            "minecraft:beacon",
            "minecraft:skull",
            "minecraft:conduit",
            "minecraft:banner",
            "minecraft:structure_block",
            "minecraft:end_gateway",
            "minecraft:sign",
            "", // Unused
            "minecraft:bed",
            "minecraft:jigsaw",
            "minecraft:campfire",
            "minecraft:beehive"
    );

    @Override
    public ByteBuffer encode(CodecContext context, PacketPlayOutBlockEntity message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        final Vector3i pos = message.getPosition();
        buf.writePosition(pos);
        final String id = message.getType();
        buf.writeByte((byte) (hardcodedTypes.indexOf(id) + 1));
        final DataView dataView = message.getTileData();
        dataView.set(idQuery, id);
        dataView.set(xQuery, pos.getX());
        dataView.set(yQuery, pos.getY());
        dataView.set(zQuery, pos.getZ());
        buf.writeDataView(dataView);
        return buf;
    }
}
