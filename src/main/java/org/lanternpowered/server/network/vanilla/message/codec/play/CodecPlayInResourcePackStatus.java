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
package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.DecoderException;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInResourcePackStatus;
import org.spongepowered.api.event.entity.living.humanoid.player.ResourcePackStatusEvent.ResourcePackStatus;

public final class CodecPlayInResourcePackStatus implements Codec<MessagePlayInResourcePackStatus> {

    private final Int2ObjectMap<ResourcePackStatus> status = new Int2ObjectOpenHashMap<>();

    {
        this.status.put(0, ResourcePackStatus.SUCCESSFULLY_LOADED);
        this.status.put(1, ResourcePackStatus.DECLINED);
        this.status.put(2, ResourcePackStatus.FAILED);
        this.status.put(3, ResourcePackStatus.ACCEPTED);
    }

    @Override
    public MessagePlayInResourcePackStatus decode(CodecContext context, ByteBuffer buf) throws CodecException {
        int status0 = buf.readVarInt();
        ResourcePackStatus status = this.status.get(status0);
        if (status == null) {
            throw new DecoderException("Unknown status: " + status0);
        }
        return new MessagePlayInResourcePackStatus(status);
    }
}
