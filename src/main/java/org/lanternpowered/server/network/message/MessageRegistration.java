package org.lanternpowered.server.network.message;

import javax.annotation.Nullable;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.message.processor.Processor;

public interface MessageRegistration<M extends Message> {

    /**
     * Gets the message type of the registration.
     * 
     * @return the message type
     */
    Class<M> getType();

    /**
     * Gets the opcode of the registration.
     * 
     * @return the opcode
     */
    @Nullable
    Integer getOpcode();

    /**
     * Gets the processor of the registration.
     * 
     * @return the processor
     */
    <P extends Processor<? super M>> P getProcessor();

    @Nullable
    <C extends Codec<? super M>> C getCodec();

    @Nullable
    <H extends Handler<? super M>> H getHandler();
}
