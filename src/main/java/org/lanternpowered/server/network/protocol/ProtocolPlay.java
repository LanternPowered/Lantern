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

import org.lanternpowered.server.network.message.CodecRegistration;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.MessageRegistry;
import org.lanternpowered.server.network.vanilla.message.codec.connection.CodecInOutPing;
import org.lanternpowered.server.network.vanilla.message.codec.connection.CodecOutDisconnect;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInChangeSign;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInChatMessage;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInClientSettings;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInOutCustomPayload;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInOutHeldItemChange;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInPlayerAction;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInPlayerVehicleControls;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayInResourcePackStatus;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutChatMessage;
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecPlayOutEntityStatus;
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
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInClientSettings;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInRegisterChannels;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInResourcePackStatus;
import org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInUnregisterChannels;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutOpenCredits;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutParticleEffect;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutSetGameMode;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutWorldSky;
import org.lanternpowered.server.network.vanilla.message.type.connection.MessageInOutPing;
import org.lanternpowered.server.network.vanilla.message.type.connection.MessageOutDisconnect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChangeCommand;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChangeItemName;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChangeOffer;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChangeSign;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChatMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInClientSettings;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInLeaveBed;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutBrand;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutHeldItemChange;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutRegisterChannels;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutUnregisterChannels;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerSneak;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerSprint;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerVehicleJump;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerVehicleMovement;
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

public final class ProtocolPlay extends ProtocolBase {

    public ProtocolPlay() {
        MessageRegistry inbound = this.inbound();
        MessageRegistry outbound = this.outbound();

        // Register the processors
        outbound.bindProcessor(MessagePlayOutOpenCredits.class, new ProcessorPlayOutOpenCredits());
        outbound.bindProcessor(MessagePlayOutParticleEffect.class, new ProcessorPlayOutParticleEffect());
        outbound.bindProcessor(MessagePlayOutSetGameMode.class, new ProcessorPlayOutSetGameMode());
        outbound.bindProcessor(MessagePlayOutWorldSky.class, new ProcessorPlayOutWorldSky());

        // Register the codecs and handlers of the default messages
        inbound.bind(0x00, CodecInOutPing.class, MessageInOutPing.class)
                .bindHandler(new HandlerInPing());
        inbound.bind(0x01, CodecPlayInChatMessage.class, MessagePlayInChatMessage.class)
                .bindHandler(new HandlerPlayInChatMessage());
        // ...
        inbound.bind(0x12, CodecPlayInChangeSign.class, MessagePlayInChangeSign.class)
                .bindHandler(new HandlerPlayInChangeSign());
        // ...
        inbound.bind(0x15, CodecPlayInClientSettings.class, MessagePlayInClientSettings.class)
                .bindHandler(new HandlerPlayInClientSettings());
        // 0x16
        inbound.bind(0x17, CodecPlayInOutCustomPayload.class);
        // ...
        inbound.bind(0x0b, CodecPlayInPlayerAction.class);
        inbound.bind(0x0c, CodecPlayInPlayerVehicleControls.class);

        // Provided by CodecPlayInOutCustomPayload
        inbound.bind(MessagePlayInOutBrand.class); // TODO: Handler
        inbound.bind(MessagePlayInChangeItemName.class); // TODO: Handler
        inbound.bind(MessagePlayInChangeOffer.class); // TODO: Handler
        inbound.bind(MessagePlayInChangeCommand.Block.class); // TODO: Handler
        inbound.bind(MessagePlayInChangeCommand.Entity.class); // TODO: Handler
        inbound.bind(MessagePlayInOutChannelPayload.class).bindHandler(new HandlerPlayInChannelPayload());
        inbound.bind(MessagePlayInOutRegisterChannels.class).bindHandler(new HandlerPlayInRegisterChannels());
        inbound.bind(MessagePlayInOutUnregisterChannels.class).bindHandler(new HandlerPlayInUnregisterChannels());
        // Provided by CodecPlayInPlayerAction
        inbound.bind(MessagePlayInLeaveBed.class);// TODO: Handler
        // Provided by CodecPlayInPlayerVehicleControls or CodecPlayInPlayerAction
        inbound.bind(MessagePlayInPlayerSneak.class); // TODO: Handler
        inbound.bind(MessagePlayInPlayerSprint.class); // TODO: Handler
        inbound.bind(MessagePlayInPlayerVehicleJump.class); // TODO: Handler
        // Provided by CodecPlayInPlayerVehicleControls
        inbound.bind(MessagePlayInPlayerVehicleMovement.class); // TODO: Handler

        // 0x18
        inbound.bind(0x19, CodecPlayInResourcePackStatus.class, MessagePlayInResourcePackStatus.class)
                .bindHandler(new HandlerPlayInResourcePackStatus());

        outbound.bind(0x00, CodecInOutPing.class, MessageInOutPing.class);
        outbound.bind(0x01, CodecPlayOutPlayerJoinGame.class, MessagePlayOutPlayerJoinGame.class);
        outbound.bind(0x02, CodecPlayOutChatMessage.class, MessagePlayOutChatMessage.class);
        outbound.bind(0x03, CodecPlayOutWorldTime.class, MessagePlayOutWorldTime.class);
        // 0x04
        outbound.bind(0x05, CodecPlayOutPlayerSpawnPosition.class, MessagePlayOutPlayerSpawnPosition.class);
        outbound.bind(0x06, CodecPlayOutPlayerHealthUpdate.class, MessagePlayOutPlayerHealthUpdate.class);
        outbound.bind(0x07, CodecPlayOutPlayerRespawn.class, MessagePlayOutPlayerRespawn.class);
        // 0x08
        outbound.bind(0x09, CodecPlayInOutHeldItemChange.class, MessagePlayInOutHeldItemChange.class);
        // ...
        CodecRegistration<Message, CodecPlayOutEntityStatus> codecPlayOutEntityStatus = outbound.bind(0x1a, CodecPlayOutEntityStatus.class);
        codecPlayOutEntityStatus.bind(MessagePlayOutSetOpLevel.class);
        codecPlayOutEntityStatus.bind(MessagePlayOutSetReducedDebug.class);
        // ...
        outbound.bind(0x29, CodecPlayOutSoundEffect.class, MessagePlayOutSoundEffect.class);
        outbound.bind(0x2a, CodecPlayOutSpawnParticle.class, ProcessorPlayOutParticleEffect.MessagePlayOutSpawnParticle.class);
        // ...
        CodecRegistration<Message, CodecPlayInOutCustomPayload> codecPlayInOutCustomPayload = outbound.bind(
                0x3f, CodecPlayInOutCustomPayload.class);
        codecPlayInOutCustomPayload.bind(MessagePlayInOutChannelPayload.class);
        codecPlayInOutCustomPayload.bind(MessagePlayOutOpenBook.class);
        codecPlayInOutCustomPayload.bind(MessagePlayInOutBrand.class);
        outbound.bind(0x40, CodecOutDisconnect.class, MessageOutDisconnect.class);
        // ...
        outbound.bind(0x45, CodecPlayOutTitle.class, MessagePlayOutTitle.class);
        // ...
    }
}
