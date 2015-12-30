/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
package org.lanternpowered.server.status;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.network.status.Favicon;
import org.spongepowered.api.text.Text;

import java.util.Optional;

import javax.annotation.Nullable;

public class LanternStatusResponse implements ClientPingServerEvent.Response {

    private final MinecraftVersion version;

    @Nullable private Favicon favicon;
    private Text description;
    private Players players;

    private boolean hidePlayers;

    public LanternStatusResponse(MinecraftVersion version, Favicon favicon, Text description, Players players) {
        this.description = description;
        this.favicon = favicon;
        this.version = version;
        this.players = players;
    }

    @Override
    public Text getDescription() {
        return this.description;
    }

    @Override
    public MinecraftVersion getVersion() {
        return this.version;
    }

    @Override
    public Optional<Favicon> getFavicon() {
        return Optional.ofNullable(this.favicon);
    }

    @Override
    public void setDescription(Text description) {
        this.description = checkNotNull(description, "description");
    }

    @Override
    public void setHidePlayers(boolean hide) {
        this.hidePlayers = hide;
    }

    @Override
    public void setFavicon(@Nullable Favicon favicon) {
        this.favicon = favicon;
    }

    @Override
    public Optional<Players> getPlayers() {
        if (this.hidePlayers) {
            return Optional.empty();
        }
        return Optional.of(this.players);
    }

    /**
     * Gets the instance of the players safely, so it will never be null.
     * 
     * @return the players
     */
    public Players getPlayerSafely() {
        return this.players;
    }
}
