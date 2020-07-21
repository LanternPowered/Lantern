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
package org.lanternpowered.server.network.vanilla.packet.type.play;

import org.lanternpowered.server.network.message.Packet;
import org.spongepowered.api.text.Text;

public abstract class PacketPlayOutScoreboardScore implements Packet {

    private final String objectiveName;
    private final Text scoreName;

    public PacketPlayOutScoreboardScore(String objectiveName, Text scoreName) {
        this.objectiveName = objectiveName;
        this.scoreName = scoreName;
    }

    public String getObjectiveName() {
        return this.objectiveName;
    }

    public Text getScoreName() {
        return this.scoreName;
    }

    public static final class CreateOrUpdate extends PacketPlayOutScoreboardScore {

        private final int value;

        public CreateOrUpdate(String objectiveName, Text scoreName, int value) {
            super(objectiveName, scoreName);
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public static final class Remove extends PacketPlayOutScoreboardScore {

        public Remove(String objectiveName, Text scoreName) {
            super(objectiveName, scoreName);
        }
    }
}
