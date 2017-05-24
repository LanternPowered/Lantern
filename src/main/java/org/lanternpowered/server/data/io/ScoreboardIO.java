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
package org.lanternpowered.server.data.io;

import org.lanternpowered.server.data.persistence.nbt.NbtStreamUtils;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.type.scoreboard.DisplaySlotRegistryModule;
import org.lanternpowered.server.scoreboard.LanternDisplaySlot;
import org.lanternpowered.server.scoreboard.LanternObjective;
import org.lanternpowered.server.scoreboard.LanternScore;
import org.lanternpowered.server.scoreboard.LanternScoreboard;
import org.lanternpowered.server.scoreboard.LanternTeam;
import org.lanternpowered.server.text.LanternTexts;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.scoreboard.CollisionRule;
import org.spongepowered.api.scoreboard.CollisionRules;
import org.spongepowered.api.scoreboard.Score;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.Team;
import org.spongepowered.api.scoreboard.Visibilities;
import org.spongepowered.api.scoreboard.Visibility;
import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.critieria.Criterion;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayMode;
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayModes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public class ScoreboardIO {

    private final static String SCOREBOARD_DATA = "scoreboard.dat";

    private final static DataQuery DATA = DataQuery.of("data");
    private final static DataQuery OBJECTIVES = DataQuery.of("Objectives");
    private final static DataQuery OBJECTIVE = DataQuery.of("Objective");
    private final static DataQuery EXTRA_OBJECTIVES = DataQuery.of("EObjectives"); // Lantern
    private final static DataQuery NAME = DataQuery.of("Name");
    private final static DataQuery DISPLAY_NAME = DataQuery.of("DisplayName");
    private final static DataQuery CRITERION_NAME = DataQuery.of("CriteriaName");
    private final static DataQuery DISPLAY_MODE = DataQuery.of("RenderType");
    private final static DataQuery SCORES = DataQuery.of("PlayerScores");
    private final static DataQuery SCORE = DataQuery.of("Score");
    private final static DataQuery INVALID = DataQuery.of("Invalid"); // Lantern
    private final static DataQuery LOCKED = DataQuery.of("Locked");
    private final static DataQuery ALLOW_FRIENDLY_FIRE = DataQuery.of("AllowFriendlyFire");
    private final static DataQuery CAN_SEE_FRIENDLY_INVISIBLES = DataQuery.of("SeeFriendlyInvisibles");
    private final static DataQuery NAME_TAG_VISIBILITY = DataQuery.of("NameTagVisibility");
    private final static DataQuery DEATH_MESSAGE_VISIBILITY = DataQuery.of("DeathMessageVisibility");
    private final static DataQuery COLLISION_RULE = DataQuery.of("CollisionRule");
    private final static DataQuery PREFIX = DataQuery.of("Prefix");
    private final static DataQuery SUFFIX = DataQuery.of("Suffix");
    private final static DataQuery TEAM_COLOR = DataQuery.of("TeamColor");
    private final static DataQuery MEMBERS = DataQuery.of("Players");
    private final static DataQuery TEAMS = DataQuery.of("Teams");
    private final static DataQuery DISPLAY_SLOTS = DataQuery.of("DisplaySlots");

    private final static Pattern DISPLAY_SLOT_PATTERN = Pattern.compile("^slot_([0-9]+)$");

    public static Scoreboard read(Path worldFolder) throws IOException {
        DataView dataView = IOHelper.<DataView>read(worldFolder.resolve(SCOREBOARD_DATA), file -> {
            try {
                return NbtStreamUtils.read(Files.newInputStream(file), true);
            } catch (IOException e) {
                throw new IOException("Unable to access " + file.getFileName() + "!", e);
            }
        }).orElse(null);
        final Scoreboard.Builder scoreboardBuilder = Scoreboard.builder();
        if (dataView == null) {
            return scoreboardBuilder.build();
        }
        final Map<String, Objective> objectives = new HashMap<>();
        dataView = dataView.getView(DATA).orElseThrow(() -> new IllegalStateException("Unable to find the data compound."));
        dataView.getViewList(OBJECTIVES).ifPresent(list -> list.forEach(entry -> {
            final String name = entry.getString(NAME).get();
            final Text displayName = LanternTexts.fromLegacy(entry.getString(DISPLAY_NAME).get());
            final Criterion criterion = Sponge.getRegistry().getType(Criterion.class, entry.getString(CRITERION_NAME).get())
                    .orElseGet(() -> {
                        Lantern.getLogger().warn("Unable to find a criterion with id: {}, default to dummy.",
                                entry.getString(CRITERION_NAME).get());
                        return Criteria.DUMMY;
                    });
            final ObjectiveDisplayMode objectiveDisplayMode = Sponge.getRegistry().getType(ObjectiveDisplayMode.class,
                    entry.getString(DISPLAY_MODE).get()).orElseGet(() -> {
                        Lantern.getLogger().warn("Unable to find a display mode with id: {}, default to integer.",
                                entry.getString(CRITERION_NAME).get());
                        return ObjectiveDisplayModes.INTEGER;
                    });
            objectives.put(name, Objective.builder()
                    .name(name)
                    .displayName(displayName)
                    .criterion(criterion)
                    .objectiveDisplayMode(objectiveDisplayMode)
                    .build());
        }));
        dataView.getViewList(SCORES).ifPresent(list -> list.forEach(entry -> {
            // The invalid state is added by lantern, it means that there is already
            // a other score with the same name and target objective
            // We have to keep all the entries to remain compatible with vanilla mc.
            if (entry.getInt(INVALID).orElse(0) > 0) {
                return;
            }

            final Text name = LanternTexts.fromLegacy(entry.getString(NAME).get());
            final int value = entry.getInt(SCORE).get();
            final boolean locked = entry.getInt(LOCKED).orElse(0) > 0; // TODO

            final String objectiveName = entry.getString(OBJECTIVE).get();
            Score score = null;

            Objective objective = objectives.get(objectiveName);
            if (objective != null) {
                score = addToObjective(objective, null, name, value);
            }

            final List<String> extraObjectives = entry.getStringList(EXTRA_OBJECTIVES).orElse(null);
            if (extraObjectives != null) {
                for (String extraObjective : extraObjectives) {
                    objective = objectives.get(extraObjective);
                    if (objective != null) {
                        score = addToObjective(objective, score, name, value);
                    }
                }
            }
        }));

        final List<Team> teams = new ArrayList<>();
        dataView.getViewList(TEAMS).ifPresent(list -> list.forEach(entry -> {
            final Team.Builder builder = Team.builder()
                    .allowFriendlyFire(entry.getInt(ALLOW_FRIENDLY_FIRE).orElse(0) > 0)
                    .canSeeFriendlyInvisibles(entry.getInt(CAN_SEE_FRIENDLY_INVISIBLES).orElse(0) > 0)
                    .name(entry.getString(NAME).get())
                    .displayName(LanternTexts.fromLegacy(entry.getString(DISPLAY_NAME).get()))
                    .prefix(LanternTexts.fromLegacy(entry.getString(PREFIX).get()))
                    .suffix(LanternTexts.fromLegacy(entry.getString(SUFFIX).get()))
                    .members(entry.getStringList(MEMBERS).get().stream().map(LanternTexts::fromLegacy).collect(Collectors.toSet()));
            entry.getString(NAME_TAG_VISIBILITY).ifPresent(value -> builder.nameTagVisibility(Sponge.getRegistry().getAllOf(Visibility.class)
                    .stream().filter(visibility -> visibility.getName().equals(value)).findFirst().orElseGet(() -> {
                Lantern.getLogger().warn("Unable to find a name tag visibility with id: {}, default to always.", value);
                return Visibilities.ALWAYS;
            })));
            entry.getString(DEATH_MESSAGE_VISIBILITY).ifPresent(value -> builder.deathTextVisibility(Sponge.getRegistry().getAllOf(Visibility.class)
                    .stream().filter(visibility -> visibility.getName().equals(value)).findFirst().orElseGet(() -> {
                Lantern.getLogger().warn("Unable to find a death message visibility with id: {}, default to always.", value);
                return Visibilities.ALWAYS;
            })));
            entry.getString(COLLISION_RULE).ifPresent(value -> builder.collisionRule(Sponge.getRegistry().getAllOf(CollisionRule.class)
                    .stream().filter(visibility -> visibility.getName().equals(value)).findFirst().orElseGet(() -> {
                Lantern.getLogger().warn("Unable to find a collision rule with id: {}, default to never.", value);
                return CollisionRules.NEVER;
            })));
            entry.getString(TEAM_COLOR).ifPresent(color -> {
                TextColor textColor = Sponge.getRegistry().getType(TextColor.class, color).orElseGet(() -> {
                    Lantern.getLogger().warn("Unable to find a team color with id: {}, default to none.", color);
                    return TextColors.NONE;
                });
                if (textColor != TextColors.NONE && textColor != TextColors.RESET) {
                    builder.color(textColor);
                }
            });
            teams.add(builder.build());
        }));

        final Scoreboard scoreboard = scoreboardBuilder.objectives(new ArrayList<>(objectives.values())).teams(teams).build();

        dataView.getView(DISPLAY_SLOTS).ifPresent(displaySlots -> {
            for (DataQuery key : displaySlots.getKeys(false)) {
                final Matcher matcher = DISPLAY_SLOT_PATTERN.matcher(key.getParts().get(0));
                if (matcher.matches()) {
                    final int internalId = Integer.parseInt(matcher.group(1));
                    Lantern.getRegistry().getRegistryModule(DisplaySlotRegistryModule.class).get().getByInternalId(internalId).ifPresent(slot -> {
                        final Objective objective = objectives.get(displaySlots.getString(key).get());
                        if (objective != null) {
                            scoreboard.updateDisplaySlot(objective, slot);
                        }
                    });
                }
            }
        });

        return scoreboard;
    }

    private static Score addToObjective(Objective objective, @Nullable Score score, Text name, int value) {
        if (score == null) {
            score = objective.getOrCreateScore(name);
            score.setScore(value);
        } else {
            objective.addScore(score);
        }
        return score;
    }

    public static void write(Path folder, Scoreboard scoreboard) throws IOException {
        final List<DataView> objectives = scoreboard.getObjectives().stream().map(objective -> DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED)
                .set(NAME, objective.getName())
                .set(DISPLAY_NAME, ((LanternObjective) objective).getLegacyDisplayName())
                .set(CRITERION_NAME, objective.getCriterion().getId())
                .set(DISPLAY_MODE, objective.getDisplayMode().getId())).collect(Collectors.toList());

        final List<DataView> scores = new ArrayList<>();
        for (Score score : scoreboard.getScores()) {
            final Iterator<Objective> it = score.getObjectives().iterator();
            final DataView baseView = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED)
                    .set(NAME, ((LanternScore) score).getLegacyName())
                    .set(SCORE, score.getScore());
            // TODO: Locked state

            final DataView mainView = baseView.copy()
                    .set(OBJECTIVE, it.next().getName());

            final List<String> extraObjectives = new ArrayList<>();
            while (it.hasNext()) {
                final String extraObjectiveName = it.next().getName();
                scores.add(baseView.copy()
                        .set(OBJECTIVE, extraObjectiveName)
                        .set(INVALID, (byte) 1));
                extraObjectives.add(extraObjectiveName);
            }

            if (!extraObjectives.isEmpty()) {
                mainView.set(EXTRA_OBJECTIVES, extraObjectives);
            }
        }

        final List<DataView> teams = new ArrayList<>();
        for (Team team : scoreboard.getTeams()) {
           final DataView container = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED)
                    .set(ALLOW_FRIENDLY_FIRE, (byte) (team.allowFriendlyFire() ? 1 : 0))
                    .set(CAN_SEE_FRIENDLY_INVISIBLES, (byte) (team.canSeeFriendlyInvisibles() ? 1 : 0))
                    .set(NAME_TAG_VISIBILITY, team.getNameTagVisibility().getName())
                    .set(NAME, team.getName())
                    .set(DISPLAY_NAME, ((LanternTeam) team).getLegacyDisplayName())
                    .set(DEATH_MESSAGE_VISIBILITY, team.getDeathMessageVisibility().getName())
                    .set(COLLISION_RULE, team.getCollisionRule().getName())
                    .set(PREFIX, ((LanternTeam) team).getLegacyPrefix())
                    .set(SUFFIX, ((LanternTeam) team).getLegacySuffix());
            final TextColor teamColor = team.getColor();
            if (teamColor != TextColors.NONE) {
                container.set(TEAM_COLOR, teamColor.getId());
            }
            final Set<Text> members = team.getMembers();
            container.set(MEMBERS, members.stream().map(LanternTexts::toLegacy).collect(Collectors.toList()));
            teams.add(container);
        }

        final DataContainer rootDataContainer = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);
        final DataView dataView = rootDataContainer.createView(DATA)
                .set(OBJECTIVES, objectives)
                .set(SCORES, scores)
                .set(TEAMS, teams);

        final DataView displaySlots = dataView.createView(DISPLAY_SLOTS);
        ((LanternScoreboard) scoreboard).getObjectivesInSlot().entrySet().forEach(entry ->
                displaySlots.set(DataQuery.of("slot_" + ((LanternDisplaySlot) entry.getKey()).getInternalId()), entry.getValue().getName()));

        IOHelper.write(folder.resolve(SCOREBOARD_DATA), file -> {
            NbtStreamUtils.write(rootDataContainer, Files.newOutputStream(file), true);
            return true;
        });
    }
}
