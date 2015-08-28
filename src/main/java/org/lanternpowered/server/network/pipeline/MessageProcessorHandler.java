package org.lanternpowered.server.network.pipeline;

import java.util.List;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.MessageRegistration;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.protocol.Protocol;
import org.lanternpowered.server.network.session.Session;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageEncoder;

import static org.lanternpowered.server.network.pipeline.MessageCodecHandler.CONTEXT;

/**
 * Decoding is handled in {@link MessageCodecHandler}, to avoid multiple times
 * receiving channel attributes and extra pipeline components.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MessageProcessorHandler extends MessageToMessageEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message message, List<Object> output) throws Exception {
        Protocol protocol = ctx.channel().attr(Session.STATE).get().getProtocol();
        MessageRegistration registration = protocol.outbound().find(message.getClass());

        if (registration == null) {
            throw new EncoderException("Message type (" + message.getClass().getName() + ") is not registered!");
        }

        List<Processor> processors = registration.getProcessors();
        // Only process if there are processors found
        if (!processors.isEmpty()) {
            CodecContext context = ctx.channel().attr(CONTEXT).get();
            for (Processor processor : processors) {
                // The processor should handle the output messages
                processor.process(context, message, output);
            }
        } else {
            // Push the codec context
            output.add(message);
        }
    }
}
