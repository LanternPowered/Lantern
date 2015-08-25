package org.lanternpowered.server.network.vanilla.message.processor.play;

import io.netty.handler.codec.CodecException;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.List;
import java.util.Locale;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInClientSettings;

public class ProcessorPlayInClientSettings implements Processor<MessagePlayInClientSettings> {

    public static final AttributeKey<Locale> LOCALE = AttributeKey.valueOf("locale");

    @Override
    public void process(CodecContext context, MessagePlayInClientSettings message, List<Message> output) throws CodecException {
        Locale locale = message.getLocale();
        Attribute<Locale> attr = context.channel().attr(LOCALE);
        if (locale.equals(Locale.ENGLISH)) {
            attr.remove();
        } else {
            attr.set(locale);
        }
        output.add(message);
    }

}
