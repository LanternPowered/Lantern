/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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
package org.lanternpowered.server.scoreboard;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTeams;
import org.spongepowered.api.scoreboard.CollisionRule;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.Team;
import org.spongepowered.api.scoreboard.Visibility;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

public class LanternTeam implements Team {

    private final String name;
    @Nullable private LanternScoreboard scoreboard;
    private TextColor color;
    final Set<Text> members = new HashSet<>();
    private Text prefix;
    private Text suffix;
    private Text displayName;
    private boolean allowFriendlyFire;
    private boolean canSeeFriendlyInvisibles;
    private Visibility nameTagVisibility;
    private Visibility deathMessageVisibility;
    private CollisionRule collisionRule;

    LanternTeam(String name, TextColor color, Text displayName, Text prefix, Text suffix,
                boolean allowFriendlyFire, boolean canSeeFriendlyInvisibles, Visibility nameTagVisibility,
                Visibility deathMessageVisibility, CollisionRule collisionRule) {
        this.displayName = displayName;
        this.prefix = prefix;
        this.suffix = suffix;
        this.name = name;
        this.color = color;
        this.allowFriendlyFire = allowFriendlyFire;
        this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
        this.nameTagVisibility = nameTagVisibility;
        this.deathMessageVisibility = deathMessageVisibility;
        this.collisionRule = collisionRule;
    }

    void setScoreboard(@Nullable LanternScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    MessagePlayOutTeams.CreateOrUpdate toCreateMessage() {
        return new MessagePlayOutTeams.Create(this.name, this.displayName,
                this.prefix, this.suffix, this.nameTagVisibility,
                this.collisionRule, this.color, this.allowFriendlyFire, this.canSeeFriendlyInvisibles,
                new ArrayList<>(this.members));
    }

    MessagePlayOutTeams.CreateOrUpdate toUpdateMessage() {
        return new MessagePlayOutTeams.Update(this.name, this.displayName,
                this.prefix, this.suffix, this.nameTagVisibility,
                this.collisionRule, this.color, this.allowFriendlyFire, this.canSeeFriendlyInvisibles);
    }

    private void sendUpdate() {
        if (this.scoreboard != null) {
            this.scoreboard.sendToPlayers(() -> Collections.singletonList(toUpdateMessage()));
        }
    }

    @Override
    public CollisionRule getCollisionRule() {
        return this.collisionRule;
    }

    @Override
    public void setCollisionRule(CollisionRule rule) {
        final boolean update = !rule.equals(this.collisionRule);
        this.collisionRule = rule;
        if (update) {
            sendUpdate();
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Text getDisplayName() {
        return this.displayName;
    }

    @Override
    public void setDisplayName(Text displayName) throws IllegalArgumentException {
        final int length = displayName.toPlain().length();
        checkArgument(length <= 32, "Display name is %s characters long! It must be at most 32.", length);
        final boolean update = !this.displayName.equals(displayName);
        this.displayName = displayName;
        if (update) {
            sendUpdate();
        }
    }

    @Override
    public TextColor getColor() {
        return this.color;
    }

    @Override
    public void setColor(TextColor color) throws IllegalArgumentException {
        final boolean update = !color.equals(this.color);
        this.color = color;
        if (update) {
            sendUpdate();
        }
    }

    @Override
    public Text getPrefix() {
        return this.prefix;
    }

    @Override
    public void setPrefix(Text prefix) throws IllegalArgumentException {
        final int length = prefix.toPlain().length();
        checkArgument(length <= 16, "Prefix is %s characters long! It must be at most 16.", length);
        final boolean update = !this.prefix.equals(prefix);
        this.prefix = prefix;
        if (update) {
            sendUpdate();
        }
    }

    @Override
    public Text getSuffix() {
        return this.suffix;
    }

    @Override
    public void setSuffix(Text suffix) throws IllegalArgumentException {
        final int length = suffix.toPlain().length();
        checkArgument(length <= 16, "Suffix is %s characters long! It must be at most 16.", length);
        final boolean update = !this.suffix.equals(suffix);
        this.suffix = prefix;
        if (update) {
            sendUpdate();
        }
    }

    @Override
    public boolean allowFriendlyFire() {
        return this.allowFriendlyFire;
    }

    @Override
    public void setAllowFriendlyFire(boolean enabled) {
        final boolean update = enabled != this.allowFriendlyFire;
        this.allowFriendlyFire = enabled;
        if (update) {
            sendUpdate();
        }
    }

    @Override
    public boolean canSeeFriendlyInvisibles() {
        return this.canSeeFriendlyInvisibles;
    }

    @Override
    public void setCanSeeFriendlyInvisibles(boolean enabled) {
        final boolean update = enabled != this.canSeeFriendlyInvisibles;
        this.canSeeFriendlyInvisibles = enabled;
        if (update) {
            sendUpdate();
        }
    }

    @Override
    public Visibility getNameTagVisibility() {
        return this.nameTagVisibility;
    }

    @Override
    public void setNameTagVisibility(Visibility visibility) {
        final boolean update = !visibility.equals(this.nameTagVisibility);
        this.nameTagVisibility = visibility;
        if (update) {
            sendUpdate();
        }
    }

    @Override
    public Visibility getDeathMessageVisibility() {
        return this.deathMessageVisibility;
    }

    @Override
    public void setDeathMessageVisibility(Visibility visibility) {
        final boolean update = !visibility.equals(this.deathMessageVisibility);
        this.deathMessageVisibility = visibility;
        if (update) {
            sendUpdate();
        }
    }

    @Override
    public Set<Text> getMembers() {
        return ImmutableSet.copyOf(this.members);
    }

    @Override
    public void addMember(Text member) {
        checkNotNull(member, "member");
        if (this.members.add(member) && this.scoreboard != null) {
            this.scoreboard.sendToPlayers(() -> Collections.singletonList(
                    new MessagePlayOutTeams.AddMembers(this.name, Collections.singletonList(member))));
        }
    }

    @Override
    public boolean removeMember(Text member) {
        checkNotNull(member, "member");
        if (this.members.remove(member)) {
            if (this.scoreboard != null) {
                this.scoreboard.sendToPlayers(() -> Collections.singletonList(
                        new MessagePlayOutTeams.RemoveMembers(this.name, Collections.singletonList(member))));
            }
            return true;
        }
        return false;
    }

    public List<Text> addMembers(Collection<Text> members) {
        checkNotNull(members, "members");
        final List<Text> failedMembers = new ArrayList<>();
        final List<Text> addedMembers = new ArrayList<>();
        for (Text member : members) {
            if (this.members.add(member)) {
                addedMembers.add(member);
            } else {
                failedMembers.add(member);
            }
        }
        if (this.scoreboard != null) {
            this.scoreboard.sendToPlayers(() -> Collections.singletonList(
                    new MessagePlayOutTeams.AddMembers(this.name, addedMembers)));
        }
        return failedMembers;
    }

    public List<Text> removeMembers(Collection<Text> members) {
        checkNotNull(members, "members");
        final List<Text> failedMembers = new ArrayList<>();
        if (this.members.isEmpty()) {
            return failedMembers;
        }
        final List<Text> removedMembers = new ArrayList<>();
        for (Text member : members) {
            if (this.members.remove(member)) {
                removedMembers.add(member);
            } else {
                failedMembers.add(member);
            }
        }
        if (this.scoreboard != null) {
            this.scoreboard.sendToPlayers(() -> Collections.singletonList(
                    new MessagePlayOutTeams.RemoveMembers(this.name, removedMembers)));
        }
        return failedMembers;
    }

    @Override
    public Optional<Scoreboard> getScoreboard() {
        return Optional.ofNullable(this.scoreboard);
    }

    @Override
    public boolean unregister() {
        if (this.scoreboard == null) {
            return false;
        }
        this.scoreboard.removeTeam(this);
        this.scoreboard.sendToPlayers(() -> Collections.singletonList(new MessagePlayOutTeams.Remove(this.name)));
        this.scoreboard = null;
        return true;
    }
}
