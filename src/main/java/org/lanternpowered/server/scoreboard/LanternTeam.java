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
package org.lanternpowered.server.scoreboard;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTeams;
import org.lanternpowered.server.text.LanternTexts;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.Team;
import org.spongepowered.api.scoreboard.Visibility;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

public class LanternTeam implements Team {

    private final String name;
    @Nullable private LanternScoreboard scoreboard;
    final Set<Text> members = Sets.newHashSet();
    private TextColor color;
    private Text prefix;
    private String legacyPrefix;
    private Text suffix;
    private String legacySuffix;
    private Text displayName;
    private String legacyDisplayName;
    private boolean allowFriendlyFire;
    private boolean canSeeFriendlyInvisibles;
    private Visibility nameTagVisibility;
    private Visibility deathMessageVisibility;
    private Visibility collisionRule;

    LanternTeam(String name, TextColor color, Text displayName, Text prefix, Text suffix,
                boolean allowFriendlyFire, boolean canSeeFriendlyInvisibles, Visibility nameTagVisibility,
                Visibility deathMessageVisibility, Visibility collisionRule) {
        this.displayName = displayName;
        this.legacyDisplayName = LanternTexts.toLegacy(displayName);
        this.prefix = prefix;
        this.legacyPrefix = LanternTexts.toLegacy(prefix);
        this.suffix = suffix;
        this.legacySuffix = LanternTexts.toLegacy(suffix);
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

    MessagePlayOutTeams.CreateOrUpdate toCreateOrUpdateMessage(boolean create) {
        return create ?
                new MessagePlayOutTeams.Create(this.name, this.legacyDisplayName,
                        this.legacyPrefix, this.legacySuffix, this.nameTagVisibility,
                        this.collisionRule, this.color, this.allowFriendlyFire, this.canSeeFriendlyInvisibles) :
                new MessagePlayOutTeams.Update(this.name, this.legacyDisplayName,
                        this.legacyPrefix, this.legacySuffix, this.nameTagVisibility,
                        this.collisionRule, this.color, this.allowFriendlyFire, this.canSeeFriendlyInvisibles);
    }

    private void sendUpdate() {
        if (this.scoreboard != null) {
            this.scoreboard.sendToPlayers(() -> Collections.singletonList(this.toCreateOrUpdateMessage(false)));
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
        String legacyDisplayName = LanternTexts.toLegacy(checkNotNull(displayName, "displayName"));
        checkArgument(legacyDisplayName.length() <= 32, "Display name is %s characters long! It must be at most 32.",
                legacyDisplayName.length());
        boolean update = !this.legacyDisplayName.equals(legacyDisplayName);
        this.legacyDisplayName = legacyDisplayName;
        this.displayName = displayName;
        if (update) {
            this.sendUpdate();
        }
    }

    @Override
    public TextColor getColor() {
        return this.color;
    }

    @Override
    public void setColor(TextColor color) throws IllegalArgumentException {
        boolean update = !this.color.equals(checkNotNull(color, "color"));
        this.color = color;
        if (update) {
            this.sendUpdate();
        }
    }

    @Override
    public Text getPrefix() {
        return this.prefix;
    }

    @Override
    public void setPrefix(Text prefix) throws IllegalArgumentException {
        String legacyPrefix = LanternTexts.toLegacy(checkNotNull(prefix, "prefix"));
        checkArgument(legacyPrefix.length() <= 16, "Prefix is %s characters long! It must be at most 16.",
                legacyPrefix.length());
        boolean update = !this.legacyPrefix.equals(legacyPrefix);
        this.legacyPrefix = legacyPrefix;
        this.prefix = prefix;
        if (update) {
            this.sendUpdate();
        }
    }

    @Override
    public Text getSuffix() {
        return this.suffix;
    }

    @Override
    public void setSuffix(Text suffix) throws IllegalArgumentException {
        String legacySuffix = LanternTexts.toLegacy(checkNotNull(suffix, "suffix"));
        checkArgument(legacySuffix.length() <= 16, "Suffix is %s characters long! It must be at most 16.",
                legacySuffix.length());
        boolean update = !this.legacySuffix.equals(legacySuffix);
        this.legacySuffix = legacySuffix;
        this.suffix = suffix;
        if (update) {
            this.sendUpdate();
        }
    }

    @Override
    public boolean allowFriendlyFire() {
        return this.allowFriendlyFire;
    }

    @Override
    public void setAllowFriendlyFire(boolean enabled) {
        boolean update = enabled != this.allowFriendlyFire;
        this.allowFriendlyFire = enabled;
        if (update) {
            this.sendUpdate();
        }
    }

    @Override
    public boolean canSeeFriendlyInvisibles() {
        return this.canSeeFriendlyInvisibles;
    }

    @Override
    public void setCanSeeFriendlyInvisibles(boolean enabled) {
        boolean update = enabled != this.canSeeFriendlyInvisibles;
        this.canSeeFriendlyInvisibles = enabled;
        if (update) {
            this.sendUpdate();
        }
    }

    @Override
    public Visibility getNameTagVisibility() {
        return this.nameTagVisibility;
    }

    @Override
    public void setNameTagVisibility(Visibility visibility) {
        boolean update = !checkNotNull(visibility, "visibility").equals(this.nameTagVisibility);
        this.nameTagVisibility = visibility;
        if (update) {
            this.sendUpdate();
        }
    }

    @Override
    public Visibility getDeathMessageVisibility() {
        return this.deathMessageVisibility;
    }

    @Override
    public void setDeathMessageVisibility(Visibility visibility) {
        boolean update = !checkNotNull(visibility, "visibility").equals(this.deathMessageVisibility);
        this.deathMessageVisibility = visibility;
        if (update) {
            this.sendUpdate();
        }
    }

    @Override
    public Set<Text> getMembers() {
        return ImmutableSet.copyOf(this.members);
    }

    @Override
    public void addMember(Text member) {
        if (this.members.add(checkNotNull(member, "member")) && this.scoreboard != null) {
            this.scoreboard.sendToPlayers(() -> Collections.singletonList(
                    new MessagePlayOutTeams.AddPlayers(this.name, Collections.singletonList(LanternTexts.toLegacy(member)))));
        }
    }

    @Override
    public boolean removeMember(Text member) {
        if (this.members.remove(checkNotNull(member, "member"))) {
            if (this.scoreboard != null) {
                this.scoreboard.sendToPlayers(() -> Collections.singletonList(
                        new MessagePlayOutTeams.RemovePlayers(this.name, Collections.singletonList(LanternTexts.toLegacy(member)))));
            }
            return true;
        }
        return false;
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
