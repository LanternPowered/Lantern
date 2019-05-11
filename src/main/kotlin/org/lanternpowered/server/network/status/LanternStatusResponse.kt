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
package org.lanternpowered.server.network.status

import org.lanternpowered.api.ext.emptyOptional
import org.lanternpowered.api.ext.optional
import org.lanternpowered.server.game.version.LanternMinecraftVersion
import org.lanternpowered.api.util.ToStringHelper
import org.spongepowered.api.MinecraftVersion
import org.spongepowered.api.event.server.ClientPingServerEvent
import org.spongepowered.api.network.status.Favicon
import org.spongepowered.api.text.Text

class LanternStatusResponse(
        private var version: MinecraftVersion,
        private var description: Text,
        private val players: ClientPingServerEvent.Response.Players,
        private var favicon: Favicon?
) : ClientPingServerEvent.Response {

    private var hidePlayers = false

    /**
     * Sets the [MinecraftVersion] of the status response.
     *
     * The name of this version will be displayed on the client when the
     * server or the client is outdated.
     *
     * @param version The version
     */
    fun setVersion(version: MinecraftVersion) = run { this.version = version }

    /**
     * Sets the [MinecraftVersion] of the status response.
     *
     * The name of this version will be displayed on the client when the
     * server or the client is outdated.
     *
     * @param name The name
     * @param protocol The protocol version
     * @param legacy Whether the version is legacy
     */
    fun setVersion(name: String, protocol: Int, legacy: Boolean) {
        setVersion(LanternMinecraftVersion(name, protocol, legacy))
    }

    override fun getDescription() = this.description
    override fun getVersion() = this.version
    override fun getFavicon() = this.favicon.optional()
    override fun getPlayers() = if (this.hidePlayers) emptyOptional() else this.players.optional()

    override fun setDescription(description: Text) = run { this.description = description }
    override fun setHidePlayers(hide: Boolean) = run { this.hidePlayers = hide }
    override fun setFavicon(favicon: Favicon?) = run { this.favicon = favicon }

    override fun toString(): String {
        return ToStringHelper(this)
                .omitNullValues()
                .add("version", this.version)
                .add("description", this.description)
                .add("players", this.players)
                .add("hidePlayers", this.hidePlayers)
                .add("favicon", this.favicon)
                .toString()
    }
}
