/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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

        public Create(String objectiveName, String displayName, Type type) {
            super(objectiveName, displayName, type);
        }
    }

    public static final class Update extends CreateOrUpdate {

        public Update(String objectiveName, String displayName, Type type) {
            super(objectiveName, displayName, type);
        }
    }

    public static abstract class CreateOrUpdate extends MessagePlayOutScoreboardObjective {

        private final String displayName;
        private final Type type;

        CreateOrUpdate(String objectiveName, String displayName, Type type) {
            super(objectiveName);
            this.displayName = displayName;
            this.type = type;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public Type getType() {
            return this.type;
        }
    }

    public enum Type {
        INTEGER,
        HEARTS,
    }
}
