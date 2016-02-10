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
package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.text.format.TextColor;

import java.util.List;

public abstract class MessagePlayOutTeams implements Message {

    private final String teamName;

    MessagePlayOutTeams(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return this.teamName;
    }

    public static final class Remove extends MessagePlayOutTeams {

        public Remove(String teamName) {
            super(teamName);
        }
    }

    public static final class Create extends CreateOrUpdate {

        public Create(String teamName, String displayName, String prefix, String suffix, Visibility nameTagVisibility,
                Visibility collisionRule,  TextColor color, boolean friendlyFire, boolean seeFriendlyInvisibles) {
            super(teamName, displayName, prefix, suffix, nameTagVisibility, collisionRule, color, friendlyFire, seeFriendlyInvisibles);
        }
    }

    public static final class Update extends CreateOrUpdate {

        public Update(String teamName, String displayName, String prefix, String suffix, Visibility nameTagVisibility,
                Visibility collisionRule,  TextColor color, boolean friendlyFire, boolean seeFriendlyInvisibles) {
            super(teamName, displayName, prefix, suffix, nameTagVisibility, collisionRule, color, friendlyFire, seeFriendlyInvisibles);
        }
    }

    public static abstract class CreateOrUpdate extends MessagePlayOutTeams {

        private final String displayName;
        private final String prefix;
        private final String suffix;
        private final Visibility nameTagVisibility;
        private final Visibility collisionRule;
        private final TextColor color;
        private final boolean friendlyFire;
        private final boolean seeFriendlyInvisibles;

        CreateOrUpdate(String teamName, String displayName, String prefix, String suffix, Visibility nameTagVisibility,
                Visibility collisionRule, TextColor color, boolean friendlyFire, boolean seeFriendlyInvisibles) {
            super(teamName);
            this.displayName = displayName;
            this.prefix = prefix;
            this.suffix = suffix;
            this.nameTagVisibility = nameTagVisibility;
            this.collisionRule = collisionRule;
            this.color = color;
            this.friendlyFire = friendlyFire;
            this.seeFriendlyInvisibles = seeFriendlyInvisibles;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public String getPrefix() {
            return this.prefix;
        }

        public String getSuffix() {
            return this.suffix;
        }

        public Visibility getNameTagVisibility() {
            return this.nameTagVisibility;
        }

        public Visibility getCollisionRule() {
            return this.collisionRule;
        }

        public TextColor getColor() {
            return this.color;
        }

        public boolean getFriendlyFire() {
            return this.friendlyFire;
        }

        public boolean getSeeFriendlyInvisibles() {
            return this.seeFriendlyInvisibles;
        }
    }

    public static abstract class AddPlayers extends AddOrRemovePlayers {

        public AddPlayers(String teamName, List<String> players) {
            super(teamName, players);
        }
    }

    public static abstract class RemovePlayers extends AddOrRemovePlayers {

        public RemovePlayers(String teamName, List<String> players) {
            super(teamName, players);
        }
    }

    public static abstract class AddOrRemovePlayers extends MessagePlayOutTeams {

        private final List<String> players;

        AddOrRemovePlayers(String teamName, List<String> players) {
            super(teamName);
            this.players = players;
        }

        public List<String> getPlayers() {
            return this.players;
        }
    }

    public enum Visibility {
        ALWAYS,
        NEVER,
        HIDE_FOR_OTHER_TEAMS,
        HIDE_FOR_OWN_TEAM,
    }
}
