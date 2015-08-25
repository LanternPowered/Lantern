package org.lanternpowered.server.network.message.codec.object.serializer;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.TextMessageException;

public class SerializerText implements ObjectSerializer<Text> {

    @Override
    public void write(ObjectSerializerContext context, ByteBuf buf, Text object) throws CodecException {
        context.write(buf, String.class, Texts.json().to(object));
    }

    @Override
    public Text read(ObjectSerializerContext context, ByteBuf buf) throws CodecException {
        try {
            return Texts.json().from(context.read(buf, String.class));
        } catch (TextMessageException e) {
            throw new CodecException(e);
        }
    }

}
