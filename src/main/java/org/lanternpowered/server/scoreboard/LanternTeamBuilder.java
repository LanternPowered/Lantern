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
package org.lanternpowered.server.scoreboard;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

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

import org.checkerframework.checker.nullness.qual.Nullable;

public class LanternTeamBuilder implements Team.Builder {

    @Nullable private String name;
    @Nullable private Text displayName;
    private Text prefix;
    private Text suffix;
    private TextColor color;
    private boolean allowFriendlyFire;
    private boolean showFriendlyInvisibles;
    private Visibility nameTagVisibility;
    private Visibility deathMessageVisibility;
    private CollisionRule collisionRule;
    private Set<Text> members;

    public LanternTeamBuilder() {
        reset();
    }

    @Override
    public LanternTeamBuilder name(String name) {
        this.name = checkNotNull(name, "name");
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
        final int length = displayName.toPlain().length();
        checkArgument(length <= 32, "Display name is %s characters long! It must be at most 32.", length);
        this.displayName = displayName;
        return this;
    }

    @Override
    public LanternTeamBuilder prefix(Text prefix) {
        checkNotNull(prefix, "prefix");
        this.prefix = prefix;
        return this;
    }

    @Override
    public LanternTeamBuilder suffix(Text suffix) {
        checkNotNull(suffix, "suffix");
        this.suffix = suffix;
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
        return name(value.getName())
                .displayName(value.getDisplayName())
                .color(value.getColor())
                .allowFriendlyFire(value.allowFriendlyFire())
                .canSeeFriendlyInvisibles(value.canSeeFriendlyInvisibles())
                .nameTagVisibility(value.getNameTagVisibility())
                .deathTextVisibility(value.getDeathMessageVisibility())
                .members(value.getMembers());
    }

    @Override
    public LanternTeamBuilder reset() {
        this.name = null;
        this.displayName = null;
        this.prefix = Text.of();
        this.suffix = Text.of();
        this.allowFriendlyFire = false;
        this.showFriendlyInvisibles = false;
        this.color(TextColors.NONE);
        this.nameTagVisibility(Visibilities.ALWAYS);
        this.deathTextVisibility(Visibilities.ALWAYS);
        this.collisionRule(CollisionRules.NEVER);
        this.members = new HashSet<>();
        return this;
    }

    @Override
    public Team build() throws IllegalStateException {
        checkState(this.name != null, "name must be set");

        Text displayName = this.displayName;
        if (displayName == null) {
            displayName = Text.of(this.name);
        }

        final LanternTeam team = new LanternTeam(this.name, this.color, displayName, this.prefix, this.suffix,
                this.allowFriendlyFire, this.showFriendlyInvisibles, this.nameTagVisibility, this.deathMessageVisibility,
                this.collisionRule);
        this.members.forEach(team::addMember);
        return team;
    }
}
