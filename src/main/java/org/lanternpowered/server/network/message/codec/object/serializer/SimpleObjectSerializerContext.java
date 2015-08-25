package org.lanternpowered.server.network.message.codec.object.serializer;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

public class SimpleObjectSerializerContext extends AbstractObjectSerializerContext {

    /**
     * A byte buf allocator that will always return a heap buffer to
     * allow the {@link ByteBuf#array()} to work.
     */
    private static final UnpooledByteBufAllocator ALLOCATOR = new UnpooledByteBufAllocator(false);

    public SimpleObjectSerializerContext(ObjectSerializers objectSerializers) {
        super(objectSerializers);
    }

    @Override
    public ByteBufAllocator byteBufAlloc() {
        return ALLOCATOR;
    }

}
