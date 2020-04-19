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
import org.spongepowered.api.scoreboard.CollisionRule;
import org.spongepowered.api.scoreboard.Visibility;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;

import java.util.List;

public abstract class MessagePlayOutTeams implements Message {

    private final String teamName;

    private MessagePlayOutTeams(String teamName) {
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

    public static final class Create extends CreateOrUpdate implements Members {

        private final List<Text> members;

        public Create(String teamName, Text displayName, Text prefix, Text suffix, Visibility nameTagVisibility,
                CollisionRule collisionRule,  TextColor color, boolean friendlyFire, boolean seeFriendlyInvisibles,
                List<Text> members) {
            super(teamName, displayName, prefix, suffix, nameTagVisibility, collisionRule, color, friendlyFire, seeFriendlyInvisibles);
            this.members = members;
        }

        @Override
        public List<Text> getMembers() {
            return this.members;
        }
    }

    public static final class Update extends CreateOrUpdate {

        public Update(String teamName, Text displayName, Text prefix, Text suffix, Visibility nameTagVisibility,
                CollisionRule collisionRule,  TextColor color, boolean friendlyFire, boolean seeFriendlyInvisibles) {
            super(teamName, displayName, prefix, suffix, nameTagVisibility, collisionRule, color, friendlyFire, seeFriendlyInvisibles);
        }
    }

    public static abstract class CreateOrUpdate extends MessagePlayOutTeams {

        private final Text displayName;
        private final Text prefix;
        private final Text suffix;
        private final Visibility nameTagVisibility;
        private final CollisionRule collisionRule;
        private final TextColor color;
        private final boolean friendlyFire;
        private final boolean seeFriendlyInvisibles;

        CreateOrUpdate(String teamName, Text displayName, Text prefix, Text suffix, Visibility nameTagVisibility,
                CollisionRule collisionRule, TextColor color, boolean friendlyFire, boolean seeFriendlyInvisibles) {
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

        public Text getDisplayName() {
            return this.displayName;
        }

        public Text getPrefix() {
            return this.prefix;
        }

        public Text getSuffix() {
            return this.suffix;
        }

        public Visibility getNameTagVisibility() {
            return this.nameTagVisibility;
        }

        public CollisionRule getCollisionRule() {
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

    public static final class AddMembers extends AddOrRemovePlayers {

        public AddMembers(String teamName, List<Text> members) {
            super(teamName, members);
        }
    }

    public static final class RemoveMembers extends AddOrRemovePlayers {

        public RemoveMembers(String teamName, List<Text> members) {
            super(teamName, members);
        }
    }

    public static abstract class AddOrRemovePlayers extends MessagePlayOutTeams implements Members {

        private final List<Text> members;

        AddOrRemovePlayers(String teamName, List<Text> members) {
            super(teamName);
            this.members = members;
        }

        @Override
        public List<Text> getMembers() {
            return this.members;
        }
    }

    public interface Members {

        List<Text> getMembers();
    }
}
