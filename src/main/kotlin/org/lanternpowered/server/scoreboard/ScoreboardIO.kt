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
package org.lanternpowered.server.scoreboard

import org.lanternpowered.api.data.persistence.DataQuery
import org.lanternpowered.api.data.persistence.DataView
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.format.NamedTextColor
import org.lanternpowered.api.text.serializer.LegacyTextSerializer
import org.lanternpowered.api.util.collections.toImmutableSet
import org.lanternpowered.api.util.index.requireKey
import org.lanternpowered.api.util.index.requireValue
import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.registry.type.scoreboard.CollisionRuleRegistry
import org.lanternpowered.server.registry.type.scoreboard.CriterionRegistry
import org.lanternpowered.server.registry.type.scoreboard.DisplaySlotRegistry
import org.lanternpowered.server.registry.type.scoreboard.ObjectiveDisplayModeRegistry
import org.lanternpowered.server.registry.type.scoreboard.VisibilityRegistry
import org.lanternpowered.server.text.LanternTexts.fromLegacy
import org.lanternpowered.server.text.LanternTexts.toLegacy
import org.spongepowered.api.ResourceKey
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.scoreboard.Score
import org.spongepowered.api.scoreboard.Scoreboard
import org.spongepowered.api.scoreboard.Team
import org.spongepowered.api.scoreboard.criteria.Criteria
import org.spongepowered.api.scoreboard.objective.Objective
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayModes
import java.util.ArrayList
import kotlin.streams.toList

object ScoreboardIO {

    private val OBJECTIVES = DataQuery.of("Objectives")
    private val OBJECTIVE = DataQuery.of("Objective")
    private val EXTRA_OBJECTIVES = DataQuery.of("EObjectives") // Lantern

    private val NAME = DataQuery.of("Name")
    private val DISPLAY_NAME = DataQuery.of("DisplayName")
    private val CRITERION_NAME = DataQuery.of("CriteriaName")
    private val DISPLAY_MODE = DataQuery.of("RenderType")
    private val SCORES = DataQuery.of("PlayerScores")
    private val SCORE = DataQuery.of("Score")
    private val INVALID = DataQuery.of("Invalid") // Lantern

    private val LOCKED = DataQuery.of("Locked")
    private val ALLOW_FRIENDLY_FIRE = DataQuery.of("AllowFriendlyFire")
    private val CAN_SEE_FRIENDLY_INVISIBLES = DataQuery.of("SeeFriendlyInvisibles")
    private val NAME_TAG_VISIBILITY = DataQuery.of("NameTagVisibility")
    private val DEATH_MESSAGE_VISIBILITY = DataQuery.of("DeathMessageVisibility")
    private val COLLISION_RULE = DataQuery.of("CollisionRule")
    private val SUFFIX = DataQuery.of("Suffix")
    private val PREFIX = DataQuery.of("Prefix")
    private val TEAM_COLOR = DataQuery.of("TeamColor")
    private val MEMBERS = DataQuery.of("Players")
    private val TEAMS = DataQuery.of("Teams")
    private val DISPLAY_SLOTS = DataQuery.of("DisplaySlots")

    private val DISPLAY_SLOT_PATTERN = "^slot_([0-9]+)$".toRegex()

    fun deserialize(dataView: DataView?): Scoreboard {
        val scoreboardBuilder = Scoreboard.builder()
        if (dataView == null)
            return scoreboardBuilder.build()
        val objectives = mutableMapOf<String, Objective>()
        dataView.getViewList(OBJECTIVES).ifPresent { list ->
            list.forEach { entry ->
                val name = entry.getString(NAME).get()
                val displayName = fromLegacy(entry.getString(DISPLAY_NAME).get())
                val builder = Objective.builder()
                        .name(name)
                        .displayName(displayName)

                val criterionName = entry.getString(CRITERION_NAME).get()
                val criterion = CriterionRegistry[ResourceKey.resolve(criterionName)]
                if (criterion == null) {
                    Lantern.getLogger().warn("Unable to find a criterion with id: $criterionName, default to dummy.")
                    builder.criterion(Criteria.DUMMY)
                } else {
                    builder.criterion(criterion)
                }

                val displayModeName = entry.getString(DISPLAY_MODE).get()
                val displayMode = ObjectiveDisplayModeRegistry[ResourceKey.resolve(displayModeName)]
                if (displayMode == null) {
                    Lantern.getLogger().warn("Unable to find a display mode with id: $displayModeName, default to integer.")
                    builder.objectiveDisplayMode(ObjectiveDisplayModes.INTEGER)
                } else {
                    builder.objectiveDisplayMode(displayMode)
                }
                objectives[name] = builder.build()
            }
        }
        dataView.getViewList(SCORES).ifPresent { list ->
            list.forEach { entry ->
                // The invalid state is added by lantern, it means that there is already
                // a other score with the same name and target objective
                // We have to keep all the entries to remain compatible with vanilla mc.
                if (entry.getInt(INVALID).orElse(0) > 0) {
                    return@forEach
                }
                val name = fromLegacy(entry.getString(NAME).get())
                val value = entry.getInt(SCORE).get()
                val locked = entry.getInt(LOCKED).orElse(0) > 0 // TODO
                val objectiveName = entry.getString(OBJECTIVE).get()
                var score: Score? = null
                var objective = objectives[objectiveName]
                if (objective != null) {
                    score = addToObjective(objective, null, name, value)
                }
                val extraObjectives = entry.getStringList(EXTRA_OBJECTIVES).orElse(null)
                if (extraObjectives != null) {
                    for (extraObjective in extraObjectives) {
                        objective = objectives[extraObjective]
                        if (objective != null) {
                            score = addToObjective(objective, score, name, value)
                        }
                    }
                }
            }
        }
        val teams = mutableListOf<Team>()
        dataView.getViewList(TEAMS).ifPresent { list ->
            list.forEach { entry ->
                val builder = Team.builder()
                        .allowFriendlyFire(entry.getInt(ALLOW_FRIENDLY_FIRE).orElse(0) > 0)
                        .canSeeFriendlyInvisibles(entry.getInt(CAN_SEE_FRIENDLY_INVISIBLES).orElse(0) > 0)
                        .name(entry.getString(NAME).get())
                        .displayName(fromLegacy(entry.getString(DISPLAY_NAME).get()))
                        .prefix(fromLegacy(entry.getString(PREFIX).get()))
                        .suffix(fromLegacy(entry.getString(SUFFIX).get()))
                        .members(entry.getStringList(MEMBERS).get().stream()
                                .map { member -> LegacyTextSerializer.deserialize(member) }
                                .toImmutableSet())
                entry.getString(NAME_TAG_VISIBILITY).ifPresent { value -> builder.nameTagVisibility(VisibilityRegistry.require(value)) }
                entry.getString(DEATH_MESSAGE_VISIBILITY).ifPresent { value -> builder.deathTextVisibility(VisibilityRegistry.require(value)) }
                entry.getString(COLLISION_RULE).ifPresent { value -> builder.collisionRule(CollisionRuleRegistry.require(value)) }
                entry.getString(TEAM_COLOR).ifPresent { colorName -> builder.color(NamedTextColor.NAMES.requireValue(colorName)) }
                teams.add(builder.build())
            }
        }
        val scoreboard = scoreboardBuilder.objectives(objectives.values.toList()).teams(teams).build()
        dataView.getView(DISPLAY_SLOTS).ifPresent { displaySlots: DataView ->
            for (key in displaySlots.getKeys(false)) {
                val match = DISPLAY_SLOT_PATTERN.find(key.parts[0])
                if (match != null) {
                    val internalId = match.groupValues[1].toInt()
                    DisplaySlotRegistry.get(internalId)?.let { slot ->
                        val objective = objectives[displaySlots.getString(key).get()]
                        if (objective != null) {
                            scoreboard.updateDisplaySlot(objective, slot)
                        }
                    }
                }
            }
        }
        return scoreboard
    }

    private fun addToObjective(objective: Objective, score: Score?, name: Text, value: Int): Score? {
        if (score == null) {
            val newScore = objective.getOrCreateScore(name)
            newScore.score = value
            return newScore
        } else {
            objective.addScore(score)
        }
        return score
    }

    fun serialize(scoreboard: Scoreboard) {
        val objectives = scoreboard.objectives.stream()
                .map { objective ->
                    DataContainer.createNew()
                            .set(NAME, objective.name)
                            .set(DISPLAY_NAME, toLegacy(objective.displayName))
                            .set(CRITERION_NAME, objective.criterion.key)
                            .set(DISPLAY_MODE, objective.displayMode.key)
                }
                .toList()
        val scores: MutableList<DataView> = ArrayList()
        for (score in scoreboard.scores) {
            val it: Iterator<Objective> = score.objectives.iterator()
            val baseView = DataContainer.createNew()
                    .set(NAME, toLegacy(score.name))
                    .set(SCORE, score.score)
            // TODO: Locked state
            val mainView: DataView = baseView.copy()
                    .set(OBJECTIVE, it.next().name)
            val extraObjectives = mutableListOf<String>()
            while (it.hasNext()) {
                val extraObjectiveName = it.next().name
                scores.add(baseView.copy()
                        .set(OBJECTIVE, extraObjectiveName)
                        .set(INVALID, 1.toByte()))
                extraObjectives.add(extraObjectiveName)
            }
            if (extraObjectives.isNotEmpty())
                mainView[EXTRA_OBJECTIVES] = extraObjectives
        }
        val teams: MutableList<DataView> = ArrayList()
        for (team in scoreboard.teams) {
            val container = DataContainer.createNew()
                    .set(ALLOW_FRIENDLY_FIRE, (if (team.allowFriendlyFire()) 1 else 0).toByte())
                    .set(CAN_SEE_FRIENDLY_INVISIBLES, (if (team.canSeeFriendlyInvisibles()) 1 else 0).toByte())
                    .set(NAME_TAG_VISIBILITY, VisibilityRegistry.requireId(team.nameTagVisibility))
                    .set(NAME, team.name)
                    .set(DISPLAY_NAME, toLegacy(team.displayName))
                    .set(DEATH_MESSAGE_VISIBILITY, VisibilityRegistry.requireId(team.deathMessageVisibility))
                    .set(COLLISION_RULE, CollisionRuleRegistry.requireId(team.collisionRule))
                    .set(PREFIX, toLegacy(team.prefix))
                    .set(SUFFIX, toLegacy(team.suffix))
            val teamColor = team.color
            container[TEAM_COLOR] = NamedTextColor.NAMES.requireKey(teamColor)
            val members = team.members
            container[MEMBERS] = members.stream().map<String> { member -> LegacyTextSerializer.serialize(member) }.toList()
            teams.add(container)
        }
        val dataView = DataContainer.createNew()
                .set(OBJECTIVES, objectives)
                .set(SCORES, scores)
                .set(TEAMS, teams)
        val displaySlots = dataView.createView(DISPLAY_SLOTS)
        (scoreboard as LanternScoreboard).getObjectivesInSlot().forEach { (key, value) ->
            val id = DisplaySlotRegistry.getId(key)
            displaySlots[DataQuery.of("slot_$id")] = value.name
        }
    }
}
