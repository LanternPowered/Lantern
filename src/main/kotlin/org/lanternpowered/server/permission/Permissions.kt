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
package org.lanternpowered.server.permission

object Permissions {

    const val SELECTOR_PERMISSION = "minecraft.selector"

    const val SELECTOR_LEVEL = 1

    const val COMMAND_BLOCK_PERMISSION = "minecraft.commandblock"

    const val COMMAND_BLOCK_LEVEL = 2

    object Login {

        const val BYPASS_WHITELIST_PERMISSION = "minecraft.login.bypass-whitelist"

        const val BYPASS_WHITELIST_LEVEL = 1

        const val BYPASS_PLAYER_LIMIT_PERMISSION = "minecraft.login.bypass-player-limit"

        const val BYPASS_PLAYER_LIMIT_LEVEL = 1
    }

    object Chat {

        const val FORMAT_URLS = "lantern.chat.format-urls"

        const val FORMAT_URLS_LEVEL = 0
    }
}
