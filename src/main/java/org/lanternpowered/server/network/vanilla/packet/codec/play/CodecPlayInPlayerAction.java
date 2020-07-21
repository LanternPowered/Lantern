/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.network.vanilla.packet.codec.play;

import io.netty.handler.codec.CodecException;
import io.netty.util.AttributeKey;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.packet.Packet;
import org.lanternpowered.server.network.packet.UnknownPacket;
import org.lanternpowered.server.network.packet.codec.Codec;
import org.lanternpowered.server.network.packet.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInLeaveBed;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInPlayerSneak;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInPlayerSprint;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInPlayerVehicleJump;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInRequestHorseInventory;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInStartElytraFlying;

public final class CodecPlayInPlayerAction implements Codec<Packet> {

    static final AttributeKey<Boolean> CANCEL_NEXT_JUMP_MESSAGE = AttributeKey.valueOf("cancel-next-jump-message");

    @Override
    public Packet decode(CodecContext context, ByteBuffer buf) throws CodecException {
        // Normally should this be the entity id, but only the
        // client player will send this, so it won't be used
        buf.readVarInt();
        final int action = buf.readVarInt();
        final int value = buf.readVarInt();
        // Sneaking states
        if (action == 0 || action == 1) {
            return new PacketPlayInPlayerSneak(action == 0);
        // Sprinting states
        } else if (action == 3 || action == 4) {
            return new PacketPlayInPlayerSprint(action == 3);
        // Leave bed button is pressed
        } else if (action == 2) {
            return new PacketPlayInLeaveBed();
        // Horse jump start
        } else if (action == 5) {
            return UnknownPacket.INSTANCE;
        // Horse jump stop
        } else if (action == 6) {
            // Make sure that the vehicle movement message doesn't add the jump message as well
            context.getChannel().attr(CANCEL_NEXT_JUMP_MESSAGE).set(true);
            return new PacketPlayInPlayerVehicleJump(false, ((float) value) / 100f);
        } else if (action == 7) {
            return new PacketPlayInRequestHorseInventory();
        } else if (action == 8) {
            return new PacketPlayInStartElytraFlying();
        }
        throw new CodecException("Unknown action type: " + action);
    }
}
