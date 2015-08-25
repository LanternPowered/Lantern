package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerAbilities;

public class CodecPlayOutPlayerAbilities implements Codec<MessagePlayOutPlayerAbilities> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutPlayerAbilities message) throws CodecException {
        byte bits = 0;
        // Ignore the invulnerable bit (0x1), it server side
        if (message.isFlying()) {
            bits |= 0x2;
        }
        if (message.canFly()) {
            bits |= 0x4;
        }
        // TODO: Not sure what to do with the creative bit (0x8)
        ByteBuf buf = context.byteBufAlloc().buffer();
        buf.writeByte(bits);
        buf.writeFloat(message.getFlySpeed());
        buf.writeFloat(calculateFieldOfView(message.getFieldOfView(), message.isFlying()));
        return buf;
    }

    @Override
    public MessagePlayOutPlayerAbilities decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }

    private static float calculateFieldOfView(float fov, boolean flying) {
        float x = Math.max(Math.min(fov, 1f), 0f) * 2.8f - 0.8f;
        float y = flying ? 1.1f : 1.0f; // Is this needed?
        float z = 0.1f; // movementSpeed - Just ignore this for now, it prevents sprinting.
        float w = ((y + 1f) * z) / (2 * x);
        return w;
    }

}
