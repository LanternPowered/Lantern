package org.lanternpowered.server.network.pipeline.cache;

import io.netty.buffer.ByteBuf;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.lanternpowered.server.network.message.Message;

// TODO: Figure out the best way to do this
public interface CachedMessage {

    AtomicReference<List<Message>> getProcessedMessages(Class<?> processor, int hashCode);

    AtomicReference<ByteBuf> getEncodedMessage(int hashCode);
}
