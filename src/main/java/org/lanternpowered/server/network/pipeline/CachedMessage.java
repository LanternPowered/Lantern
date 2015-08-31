package org.lanternpowered.server.network.pipeline;

import io.netty.buffer.ByteBuf;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Nullable;

import org.lanternpowered.server.network.message.Message;

import com.google.common.collect.Maps;

public class CachedMessage {

    private final ConcurrentMap<Integer, ByteBuf> encodedMessages = Maps.newConcurrentMap();
    private final ConcurrentMap<Integer, List<Message>> processedMessages = Maps.newConcurrentMap();

    @Nullable
    public List<Message> getProcessedMessages(int hashCode) {
        return this.processedMessages.get(hashCode);
    }

    public void setProcessedMessagesIfAbsent(int hashCode, List<Message> messages) {
        this.processedMessages.putIfAbsent(hashCode, messages);
    }

    @Nullable
    public ByteBuf getEncodedMessage(int hashCode) {
        return this.encodedMessages.get(hashCode);
    }

    public void setEncodedMessageIfAbsent(Class<?> processor, int hashCode, ByteBuf message) {
        this.encodedMessages.putIfAbsent(hashCode, message);
    }
}
