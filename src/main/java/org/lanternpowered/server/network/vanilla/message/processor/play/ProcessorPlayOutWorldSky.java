/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
package org.lanternpowered.server.network.vanilla.message.processor.play;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWorldSky;
import org.lanternpowered.server.network.vanilla.message.type.play.internal.MessagePlayOutChangeGameState;

import java.util.List;

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
