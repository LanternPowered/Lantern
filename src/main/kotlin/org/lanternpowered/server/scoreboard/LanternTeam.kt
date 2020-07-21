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

import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.api.util.collections.toImmutableSet
import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutTeams
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutTeams.AddMembers
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutTeams.RemoveMembers
import org.spongepowered.api.scoreboard.CollisionRule
import org.spongepowered.api.scoreboard.Scoreboard
import org.spongepowered.api.scoreboard.Team
import org.spongepowered.api.scoreboard.Visibility
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColor
import java.util.Optional

class LanternTeam internal constructor(
        private val name: String,
        private var color: TextColor,
        private var displayName: Text,
        private var prefix: Text,
        private var suffix: Text,
        private var allowFriendlyFire: Boolean,
        private var canSeeFriendlyInvisibles: Boolean,
        private var nameTagVisibility: Visibility,
        private var deathMessageVisibility: Visibility,
        private var collisionRule: CollisionRule
) : Team {

    private var scoreboard: LanternScoreboard? = null
    private val members = mutableSetOf<Text>()

    fun setScoreboard(scoreboard: LanternScoreboard?) {
        this.scoreboard = scoreboard
    }

    fun toCreateMessage(): PacketPlayOutTeams.CreateOrUpdate =
            PacketPlayOutTeams.Create(this.name, this.displayName, this.prefix, this.suffix, this.nameTagVisibility,
                    this.collisionRule, this.color, this.allowFriendlyFire, this.canSeeFriendlyInvisibles, this.members.toImmutableList())

    private fun toUpdateMessage(): PacketPlayOutTeams.CreateOrUpdate =
            PacketPlayOutTeams.Update(this.name, this.displayName, this.prefix, this.suffix, this.nameTagVisibility,
                    this.collisionRule, this.color, this.allowFriendlyFire, this.canSeeFriendlyInvisibles)

    private fun sendUpdate() {
        this.scoreboard?.sendToPlayers { listOf(toUpdateMessage()) }
    }

    override fun getName(): String = this.name
    override fun getDisplayName(): Text = this.displayName
    override fun getColor(): TextColor = this.color
    override fun getPrefix(): Text = this.prefix
    override fun getSuffix(): Text = this.suffix
    override fun getCollisionRule(): CollisionRule = this.collisionRule

    override fun setCollisionRule(rule: CollisionRule) {
        val update = rule != this.collisionRule
        this.collisionRule = rule
        if (update)
            sendUpdate()
    }

    override fun setDisplayName(displayName: Text) {
        val length = displayName.toPlain().length
        check(length <= 32) { "Display name is $length characters long! It must be at most 32." }
        val update = this.displayName != displayName
        this.displayName = displayName
        if (update)
            sendUpdate()
    }

    override fun setColor(color: TextColor) {
        val update = color != this.color
        this.color = color
        if (update)
            sendUpdate()
    }

    override fun setPrefix(prefix: Text) {
        val update = this.prefix != prefix
        this.prefix = prefix
        if (update)
            sendUpdate()
    }

    override fun setSuffix(suffix: Text) {
        val update = this.suffix != suffix
        this.suffix = suffix
        if (update)
            sendUpdate()
    }

    override fun allowFriendlyFire(): Boolean = this.allowFriendlyFire

    override fun setAllowFriendlyFire(enabled: Boolean) {
        val update = enabled != this.allowFriendlyFire
        this.allowFriendlyFire = enabled
        if (update)
            sendUpdate()
    }

    override fun canSeeFriendlyInvisibles(): Boolean = this.canSeeFriendlyInvisibles

    override fun setCanSeeFriendlyInvisibles(enabled: Boolean) {
        val update = enabled != this.canSeeFriendlyInvisibles
        this.canSeeFriendlyInvisibles = enabled
        if (update)
            sendUpdate()
    }

    override fun getNameTagVisibility(): Visibility = this.nameTagVisibility

    override fun setNameTagVisibility(visibility: Visibility) {
        val update = visibility != this.nameTagVisibility
        this.nameTagVisibility = visibility
        if (update)
            sendUpdate()
    }

    override fun getDeathMessageVisibility(): Visibility {
        return deathMessageVisibility
    }

    override fun setDeathMessageVisibility(visibility: Visibility) {
        val update = visibility != this.deathMessageVisibility
        this.deathMessageVisibility = visibility
        if (update)
            sendUpdate()
    }

    override fun getMembers(): Set<Text> = this.members.toImmutableSet()

    override fun addMember(member: Text) {
        if (this.members.add(member)) {
            this.scoreboard?.sendToPlayers { listOf(AddMembers(this.name, listOf(member))) }
        }
    }

    override fun removeMember(member: Text): Boolean {
        if (this.members.remove(member)) {
            this.scoreboard?.sendToPlayers { listOf(RemoveMembers(this.name, listOf(member))) }
            return true
        }
        return false
    }

    fun hasMember(member: Text): Boolean = this.members.contains(member)

    fun addMembers(members: Collection<Text>): List<Text> {
        val failedMembers = mutableListOf<Text>()
        val addedMembers = mutableListOf<Text>()
        for (member in members) {
            if (this.members.add(member)) {
                addedMembers.add(member)
            } else {
                failedMembers.add(member)
            }
        }
        this.scoreboard?.sendToPlayers { listOf(AddMembers(this.name, addedMembers)) }
        return failedMembers
    }

    fun removeMembers(members: Collection<Text>): List<Text> {
        val failedMembers = mutableListOf<Text>()
        if (this.members.isEmpty()) {
            return failedMembers
        }
        val removedMembers = mutableListOf<Text>()
        for (member in members) {
            if (this.members.remove(member)) {
                removedMembers.add(member)
            } else {
                failedMembers.add(member)
            }
        }
        this.scoreboard?.sendToPlayers { listOf(RemoveMembers(this.name, removedMembers)) }
        return failedMembers
    }

    override fun getScoreboard(): Optional<Scoreboard> = this.scoreboard.optional()

    override fun unregister(): Boolean {
        val scoreboard = this.scoreboard ?: return false
        scoreboard.removeTeam(this)
        scoreboard.sendToPlayers { listOf(PacketPlayOutTeams.Remove(name)) }
        this.scoreboard = null
        return true
    }
}
