package org.lanternpowered.server.network.message;

import javax.annotation.Nullable;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.message.processor.Processor;

public interface MessageRegistry {

    <M extends Message, P extends Processor<? super M>> MessageRegistration<M> register(Class<M> message,
            P processor);

    <M extends Message, P extends Processor<? super M>> MessageRegistration<M> register(Class<M> message,
            Class<P> processor);

    <M extends Message, C extends Codec<? super M>> MessageRegistration<M> register(int opcode, Class<M> message,
            Class<C> codec);

    <M extends Message, C extends Codec<? super M>, H extends Handler<? super M>> MessageRegistration<M> register(
            int opcode, Class<M> message, Class<C> codec, @Nullable Class<H> handler);

    <M extends Message, C extends Codec<? super M>, H extends Handler<? super M>> MessageRegistration<M> register(
            int opcode, Class<M> message, Class<C> codec, @Nullable H handler);

    <M extends Message, H extends Handler<? super M>> MessageRegistration<M> register(
            Class<M> message, H handler);

    @Nullable
    <M extends Message> MessageRegistration<M> find(Class<M> message);

    @Nullable
    <M extends Message> MessageRegistration<M> find(int opcode);
}
