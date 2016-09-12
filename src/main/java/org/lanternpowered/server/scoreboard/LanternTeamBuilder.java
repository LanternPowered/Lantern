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
import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.text.LanternTexts;
import org.spongepowered.api.scoreboard.CollisionRule;
import org.spongepowered.api.scoreboard.CollisionRules;
import org.spongepowered.api.scoreboard.Team;
import org.spongepowered.api.scoreboard.Visibilities;
import org.spongepowered.api.scoreboard.Visibility;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashSet;
import java.util.Set;

public class LanternTeamBuilder implements Team.Builder {

    private String name;
    private Text displayName;
    private TextColor color;
    private Text prefix;
    private Text suffix;
    private boolean allowFriendlyFire;
    private boolean showFriendlyInvisibles;
    private Visibility nameTagVisibility;
    private Visibility deathMessageVisibility;
    private CollisionRule collisionRule;
    private Set<Text> members;

    public LanternTeamBuilder() {
        this.reset();
    }

    @Override
    public LanternTeamBuilder name(String name) {
        this.name = checkNotNull(name, "name");
        if (this.displayName == null) {
            this.displayName = Text.of(this.name);
        }
        return this;
    }

    @Override
    public LanternTeamBuilder color(TextColor color) throws IllegalArgumentException {
        checkNotNull(color, "color");
        if (color == TextColors.RESET) {
            throw new IllegalArgumentException("Color cannot be TextColors.RESET!");
        }
        this.color = color;
        return this;
    }

    @Override
    public LanternTeamBuilder displayName(Text displayName) throws IllegalArgumentException {
        final String legacyDisplayName = LanternTexts.toLegacy(checkNotNull(displayName, "displayName"));
        checkArgument(legacyDisplayName.length() <= 32, "Display name is %s characters long! It must be at most 32.",
                legacyDisplayName.length());
        this.displayName = checkNotNull(displayName, "displayName");
        return this;
    }

    @Override
    public LanternTeamBuilder prefix(Text prefix) {
        final String legacyPrefix = LanternTexts.toLegacy(checkNotNull(prefix, "prefix"));
        checkArgument(legacyPrefix.length() <= 16, "Prefix is %s characters long! It must be at most 16.",
                legacyPrefix.length());
        this.prefix = checkNotNull(prefix, "prefix");
        return this;
    }

    @Override
    public LanternTeamBuilder suffix(Text suffix) {
        final String legacySuffix = LanternTexts.toLegacy(checkNotNull(suffix, "suffix"));
        checkArgument(legacySuffix.length() <= 16, "Suffix is %s characters long! It must be at most 16.",
                legacySuffix.length());
        this.suffix = checkNotNull(suffix, "suffix");
        return this;
    }

    @Override
    public LanternTeamBuilder allowFriendlyFire(boolean enabled) {
        this.allowFriendlyFire = enabled;
        return this;
    }

    @Override
    public LanternTeamBuilder canSeeFriendlyInvisibles(boolean enabled) {
        this.showFriendlyInvisibles = enabled;
        return this;
    }

    @Override
    public LanternTeamBuilder nameTagVisibility(Visibility visibility) {
        this.nameTagVisibility = checkNotNull(visibility, "visibility");
        return this;
    }

    @Override
    public LanternTeamBuilder deathTextVisibility(Visibility visibility) {
        this.deathMessageVisibility = checkNotNull(visibility, "visibility");
        return this;
    }

    @Override
    public LanternTeamBuilder collisionRule(CollisionRule rule) {
        this.collisionRule = checkNotNull(rule, "rule");
        return this;
    }

    @Override
    public LanternTeamBuilder members(Set<Text> members) {
        this.members = new HashSet<>(checkNotNull(members, "members"));
        return this;
    }

    @Override
    public LanternTeamBuilder from(Team value) {
        this.name(value.getName())
                .displayName(value.getDisplayName())
                .prefix(value.getPrefix())
                .color(value.getColor())
                .allowFriendlyFire(value.allowFriendlyFire())
                .canSeeFriendlyInvisibles(value.canSeeFriendlyInvisibles())
                .suffix(value.getSuffix())
                .nameTagVisibility(value.getNameTagVisibility())
                .deathTextVisibility(value.getDeathMessageVisibility())
                .members(value.getMembers());
        return this;
    }

    @Override
    public LanternTeamBuilder reset() {
        this.name = null;
        this.displayName = null;
        this.color = TextColors.NONE;
        this.prefix = Text.of();
        this.suffix = Text.of();
        this.allowFriendlyFire = false;
        this.showFriendlyInvisibles = false;
        this.nameTagVisibility = Visibilities.ALWAYS;
        this.deathMessageVisibility = Visibilities.ALWAYS;
        this.collisionRule = CollisionRules.NEVER;
        this.members = new HashSet<>();
        return this;
    }

    @Override
    public Team build() throws IllegalStateException {
        checkState(this.name != null, "name is not set");
        checkState(this.displayName != null, "displayName is not set");

        final LanternTeam team = new LanternTeam(this.name, this.color, this.displayName, this.prefix, this.suffix,
                this.allowFriendlyFire, this.showFriendlyInvisibles, this.nameTagVisibility, this.deathMessageVisibility,
                this.collisionRule);
        this.members.forEach(team::addMember);
        return team;
    }
}
