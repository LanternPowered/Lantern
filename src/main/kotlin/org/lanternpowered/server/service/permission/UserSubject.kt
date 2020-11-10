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
package org.lanternpowered.server.service.permission

import org.lanternpowered.api.profile.copyWithoutProperties
import org.lanternpowered.server.config.user.OpsEntry
import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.profile.LanternGameProfile
import org.lanternpowered.server.service.permission.OpLevelCollection.OpLevelSubject
import org.spongepowered.api.profile.GameProfile
import org.spongepowered.api.service.context.Context
import org.spongepowered.api.service.permission.MemorySubjectData
import org.spongepowered.api.service.permission.PermissionService
import org.spongepowered.api.service.permission.SubjectCollection
import org.spongepowered.api.service.permission.SubjectReference
import org.spongepowered.api.util.Tristate
import java.util.Optional
import java.util.UUID

/**
 * An implementation of vanilla minecraft's 4 op groups.
 */
internal class UserSubject(
        private val player: GameProfile,
        private val collection: UserCollection
) : LanternSubject() {

    private val data: MemorySubjectData

    init {
        this.data = object : SingleParentMemorySubjectData(this) {

            override var parent: SubjectReference?
                get() {
                    val opLevel = opLevel
                    return if (opLevel == 0) null else collection.service.getGroupForOpLevel(opLevel).asSubjectReference()
                }
                set(parent) {
                    val opLevel = if (parent == null) {
                        0
                    } else {
                        val subject = parent.resolve().join() as? OpLevelSubject ?: return
                        subject.opLevel
                    }
                    if (opLevel > 0) {
                        Lantern.getGame().opsConfig.addEntry(OpsEntry(player.copyWithoutProperties(), opLevel))
                    } else {
                        Lantern.getGame().opsConfig.removeEntry(player.uniqueId)
                    }
                }
        }
    }

    val uniqueId: UUID
        get() = this.player.uniqueId

    override fun getIdentifier(): String = this.player.uniqueId.toString()
    override fun getFriendlyIdentifier(): Optional<String> = this.player.name
    override fun getContainingCollection(): SubjectCollection = this.collection
    override fun getSubjectData(): MemorySubjectData = this.data

    val opLevel: Int
        get() = Lantern.getGame().opsConfig.getEntryByUUID(this.player.uniqueId).map { obj -> obj.opLevel }.orElse(0)

    override val service: PermissionService
        get() = this.collection.service

    override fun getPermissionValue(contexts: Set<Context>, permission: String): Tristate {
        var result = super.getPermissionValue(contexts, permission)
        if (result == Tristate.UNDEFINED)
            result = this.getDataPermissionValue(this.collection.defaults.subjectData, permission)
        if (result == Tristate.UNDEFINED)
            result = this.getDataPermissionValue(this.collection.service.defaults.subjectData, permission)
        if (result == Tristate.UNDEFINED && this.opLevel >= Lantern.getGame().globalConfig.defaultOpPermissionLevel)
            result = Tristate.TRUE
        return result
    }

    override fun getOption(contexts: Set<Context>, key: String): Optional<String> {
        var result = super.getOption(contexts, key)
        if (!result.isPresent)
            result = getDataOptionValue(this.collection.defaults.subjectData, key)
        if (!result.isPresent)
            result = getDataOptionValue(this.collection.service.defaults.subjectData, key)
        return result
    }
}