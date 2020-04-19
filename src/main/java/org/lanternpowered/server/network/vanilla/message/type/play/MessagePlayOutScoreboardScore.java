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

public abstract class MessagePlayOutScoreboardScore implements Message {

    private final String objectiveName;
    private final Text scoreName;

    public MessagePlayOutScoreboardScore(String objectiveName, Text scoreName) {
        this.objectiveName = objectiveName;
        this.scoreName = scoreName;
    }

    public String getObjectiveName() {
        return this.objectiveName;
    }

    public Text getScoreName() {
        return this.scoreName;
    }

    public static final class CreateOrUpdate extends MessagePlayOutScoreboardScore {

        private final int value;

        public CreateOrUpdate(String objectiveName, Text scoreName, int value) {
            super(objectiveName, scoreName);
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public static final class Remove extends MessagePlayOutScoreboardScore {

        public Remove(String objectiveName, Text scoreName) {
            super(objectiveName, scoreName);
        }
    }
}
