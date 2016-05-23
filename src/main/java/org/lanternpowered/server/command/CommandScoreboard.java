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
package org.lanternpowered.server.command;

import static org.lanternpowered.server.text.translation.TranslationHelper.t;
import static org.lanternpowered.server.text.translation.TranslationHelper.tb;
import static org.spongepowered.api.command.CommandMessageFormatting.error;

import org.lanternpowered.server.command.element.GenericArguments2;
import org.lanternpowered.server.scoreboard.LanternScore;
import org.lanternpowered.server.scoreboard.LanternTeam;
import org.lanternpowered.server.text.LanternTexts;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scoreboard.Score;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.Team;
import org.spongepowered.api.scoreboard.critieria.Criterion;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlot;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayMode;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public final class CommandScoreboard extends CommandProvider {

    public CommandScoreboard() {
        super(2, "scoreboard");
    }

    @Override
    public void completeSpec(CommandSpec.Builder specBuilder) {
        specBuilder
                .arguments(
                        GenericArguments.flags()
                                .valueFlag(GenericArguments.world(Text.of("world")), "-world", "w")
                                .buildWith(GenericArguments.none())
                )
                .child(CommandSpec.builder()
                        .child(CommandSpec.builder()
                                .executor((src, args) -> {
                                    // Get the scoreboard of the world the command source is located in
                                    final Scoreboard scoreboard = CommandHelper.getWorld(src, args).getWorld().get().getScoreboard();

                                    final Set<Objective> objectives = scoreboard.getObjectives();
                                    if (objectives.isEmpty()) {
                                        throw new CommandException(t("commands.scoreboard.objectives.list.empty"));
                                    }

                                    src.sendMessage(tb("commands.scoreboard.objectives.list.count", objectives.size())
                                            .color(TextColors.DARK_GREEN)
                                            .build());
                                    objectives.forEach(objective -> src.sendMessage(t("commands.scoreboard.objectives.list.entry",
                                            objective.getName(), objective.getDisplayName(), objective.getCriterion().getName())));
                                    return CommandResult.success();
                                })
                                .build(), "list")
                        .child(CommandSpec.builder()
                                .arguments(
                                        GenericArguments.string(Text.of("name")),
                                        GenericArguments.catalogedElement(Text.of("criterion"), Criterion.class),
                                        GenericArguments.flags()
                                                .valueFlag(GenericArguments.catalogedElement(
                                                        Text.of("display-mode"), ObjectiveDisplayMode.class), "-display-mode", "-dm", "d")
                                                .buildWith(GenericArguments.none()),
                                        GenericArguments2.remainingString(Text.of("display-name"))
                                )
                                .executor((src, args) -> {
                                    // Get the scoreboard of the world the command source is located in
                                    final Scoreboard scoreboard = CommandHelper.getWorld(src, args).getWorld().get().getScoreboard();

                                    final String name = args.<String>getOne("name").get();
                                    final Criterion criterion = args.<Criterion>getOne("criterion").get();

                                    if (scoreboard.getObjective(name).isPresent()) {
                                        throw new CommandException(t("commands.scoreboard.objectives.add.alreadyExists", name));
                                    }
                                    if (name.length() > 16) {
                                        throw new CommandException(t("commands.scoreboard.objectives.add.tooLong", name, 16));
                                    }
                                    String displayName = args.<String>getOne("display-name").orElse(null);
                                    if (displayName != null && displayName.length() > 32) {
                                        throw new CommandException(t("commands.scoreboard.objectives.add.displayTooLong", displayName, 32));
                                    }

                                    Objective.Builder builder = Objective.builder()
                                            .name(name)
                                            .criterion(criterion);
                                    if (displayName != null && !displayName.isEmpty()) {
                                        builder.displayName(Text.of(displayName));
                                    }
                                    args.<ObjectiveDisplayMode>getOne("display-mode").ifPresent(builder::objectiveDisplayMode);

                                    scoreboard.addObjective(builder.build());
                                    src.sendMessage(t("commands.scoreboard.objectives.add.success", name));
                                    return CommandResult.success();
                                })
                                .build(), "add")
                        .child(CommandSpec.builder()
                                .arguments(
                                        GenericArguments.string(Text.of("name"))
                                )
                                .executor((src, args) -> {
                                    // Get the scoreboard of the world the command source is located in
                                    final Scoreboard scoreboard = CommandHelper.getWorld(src, args).getWorld().get().getScoreboard();

                                    final String name = args.<String>getOne("name").get();
                                    scoreboard.removeObjective(scoreboard.getObjective(name).orElseThrow(
                                            () -> new CommandException(t("commands.scoreboard.objectiveNotFound", name))));
                                    src.sendMessage(t("commands.scoreboard.objectives.remove.success", name));
                                    return CommandResult.success();
                                })
                                .build(), "remove")
                        .child(CommandSpec.builder()
                                .arguments(
                                        GenericArguments.catalogedElement(Text.of("display-slot"), DisplaySlot.class),
                                        GenericArguments.optional(GenericArguments.string(Text.of("name")))
                                )
                                .executor((src, args) -> {
                                    // Get the scoreboard of the world the command source is located in
                                    final Scoreboard scoreboard = CommandHelper.getWorld(src, args).getWorld().get().getScoreboard();

                                    final Optional<String> optName = args.<String>getOne("name");
                                    DisplaySlot displaySlot = args.<DisplaySlot>getOne("display-slot").get();
                                    Objective objective = null;
                                    if (optName.isPresent()) {
                                        final String name = optName.get();
                                        objective = scoreboard.getObjective(name).orElseThrow(
                                                () -> new CommandException(t("commands.scoreboard.objectiveNotFound", name)));
                                        src.sendMessage(t("commands.scoreboard.objectives.setdisplay.successSet", displaySlot.getName(), name));
                                    } else {
                                        src.sendMessage(t("commands.scoreboard.objectives.setdisplay.successCleared", displaySlot.getName()));
                                    }
                                    scoreboard.updateDisplaySlot(objective, displaySlot);
                                    return CommandResult.success();
                                })
                                .build(), "setdisplay")
                        .build(), "objectives")
                .child(CommandSpec.builder()
                        .child(CommandSpec.builder()
                                .arguments(
                                        GenericArguments.optional(GenericArguments.string(Text.of("target")))
                                )
                                .executor((src, args) -> {
                                    // Get the scoreboard of the world the command source is located in
                                    final Scoreboard scoreboard = CommandHelper.getWorld(src, args).getWorld().get().getScoreboard();

                                    int result;
                                    if (args.hasAny("target")) {
                                        String entity = args.<String>getOne("target").get();
                                        Set<Score> scores = scoreboard.getScores(Text.of(entity));
                                        if (scores.isEmpty()) {
                                            throw new CommandException(t("commands.scoreboard.players.list.player.empty"));
                                        }
                                        result = scores.size();
                                        src.sendMessage(tb("commands.scoreboard.players.list.player.count", result, entity)
                                                .color(TextColors.DARK_GREEN)
                                                .build());
                                        scores.forEach(score -> score.getObjectives().forEach(objective ->
                                                src.sendMessage(t("commands.scoreboard.players.list.player.entry",
                                                        score.getScore(), objective.getDisplayName(), objective.getName()))));
                                    } else {
                                        Set<Text> names = scoreboard.getScores().stream().map(Score::getName).collect(Collectors.toSet());
                                        if (names.isEmpty()) {
                                            throw new CommandException(t("commands.scoreboard.players.list.empty"));
                                        }
                                        result = names.size();
                                        src.sendMessage(tb("commands.scoreboard.players.list.count", result)
                                                .color(TextColors.DARK_GREEN)
                                                .build());
                                        src.sendMessage(Text.joinWith(Text.of(", "), names));
                                    }
                                    return CommandResult.builder().successCount(1).queryResult(result).build();
                                })
                                .build(), "list")
                        .child(createPlayerScoreSpec(Score::setScore), "set")
                        .child(createPlayerScoreSpec((score, value) -> score.setScore(score.getScore() + value)), "add")
                        .child(createPlayerScoreSpec((score, value) -> score.setScore(score.getScore() - value)), "remove")
                        .child(CommandSpec.builder()
                                .arguments(
                                        GenericArguments.string(Text.of("target")),
                                        GenericArguments.optional(GenericArguments.string(Text.of("objective")))
                                )
                                .executor((src, args) -> {
                                    // Get the scoreboard of the world the command source is located in
                                    final Scoreboard scoreboard = CommandHelper.getWorld(src, args).getWorld().get().getScoreboard();

                                    String target = args.<String>getOne("target").get();
                                    if (args.hasAny("objective")) {
                                        String objectiveName = args.<String>getOne("objective").get();
                                        Objective objective = scoreboard.getObjective(objectiveName).orElseThrow(
                                                () -> new CommandException(t("commands.scoreboard.objectiveNotFound", objectiveName)));
                                        objective.removeScore(Text.of(target));
                                        src.sendMessage(t("commands.scoreboard.players.resetscore.success", objectiveName, target));
                                    } else {
                                        scoreboard.removeScores(Text.of(target));
                                        src.sendMessage(t("commands.scoreboard.players.reset.success", target));
                                    }
                                    return CommandResult.success();
                                })
                                .build(), "reset")
                        .build(), "players")
                .child(CommandSpec.builder()
                        .child(CommandSpec.builder()
                                .arguments(
                                        GenericArguments.optional(GenericArguments.string(Text.of("name")))
                                )
                                .executor((src, args) -> {
                                    // Get the scoreboard of the world the command source is located in
                                    final Scoreboard scoreboard = CommandHelper.getWorld(src, args).getWorld().get().getScoreboard();

                                    int result;
                                    if (args.hasAny("name")) {
                                        String teamName = args.<String>getOne("name").get();
                                        Team team = scoreboard.getTeam(teamName).orElseThrow(
                                                () -> new CommandException(t("commands.scoreboard.teamNotFound", teamName)));
                                        Set<Text> members = team.getMembers();
                                        if (members.isEmpty()) {
                                            throw new CommandException(t("commands.scoreboard.teams.list.player.empty", teamName));
                                        }
                                        result = members.size();
                                        src.sendMessage(tb("commands.scoreboard.teams.list.player.count", result, teamName)
                                                .color(TextColors.DARK_GREEN)
                                                .build());
                                        src.sendMessage(Text.joinWith(Text.of(", "), members));
                                    } else {
                                        Set<Team> teams = scoreboard.getTeams();
                                        if (teams.isEmpty()) {
                                            throw new CommandException(t("commands.scoreboard.teams.list.empty"));
                                        }
                                        result = teams.size();
                                        src.sendMessage(tb("commands.scoreboard.teams.list.count", result)
                                                .color(TextColors.DARK_GREEN)
                                                .build());
                                        teams.forEach(team -> src.sendMessage(t("commands.scoreboard.teams.list.entry",
                                                team.getName(), team.getDisplayName(), team.getMembers().size())));
                                    }

                                    return CommandResult.builder().successCount(1).queryResult(result).build();
                                })
                                .build(), "list")
                        .child(CommandSpec.builder()
                                .arguments(
                                        GenericArguments.string(Text.of("name")),
                                        GenericArguments.optional(GenericArguments2.remainingString(Text.of("display-name")))
                                )
                                .executor((src, args) -> {
                                    // Get the scoreboard of the world the command source is located in
                                    final Scoreboard scoreboard = CommandHelper.getWorld(src, args).getWorld().get().getScoreboard();
                                    final String teamName = args.<String>getOne("name").get();

                                    if (scoreboard.getTeam(teamName).isPresent()) {
                                        throw new CommandException(t("commands.scoreboard.teams.add.alreadyExists", teamName));
                                    }
                                    if (teamName.length() > 16) {
                                        throw new CommandException(t("commands.scoreboard.teams.add.tooLong", teamName, 16));
                                    }
                                    String displayName = args.<String>getOne("display-name").orElse(null);
                                    if (displayName != null && displayName.length() > 32) {
                                        throw new CommandException(t("commands.scoreboard.teams.add.displayTooLong", displayName, 32));
                                    }
                                    Team.Builder teamBuilder = Team.builder()
                                            .name(teamName);
                                    if (displayName != null && !displayName.isEmpty()) {
                                        teamBuilder.displayName(Text.of(displayName));
                                    }
                                    scoreboard.registerTeam(teamBuilder.build());
                                    src.sendMessage(t("commands.scoreboard.teams.add.success", teamName));
                                    return CommandResult.success();
                                })
                                .build(), "add")
                        .child(CommandSpec.builder()
                                .arguments(
                                        GenericArguments.string(Text.of("name"))
                                )
                                .executor((src, args) -> {
                                    // Get the scoreboard of the world the command source is located in
                                    final Scoreboard scoreboard = CommandHelper.getWorld(src, args).getWorld().get().getScoreboard();
                                    String teamName = args.<String>getOne("name").get();
                                    final Team team = scoreboard.getTeam(teamName).orElseThrow(
                                            () -> new CommandException(t("commands.scoreboard.teamNotFound", teamName)));
                                    team.unregister();
                                    src.sendMessage(t("commands.scoreboard.teams.remove.success", teamName));
                                    return CommandResult.success();
                                })
                                .build(), "remove")
                        .child(CommandSpec.builder()
                                .arguments(
                                        GenericArguments.string(Text.of("name"))
                                )
                                .executor((src, args) -> {
                                    // Get the scoreboard of the world the command source is located in
                                    final Scoreboard scoreboard = CommandHelper.getWorld(src, args).getWorld().get().getScoreboard();
                                    String teamName = args.<String>getOne("name").get();
                                    final Team team = scoreboard.getTeam(teamName).orElseThrow(
                                            () -> new CommandException(t("commands.scoreboard.teamNotFound", teamName)));
                                    Set<Text> members = team.getMembers();
                                    if (members.isEmpty()) {
                                        throw new CommandException(t("commands.scoreboard.teams.empty.alreadyEmpty", teamName));
                                    }
                                    int result = members.size();
                                    ((LanternTeam) team).removeMembers(members);
                                    src.sendMessage(t("commands.scoreboard.teams.empty.success", result, teamName));
                                    return CommandResult.builder().successCount(1).queryResult(result).build();
                                })
                                .build(), "empty")
                        .child(CommandSpec.builder()
                                .arguments(
                                        GenericArguments.optional(GenericArguments.string(Text.of("name"))),
                                        GenericArguments.optional(GenericArguments2.remainingStringArray(Text.of("players")))
                                )
                                .executor((src, args) -> {
                                    // Get the scoreboard of the world the command source is located in
                                    final Scoreboard scoreboard = CommandHelper.getWorld(src, args).getWorld().get().getScoreboard();

                                    String teamName = args.<String>getOne("name").orElse(null);
                                    final Team team = teamName == null ? null : scoreboard.getTeam(teamName).orElse(null);
                                    Set<Text> members = args.<String[]>getOne("players")
                                            .map(array -> Arrays.stream(array).map(LanternTexts::fromLegacy).collect(Collectors.toSet()))
                                            .orElse(new HashSet<>());
                                    // The team doesn't exist, then assume that the team name a player is
                                    // that wants to leave a team
                                    if (teamName != null && team == null) {
                                        members.add(LanternTexts.fromLegacy(teamName));
                                    }

                                    // If there are no members found, use the source if possible
                                    if (members.isEmpty() && src instanceof Player) {
                                        members.add(((Player) src).getTeamRepresentation());
                                    }

                                    // If there is a team specified, remove the members from a specific team
                                    Collection<Team> teams = team == null ? scoreboard.getTeams() : Collections.singleton(team);
                                    List<Text> failedMembers = null;
                                    for (Team team0 : teams) {
                                        List<Text> failedMembers0 = ((LanternTeam) team0).removeMembers(members);
                                        if (failedMembers == null) {
                                            failedMembers = failedMembers0;
                                        } else {
                                            failedMembers.retainAll(failedMembers0);
                                        }
                                    }
                                    if (failedMembers != null) {
                                        members.removeAll(failedMembers);
                                    }

                                    int result = members.size();
                                    if (result > 0) {
                                        src.sendMessage(t("commands.scoreboard.teams.leave.success",
                                                result, Text.joinWith(Text.of(", "), members)));
                                    }
                                    if (failedMembers != null && failedMembers.size() > 0) {
                                        src.sendMessage(error(t("commands.scoreboard.teams.leave.failure",
                                                failedMembers.size(), Text.joinWith(Text.of(", "), failedMembers))));
                                    }
                                    return CommandResult.builder().successCount(1).queryResult(result).build();
                                })
                                .build(), "leave")
                        .child(CommandSpec.builder()
                                .arguments(
                                        GenericArguments.string(Text.of("name")),
                                        GenericArguments.optional(GenericArguments2.remainingStringArray(Text.of("players")))
                                )
                                .executor((src, args) -> {
                                    // Get the scoreboard of the world the command source is located in
                                    final Scoreboard scoreboard = CommandHelper.getWorld(src, args).getWorld().get().getScoreboard();

                                    String teamName = args.<String>getOne("name").get();
                                    final Team team = scoreboard.getTeam(teamName).orElseThrow(
                                            () -> new CommandException(t("commands.scoreboard.teamNotFound", teamName)));
                                    Set<Text> members = args.<String[]>getOne("players")
                                            .map(array -> Arrays.stream(array).map(LanternTexts::fromLegacy).collect(Collectors.toSet()))
                                            .orElse(new HashSet<>());

                                    // If there are no members found, use the source if possible
                                    if (members.isEmpty() && src instanceof Player) {
                                        members.add(((Player) src).getTeamRepresentation());
                                    }

                                    List<Text> failedMembers = ((LanternTeam) team).addMembers(members);
                                    int result = members.size();
                                    if (result > 0) {
                                        src.sendMessage(t("commands.scoreboard.teams.join.success",
                                                result, teamName, Text.joinWith(Text.of(", "), members)));
                                    }
                                    if (failedMembers.size() > 0) {
                                        src.sendMessage(error(t("commands.scoreboard.teams.join.failure",
                                                failedMembers.size(), teamName, Text.joinWith(Text.of(", "), failedMembers))));
                                    }
                                    return CommandResult.builder().successCount(1).queryResult(result).build();
                                })
                                .build(), "join")
                        .build(), "teams");
    }

    private static CommandSpec createPlayerScoreSpec(BiConsumer<Score, Integer> scoreConsumer) {
        return CommandSpec.builder()
                .arguments(
                        GenericArguments.string(Text.of("target")),
                        GenericArguments.string(Text.of("objective")),
                        GenericArguments.integer(Text.of("score"))
                )
                .executor((src, args) -> {
                    // Get the scoreboard of the world the command source is located in
                    final Scoreboard scoreboard = CommandHelper.getWorld(src, args).getWorld().get().getScoreboard();

                    String objectiveName = args.<String>getOne("objective").get();
                    Objective objective = scoreboard.getObjective(objectiveName).orElseThrow(
                            () -> new CommandException(t("commands.scoreboard.objectiveNotFound", objectiveName)));
                    String target = args.<String>getOne("target").get();

                    Collection<Score> scores;
                    if (target.equals("*")) {
                        scores = objective.getScores().values();
                    } else {
                        scores = Collections.singletonList(objective.getOrCreateScore(Text.of(target)));
                    }
                    int value = args.<Integer>getOne("score").get();

                    scores.forEach(score -> {
                        scoreConsumer.accept(score, value);
                        src.sendMessage(t("commands.scoreboard.players.set.success",
                                objectiveName, ((LanternScore) score).getLegacyName(), score.getScore()));
                    });

                    return CommandResult.success();
                })
                .build();
    }
}
