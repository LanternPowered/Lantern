package org.lanternpowered.server.network.vanilla.message.codec.play;

import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInResourcePackStatus;
import org.spongepowered.api.event.entity.player.PlayerResourcePackStatusEvent.ResourcePackStatus;

import com.google.common.collect.ImmutableMap;

public final class CodecPlayInResourcePackStatus implements Codec<MessagePlayInResourcePackStatus> {

    private final Map<Integer, ResourcePackStatus> status = ImmutableMap.<Integer, ResourcePackStatus>builder()
            .put(0, ResourcePackStatus.SUCCESSFULLY_LOADED)
            .put(1, ResourcePackStatus.DECLINED)
            .put(2, ResourcePackStatus.PACK_FILE_FORMAT_NOT_RECOGNIZED)
            .put(3, ResourcePackStatus.ACCEPTED)
            .build();

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayInResourcePackStatus message) throws CodecException {
        throw new CodecException();
    }

    @Override
    public MessagePlayInResourcePackStatus decode(CodecContext context, ByteBuf buf) throws CodecException {
        String hash = context.read(buf, String.class);
        int status0 = context.readVarInt(buf);
        ResourcePackStatus status = this.status.get(status0);
        if (status == null) {
            throw new CodecException("Unknown status: " + status0);
        }
        return new MessagePlayInResourcePackStatus(hash, status);
    }

}
