package org.lanternpowered.server.network.vanilla.message.processor.play;

import java.util.List;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWorldSky;
import org.lanternpowered.server.network.vanilla.message.type.play.internal.MessagePlayOutChangeGameState;

public final class ProcessorPlayOutWorldSky implements Processor<MessagePlayOutWorldSky> {

    @Override
    public void process(CodecContext context, MessagePlayOutWorldSky message, List<Message> output) {
        float rain = message.getRainStrength();
        float darkness = message.getDarkness();

        // Rain strength may not be greater then 1.5
        // TODO: Check what limit may cause issues
        rain = Math.min(1.5f, rain);

        // The maximum darkness value before strange things start to happen
        // Night vision changes the max from 6.0 to 4.0, we won't
        // check that for now so we will play safe.
        float f1 = 4f;

        // The minimum value before the rain stops
        float f2 = 0.01f;

        // The rain value may not be zero, otherwise will it cause math errors.
        if (rain < f2 && darkness > 0f) {
            rain = f2;
        }

        if (darkness > 0f) {
            darkness = Math.min(darkness / rain, f1 / rain);
        }

        output.add(new MessagePlayOutChangeGameState(7, rain));
        output.add(new MessagePlayOutChangeGameState(8, darkness));
    }

}
