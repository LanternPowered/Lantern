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
package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.handler.codec.CodecException;
import io.netty.util.AttributeKey;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.UnknownMessage;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInLeaveBed;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerSneak;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerSprint;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerVehicleJump;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInRequestHorseInventory;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInStartElytraFlying;

public final class CodecPlayInPlayerAction implements Codec<Message> {

    static final AttributeKey<Boolean> CANCEL_NEXT_JUMP_MESSAGE = AttributeKey.valueOf("cancel-next-jump-message");

    @Override
    public Message decode(CodecContext context, ByteBuffer buf) throws CodecException {
        // Normally should this be the entity id, but only the
        // client player will send this, so it won't be used
        buf.readVarInt();
        final int action = buf.readVarInt();
        final int value = buf.readVarInt();
        // Sneaking states
        if (action == 0 || action == 1) {
            return new MessagePlayInPlayerSneak(action == 0);
        // Sprinting states
        } else if (action == 3 || action == 4) {
            return new MessagePlayInPlayerSprint(action == 3);
        // Leave bed button is pressed
        } else if (action == 2) {
            return new MessagePlayInLeaveBed();
        // Horse jump start
        } else if (action == 5) {
            return UnknownMessage.INSTANCE;
        // Horse jump stop
        } else if (action == 6) {
            // Make sure that the vehicle movement message doesn't add the jump message as well
            context.getChannel().attr(CANCEL_NEXT_JUMP_MESSAGE).set(true);
            return new MessagePlayInPlayerVehicleJump(false, ((float) value) / 100f);
        } else if (action == 7) {
            return new MessagePlayInRequestHorseInventory();
        } else if (action == 8) {
            return new MessagePlayInStartElytraFlying();
        }
        throw new CodecException("Unknown action type: " + action);
    }
}
