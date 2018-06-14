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
package org.lanternpowered.server.network.vanilla.message.codec.play;

import com.flowpowered.math.vector.Vector3i;
import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTileEntity;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;

import java.util.Arrays;
import java.util.List;

public final class CodecPlayOutUpdateTileEntity implements Codec<MessagePlayOutTileEntity> {

    private static final DataQuery idQuery = DataQuery.of("id");
    private static final DataQuery xQuery = DataQuery.of("x");
    private static final DataQuery yQuery = DataQuery.of("y");
    private static final DataQuery zQuery = DataQuery.of("z");

    // The inbuilt tile entity types, to send updates
    private static final List<String> hardcodedTypes = Arrays.asList(
            "minecraft:mob_spawner",
            "minecraft:command_block",
            "minecraft:beacon",
            "minecraft:skull",
            "minecraft:flower_pot",
            "minecraft:banner",
            "minecraft:structure_block",
            "minecraft:end_gateway",
            "minecraft:sign",
            "minecraft:shulker_box",
            "minecraft:bed"
    );

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutTileEntity message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        final Vector3i pos = message.getPosition();
        buf.writeVector3i(pos);
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
