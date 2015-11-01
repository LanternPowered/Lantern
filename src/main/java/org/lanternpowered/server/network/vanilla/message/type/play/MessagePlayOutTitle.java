/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.text.Text;

public abstract class MessagePlayOutTitle implements Message {

    public static final class SetTitle extends MessagePlayOutTitle {

        private final Text title;

        public SetTitle(Text title) {
            this.title = title;
        }

        public Text getTitle() {
            return this.title;
        }
    }

    public static final class SetSubtitle extends MessagePlayOutTitle {

        private final Text title;

        public SetSubtitle(Text title) {
            this.title = title;
        }

        public Text getTitle() {
            return this.title;
        }
    }

    public static final class SetTimes extends MessagePlayOutTitle {

        private final int fadeIn;
        private final int fadeOut;
        private final int stay;

        public SetTimes(int fadeIn, int stay, int fadeOut) {
            this.fadeOut = fadeOut;
            this.fadeIn = fadeIn;
            this.stay = stay;
        }

        public int getFadeIn() {
            return this.fadeIn;
        }

        public int getStay() {
            return this.stay;
        }

        public int getFadeOut() {
            return this.fadeOut;
        }
    }

    public static final class Clear extends MessagePlayOutTitle {
    }

    public static final class Reset extends MessagePlayOutTitle {
    }
}
