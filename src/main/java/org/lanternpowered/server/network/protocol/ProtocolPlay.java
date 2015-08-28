package org.lanternpowered.server.network.protocol;

import org.lanternpowered.server.network.message.MessageRegistry;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayInChannelPayload;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayInClientSettings;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayInPlayerAction;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayInPlayerVehicleControls;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutBrand;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutChannelPayload;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutOpenBook;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutOpenCredits;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutPlayerJoinGame;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutRegisterChannels;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutSetGameMode;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutSetReducedDebug;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutUnregisterChannels;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayOutWorldSky;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInClientSettings;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutBrand;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutRegisterChannels;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutUnregisterChannels;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutOpenBook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutOpenCredits;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerJoinGame;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetGameMode;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetReducedDebug;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWorldSky;
import org.lanternpowered.server.network.vanilla.message.type.play.internal.MessagePlayInPlayerAction;
import org.lanternpowered.server.network.vanilla.message.type.play.internal.MessagePlayInPlayerVehicleControls;

public class ProtocolPlay extends ProtocolBase {

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
        outbound.register(MessagePlayOutOpenBook.class, new ProcessorPlayOutOpenBook());
        outbound.register(MessagePlayOutOpenCredits.class, new ProcessorPlayOutOpenCredits());
        outbound.register(MessagePlayOutPlayerJoinGame.class, new ProcessorPlayOutPlayerJoinGame());
        outbound.register(MessagePlayInOutRegisterChannels.class, new ProcessorPlayOutRegisterChannels());
        outbound.register(MessagePlayOutSetGameMode.class, new ProcessorPlayOutSetGameMode());
        outbound.register(MessagePlayOutSetReducedDebug.class, new ProcessorPlayOutSetReducedDebug());
        outbound.register(MessagePlayInOutUnregisterChannels.class, new ProcessorPlayOutUnregisterChannels());
        outbound.register(MessagePlayOutWorldSky.class, new ProcessorPlayOutWorldSky());

        // Register handlers of the missing messages

        // Register the codecs and handlers of the default messages
    }
}
