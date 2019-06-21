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
