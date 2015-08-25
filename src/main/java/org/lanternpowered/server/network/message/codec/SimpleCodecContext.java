package org.lanternpowered.server.network.message.codec;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;

import org.lanternpowered.server.network.message.codec.object.serializer.AbstractObjectSerializerContext;
import org.lanternpowered.server.network.message.codec.object.serializer.ObjectSerializers;
import org.lanternpowered.server.network.session.Session;

public class SimpleCodecContext extends AbstractObjectSerializerContext implements CodecContext {

    private final ByteBufAllocator byteBufAllocator;
    private final Channel channel;
    private final Session session;

    public SimpleCodecContext(ObjectSerializers objectSerializers, ByteBufAllocator byteBufAllocator,
            Channel channel, Session session) {
        super(objectSerializers);

        this.byteBufAllocator = byteBufAllocator;
        this.channel = channel;
        this.session = session;
    }

    @Override
    public ByteBufAllocator byteBufAlloc() {
        return this.byteBufAllocator;
    }

    @Override
    public Channel channel() {
        return this.channel;
    }

    @Override
    public Session session() {
        return this.session;
    }

}
