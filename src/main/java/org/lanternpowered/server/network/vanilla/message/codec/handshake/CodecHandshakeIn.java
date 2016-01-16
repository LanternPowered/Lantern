/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.network.vanilla.message.codec.handshake;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.codec.object.VarInt;
import org.lanternpowered.server.network.vanilla.message.type.handshake.MessageHandshakeIn;
import org.lanternpowered.server.network.vanilla.message.type.handshake.MessageHandshakeIn.ProxyData;
import org.lanternpowered.server.profile.LanternProfileProperty;
import org.lanternpowered.server.util.UUIDHelper;
import org.spongepowered.api.profile.property.ProfileProperty;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.UUID;

public final class CodecHandshakeIn implements Codec<MessageHandshakeIn> {

    private static final String FML_HANDSHAKE_TOKEN = "\0FML\0";
    private static final Gson GSON = new Gson();

    @Override
    public ByteBuf encode(CodecContext context, MessageHandshakeIn message) throws CodecException {
        throw new CodecException();
    }

    @Override
    public MessageHandshakeIn decode(CodecContext context, ByteBuf buf) throws CodecException {
        int protocol = context.read(buf, VarInt.class).value();
        String hostname = context.read(buf, String.class);
        short port = buf.readShort();
        int state = context.read(buf, VarInt.class).value();
        ProxyData proxyData = null;
        SocketAddress socketAddress;
        // Check for the fml marker
        boolean fmlMarker = hostname.contains(FML_HANDSHAKE_TOKEN);
        if (fmlMarker) {
            hostname = hostname.split(FML_HANDSHAKE_TOKEN)[0];
        }
        // Check for bungee-coord data
        String[] split = hostname.split("\00\\|", 2)[0].split("\00");
        // The position of the fml marker in the parts array, may be absent
        if (split.length > 1) {
            // TODO: Check whether bungee is enabled
            if (split.length != 3 && split.length != 4) {
                throw new CodecException("Parts length was " + split.length + ", should be 3 or 4!");
            }
            hostname = split[0];
            socketAddress = new InetSocketAddress(split[1], port);
            UUID uniqueId = UUIDHelper.fromFlatString(split[2]);
            Multimap<String, ProfileProperty> properties = LinkedHashMultimap.create();
            if (split.length == 4) {
                try {
                    JsonArray json = GSON.fromJson(split[3], JsonArray.class);
                    for (int i = 0; i < json.size(); i++) {
                        JsonObject json0 = json.get(i).getAsJsonObject();
                        String name = json0.get("name").getAsString();
                        String value = json0.get("value").getAsString();
                        String signature = json0.has("signature") ? json0.get("signature").getAsString() : null;
                        properties.put(name, new LanternProfileProperty(name, value, signature));
                    }
                } catch (Exception e) {
                    throw new CodecException(e);
                }
            }
            proxyData = new ProxyData(uniqueId, properties);
        } else {
            socketAddress = new InetSocketAddress(hostname, port);
        }
        return new MessageHandshakeIn(state, hostname, socketAddress, protocol, proxyData, fmlMarker);
    }
}
