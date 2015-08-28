package org.lanternpowered.server.network.vanilla.message.processor.play;

import io.netty.handler.codec.CodecException;
import io.netty.util.AttributeKey;

import java.util.List;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerVehicleJump;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerSneak;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerVehicleMovement;
import org.lanternpowered.server.network.vanilla.message.type.play.internal.MessagePlayInPlayerVehicleControls;

public final class ProcessorPlayInPlayerVehicleControls implements Processor<MessagePlayInPlayerVehicleControls> {

    private final static AttributeKey<Boolean> SNEAKING = AttributeKey.valueOf("last-sneaking-state");
    private final static AttributeKey<Boolean> JUMPING = AttributeKey.valueOf("last-jumping-state");

    @Override
    public void process(CodecContext context, MessagePlayInPlayerVehicleControls message, List<Message> output) throws CodecException {
        boolean sneaking = message.isSneaking();
        boolean flag0 = context.channel().attr(SNEAKING).getAndSet(sneaking);
        if (flag0 != sneaking) {
            output.add(new MessagePlayInPlayerSneak(sneaking));
        }
        boolean jumping = message.isJumping();
        boolean flag1 = context.channel().attr(JUMPING).getAndSet(jumping);
        boolean flag2 = context.channel().attr(ProcessorPlayInPlayerAction.CANCEL_NEXT_JUMP_MESSAGE).getAndSet(false);
        if (flag1 != jumping && !flag2) {
            output.add(new MessagePlayInPlayerVehicleJump(jumping, 0f));
        }
        float sideways = message.getSideways();
        float forwards = message.getForwards();
        // The mc client already applies the sneak speed, but we want to choose it
        if (sneaking) {
            sideways /= 0.3f;
            forwards /= 0.3f;
        }
        output.add(new MessagePlayInPlayerVehicleMovement(forwards, sideways));
    }

}
