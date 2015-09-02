package org.lanternpowered.server.network.pipeline;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;

import org.lanternpowered.server.game.LanternGame;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

public final class MessageEncryptionHandler extends MessageToMessageCodec<ByteBuf, ByteBuf> {

    private final CryptBuf encodeBuf;
    private final CryptBuf decodeBuf;

    public MessageEncryptionHandler(SecretKey sharedSecret) {
        try {
            this.encodeBuf = new CryptBuf(Cipher.ENCRYPT_MODE, sharedSecret);
            this.decodeBuf = new CryptBuf(Cipher.DECRYPT_MODE, sharedSecret);
        } catch (GeneralSecurityException e) {
            // should never happen
            LanternGame.log().error("Failed to initialize encrypted channel", e);
            throw new AssertionError("Failed to initialize encrypted channel", e);
        }
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        this.encodeBuf.crypt(msg, out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        this.decodeBuf.crypt(msg, out);
    }

    private static class CryptBuf {

        private final Cipher cipher;

        private CryptBuf(int mode, SecretKey sharedSecret) throws GeneralSecurityException {
            this.cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            this.cipher.init(mode, sharedSecret, new IvParameterSpec(sharedSecret.getEncoded()));
        }

        public void crypt(ByteBuf msg, List<Object> out) {
            ByteBuffer outBuffer = ByteBuffer.allocate(msg.readableBytes());

            try {
                this.cipher.update(msg.nioBuffer(), outBuffer);
            } catch (ShortBufferException e) {
                throw new AssertionError("Encryption buffer was too short", e);
            }

            outBuffer.flip();
            out.add(Unpooled.wrappedBuffer(outBuffer));
        }
    }

}