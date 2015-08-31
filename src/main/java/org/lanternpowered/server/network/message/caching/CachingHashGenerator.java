package org.lanternpowered.server.network.message.caching;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;

public interface CachingHashGenerator<T extends Message> {

    /**
     * Generates a hash for the context and message that is used
     * for the caching system of messages.
     * 
     * @param context the context
     * @return the hash code
     */
    int generate(CodecContext context, T message);

    /**
     * This hash generator will always return {@code 0}, this means that all the
     * messages that are processed for each context, will give the same result.
     */
    public static final class Equal implements CachingHashGenerator<Message> {

        @Override
        public int generate(CodecContext context, Message message) {
            return 0;
        }
    }
}
