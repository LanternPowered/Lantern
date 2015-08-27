package org.lanternpowered.server.network.pipeline.cache;

import io.netty.buffer.ByteBuf;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.lanternpowered.server.network.message.Message;

public interface CachedMessage {

    AtomicReference<List<Message>> getProcessedMessages(int hashCode);

    AtomicReference<ByteBuf> getEncodedMessage(int hashCode);
}
