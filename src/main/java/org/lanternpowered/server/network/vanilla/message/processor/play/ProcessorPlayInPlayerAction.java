package org.lanternpowered.server.network.vanilla.message.processor.play;

import io.netty.handler.codec.CodecException;
import io.netty.util.AttributeKey;

import java.util.List;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInLeaveBed;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerVehicleJump;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerSneak;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerSprint;
import org.lanternpowered.server.network.vanilla.message.type.play.internal.MessagePlayInPlayerAction;

public final class ProcessorPlayInPlayerAction implements Processor<MessagePlayInPlayerAction> {

    public static final AttributeKey<Boolean> CANCEL_NEXT_JUMP_MESSAGE = AttributeKey.valueOf("cancel-next-jump-message");

    @Override
    public void process(CodecContext context, MessagePlayInPlayerAction message, List<Message> output) throws CodecException {
        int action = message.getAction();

        // Sneaking states
        if (action == 0 || action == 1) {
            output.add(new MessagePlayInPlayerSneak(action == 0));
        // Sprinting states
        } else if (action == 3 || action == 4) {
            output.add(new MessagePlayInPlayerSprint(action == 3));
        // Leave bed button is pressed
        } else if (action == 2) {
            output.add(new MessagePlayInLeaveBed());
        // Horse jump power action
        } else if (action == 5) {
            output.add(new MessagePlayInPlayerVehicleJump(false, message.getValue() / 100f));
            // Make sure that the vehicle movement message doesn't add the jump message as well
            context.channel().attr(CANCEL_NEXT_JUMP_MESSAGE).set(true);
        }
    }

}
