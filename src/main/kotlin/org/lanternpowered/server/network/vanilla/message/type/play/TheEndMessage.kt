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
package org.lanternpowered.server.network.vanilla.message.type.play

import org.lanternpowered.server.network.message.Message
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.monster.boss.dragon.EnderDragon

/**
 * This message will be send when a [Player] the
 * [EnderDragon] defeats. This will open the credits
 * or directly send a [MessagePlayInPerformRespawn].
 */
data class TheEndMessage(val playCredits: Boolean) : Message
