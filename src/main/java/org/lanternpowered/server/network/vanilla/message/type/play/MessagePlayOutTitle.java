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

    public static final class SetActionbarTitle extends MessagePlayOutTitle {

        private final Text title;

        public SetActionbarTitle(Text title) {
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
