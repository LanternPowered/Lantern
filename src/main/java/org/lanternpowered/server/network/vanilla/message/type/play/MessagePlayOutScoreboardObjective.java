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
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayMode;
import org.spongepowered.api.text.Text;

public abstract class MessagePlayOutScoreboardObjective implements Message {

    private final String objectiveName;

    MessagePlayOutScoreboardObjective(String objectiveName) {
        this.objectiveName = objectiveName;
    }

    public String getObjectiveName() {
        return this.objectiveName;
    }

    public static final class Remove extends MessagePlayOutScoreboardObjective {

        public Remove(String objectiveName) {
            super(objectiveName);
        }
    }

    public static final class Create extends CreateOrUpdate {

        public Create(String objectiveName, Text displayName, ObjectiveDisplayMode displayMode) {
            super(objectiveName, displayName, displayMode);
        }
    }

    public static final class Update extends CreateOrUpdate {

        public Update(String objectiveName, Text displayName, ObjectiveDisplayMode type) {
            super(objectiveName, displayName, type);
        }
    }

    public static abstract class CreateOrUpdate extends MessagePlayOutScoreboardObjective {

        private final Text displayName;
        private final ObjectiveDisplayMode displayMode;

        CreateOrUpdate(String objectiveName, Text displayName, ObjectiveDisplayMode displayMode) {
            super(objectiveName);
            this.displayName = displayName;
            this.displayMode = displayMode;
        }

        public Text getDisplayName() {
            return this.displayName;
        }

        public ObjectiveDisplayMode getDisplayMode() {
            return this.displayMode;
        }
    }
}
