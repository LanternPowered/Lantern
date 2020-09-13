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

import org.lanternpowered.api.scoreboard.ScoreboardTeam
import org.lanternpowered.api.scoreboard.ScoreboardTeamBuilder
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.emptyText
import org.lanternpowered.api.text.format.NamedTextColor
import org.lanternpowered.api.text.textOf
import org.lanternpowered.api.text.toPlain
import org.lanternpowered.api.util.collections.toImmutableSet
import org.spongepowered.api.scoreboard.CollisionRule
import org.spongepowered.api.scoreboard.CollisionRules
import org.spongepowered.api.scoreboard.Visibilities
import org.spongepowered.api.scoreboard.Visibility

class LanternTeamBuilder : ScoreboardTeamBuilder {

    private var name: String? = null
    private var displayName: Text? = null
    private var prefix: Text = emptyText()
    private var suffix: Text = emptyText()
    private var color: NamedTextColor = NamedTextColor.WHITE
    private var allowFriendlyFire = false
    private var showFriendlyInvisibles = false
    private var nameTagVisibility: Visibility = Visibilities.ALWAYS.get()
    private var deathMessageVisibility: Visibility = Visibilities.ALWAYS.get()
    private var collisionRule: CollisionRule = CollisionRules.NEVER.get()
    private var members: Set<Text> = emptySet()

    override fun displayName(displayName: Text): LanternTeamBuilder = apply {
        val length = displayName.toPlain().length
        check(length <= 32) { "Display name is $length characters long! It must be at most 32." }
        this.displayName = displayName
    }

    override fun color(color: NamedTextColor): LanternTeamBuilder = apply { this.color = color }
    override fun name(name: String): LanternTeamBuilder = apply { this.name = name }
    override fun prefix(prefix: Text): LanternTeamBuilder = apply { this.prefix = prefix }
    override fun suffix(suffix: Text): LanternTeamBuilder = apply { this.suffix = suffix }
    override fun allowFriendlyFire(enabled: Boolean): LanternTeamBuilder = apply { this.allowFriendlyFire = enabled }
    override fun canSeeFriendlyInvisibles(enabled: Boolean): LanternTeamBuilder = apply { this.showFriendlyInvisibles = enabled }
    override fun nameTagVisibility(visibility: Visibility): LanternTeamBuilder = apply { this.nameTagVisibility = visibility }
    override fun deathTextVisibility(visibility: Visibility): LanternTeamBuilder = apply { this.deathMessageVisibility = visibility }
    override fun collisionRule(rule: CollisionRule): LanternTeamBuilder = apply { this.collisionRule = rule }
    override fun members(members: Set<Text>): LanternTeamBuilder = apply { this.members = members.toImmutableSet() }

    override fun from(value: ScoreboardTeam): LanternTeamBuilder = name(value.name)
            .displayName(value.displayName)
            .color(value.color)
            .allowFriendlyFire(value.allowFriendlyFire())
            .canSeeFriendlyInvisibles(value.canSeeFriendlyInvisibles())
            .nameTagVisibility(value.nameTagVisibility)
            .deathTextVisibility(value.deathMessageVisibility)
            .members(value.members)

    override fun reset(): LanternTeamBuilder = apply {
        this.name = null
        this.displayName = null
        this.prefix = emptyText()
        this.suffix = emptyText()
        this.allowFriendlyFire = false
        this.showFriendlyInvisibles = false
        this.color = NamedTextColor.WHITE
        this.nameTagVisibility = Visibilities.ALWAYS.get()
        this.deathMessageVisibility = Visibilities.ALWAYS.get()
        this.collisionRule = CollisionRules.NEVER.get()
        this.members = emptySet()
    }

    override fun build(): ScoreboardTeam {
        val name = checkNotNull(this.name) { "The name must be set" }
        val displayName = this.displayName ?: textOf(name)
        val team = LanternTeam(name, this.color, displayName, this.prefix, this.suffix,
                this.allowFriendlyFire, this.showFriendlyInvisibles, this.nameTagVisibility,
                this.deathMessageVisibility, this.collisionRule)
        this.members.forEach { member -> team.addMember(member) }
        return team
    }
}
