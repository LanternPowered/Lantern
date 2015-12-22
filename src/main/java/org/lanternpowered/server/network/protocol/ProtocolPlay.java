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
package org.lanternpowered.server.network.protocol;

import org.lanternpowered.server.network.message.MessageRegistry;
import org.lanternpowered.server.network.vanilla.message.codec.connection.CodecInOutPing;
import org.lanternpowered.server.network.vanilla.message.codec.connection.CodecOutDisconnect;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInChangeSign;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInChatMessage;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInOutCustomPayload;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInOutHeldItemChange;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInResourcePackStatus;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutChatMessage;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutPlayerHealthUpdate;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutPlayerJoinGame;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutPlayerRespawn;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutPlayerSpawnPosition;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSoundEffect;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutSpawnParticle;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutTitle;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutWorldTime;
import org.lanternpowered.server.network.vanilla.message.handler.connection.HandlerInPing;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInChangeSign;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInChannelPayload;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInChatMessage;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInRegisterChannels;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInResourcePackStatus;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInUnregisterChannels;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayInChannelPayload;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayInClientSettings;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayInPlayerAction;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayInPlayerVehicleControls;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutBrand;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutChannelPayload;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutOpenBook;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutOpenCredits;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutParticleEffect;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutPlayerJoinGame;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutRegisterChannels;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutSetGameMode;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutSetOpLevel;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutSetReducedDebug;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutUnregisterChannels;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutWorldSky;
import org.lanternpowered.server.network.vanilla.message.type.connection.MessageInOutPing;
import org.lanternpowered.server.network.vanilla.message.type.connection.MessageOutDisconnect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChangeSign;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChatMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInClientSettings;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutBrand;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutHeldItemChange;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutRegisterChannels;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutUnregisterChannels;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInResourcePackStatus;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutChatMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutOpenBook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutOpenCredits;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutParticleEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerHealthUpdate;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerJoinGame;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerRespawn;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerSpawnPosition;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetGameMode;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetOpLevel;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetReducedDebug;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSoundEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTitle;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWorldSky;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWorldTime;
import org.lanternpowered.server.network.vanilla.message.type.play.internal.MessagePlayInPlayerAction;
import org.lanternpowered.server.network.vanilla.message.type.play.internal.MessagePlayInPlayerVehicleControls;
import org.lanternpowered.server.network.vanilla.message.type.play.internal.MessagePlayOutSpawnParticle;

public final class ProtocolPlay extends ProtocolBase {

    public ProtocolPlay() {
        MessageRegistry inbound = this.inbound();
        MessageRegistry outbound = this.outbound();

        // Register the processors
        inbound.register(MessagePlayInOutChannelPayload.class, new ProcessorPlayInChannelPayload());
        inbound.register(MessagePlayInClientSettings.class, new ProcessorPlayInClientSettings());
        inbound.register(MessagePlayInPlayerAction.class, new ProcessorPlayInPlayerAction());
        inbound.register(MessagePlayInPlayerVehicleControls.class, new ProcessorPlayInPlayerVehicleControls());

        outbound.register(MessagePlayInOutBrand.class, new ProcessorPlayOutBrand());
        outbound.register(MessagePlayInOutChannelPayload.class, new ProcessorPlayOutChannelPayload());
        outbound.register(MessagePlayInOutRegisterChannels.class, new ProcessorPlayOutRegisterChannels());
        outbound.register(MessagePlayInOutUnregisterChannels.class, new ProcessorPlayOutUnregisterChannels());
        outbound.register(MessagePlayOutOpenBook.class, new ProcessorPlayOutOpenBook());
        outbound.register(MessagePlayOutOpenCredits.class, new ProcessorPlayOutOpenCredits());
        outbound.register(MessagePlayOutParticleEffect.class, new ProcessorPlayOutParticleEffect());
        outbound.register(MessagePlayOutPlayerJoinGame.class, new ProcessorPlayOutPlayerJoinGame());
        outbound.register(MessagePlayOutSetGameMode.class, new ProcessorPlayOutSetGameMode());
        outbound.register(MessagePlayOutSetReducedDebug.class, new ProcessorPlayOutSetReducedDebug());
        outbound.register(MessagePlayOutWorldSky.class, new ProcessorPlayOutWorldSky());
        outbound.register(MessagePlayOutSetOpLevel.class, new ProcessorPlayOutSetOpLevel());

        // Register handlers of the missing messages
        inbound.register(MessagePlayInOutRegisterChannels.class, new HandlerPlayInRegisterChannels());
        inbound.register(MessagePlayInOutUnregisterChannels.class, new HandlerPlayInUnregisterChannels());

        // Register the codecs and handlers of the default messages
        inbound.register(0x00, MessageInOutPing.class, CodecInOutPing.class, new HandlerInPing());
        inbound.register(0x01, MessagePlayInChatMessage.class, CodecPlayInChatMessage.class, new HandlerPlayInChatMessage());
        // ...
        inbound.register(0x12, MessagePlayInChangeSign.class, CodecPlayInChangeSign.class, new HandlerPlayInChangeSign());
        // ...
        inbound.register(0x17, MessagePlayInOutChannelPayload.class, CodecPlayInOutCustomPayload.class,
                new HandlerPlayInChannelPayload());
        // 0x18
        inbound.register(0x19, MessagePlayInResourcePackStatus.class, CodecPlayInResourcePackStatus.class,
                new HandlerPlayInResourcePackStatus());

        outbound.register(0x00, MessageInOutPing.class, CodecInOutPing.class);
        outbound.register(0x01, MessagePlayOutPlayerJoinGame.class, CodecPlayOutPlayerJoinGame.class);
        outbound.register(0x02, MessagePlayOutChatMessage.class, CodecPlayOutChatMessage.class);
        outbound.register(0x03, MessagePlayOutWorldTime.class, CodecPlayOutWorldTime.class);
        // 0x04
        outbound.register(0x05, MessagePlayOutPlayerSpawnPosition.class, CodecPlayOutPlayerSpawnPosition.class);
        outbound.register(0x06, MessagePlayOutPlayerHealthUpdate.class, CodecPlayOutPlayerHealthUpdate.class);
        outbound.register(0x07, MessagePlayOutPlayerRespawn.class, CodecPlayOutPlayerRespawn.class);
        // 0x08
        outbound.register(0x09, MessagePlayInOutHeldItemChange.class, CodecPlayInOutHeldItemChange.class);
        outbound.register(0x29, MessagePlayOutSoundEffect.class, CodecPlayOutSoundEffect.class);
        outbound.register(0x2a, MessagePlayOutSpawnParticle.class, CodecPlayOutSpawnParticle.class);
        // ...
        outbound.register(0x3f, MessagePlayInOutChannelPayload.class, CodecPlayInOutCustomPayload.class);
        outbound.register(0x40, MessageOutDisconnect.class, CodecOutDisconnect.class);
        // ...
        outbound.register(0x45, MessagePlayOutTitle.class, CodecPlayOutTitle.class);
        // ...
    }
}
