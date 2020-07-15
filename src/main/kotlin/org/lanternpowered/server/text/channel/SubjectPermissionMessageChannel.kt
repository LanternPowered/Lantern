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
package org.lanternpowered.server.text.channel

import org.lanternpowered.api.service.serviceOf
import org.lanternpowered.api.util.collections.toImmutableSet
import org.spongepowered.api.service.permission.PermissionService
import org.spongepowered.api.text.channel.MessageChannel
import org.spongepowered.api.text.channel.MessageReceiver

/**
 * A message channel that targets all subjects with the given permission.
 *
 * @param permission The permission node
 */
class SubjectPermissionMessageChannel(private val permission: String) : MessageChannel {

    override fun getMembers(): Collection<MessageReceiver> {
        val service = serviceOf<PermissionService>() ?: return emptyList()
        return service.loadedCollections.values.stream()
                .flatMap { input ->
                    input.getLoadedWithPermission(this.permission).entries.stream()
                            .filter { it.key is MessageReceiver && it.value }
                            .map { it.key as MessageReceiver }
                }
                .toImmutableSet()
    }
}
