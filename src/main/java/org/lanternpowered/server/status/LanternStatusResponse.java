package org.lanternpowered.server.status;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.event.server.PingServerEvent;
import org.spongepowered.api.status.Favicon;
import org.spongepowered.api.text.Text;

import com.google.common.base.Optional;

public class LanternStatusResponse implements PingServerEvent.Response {

    private final MinecraftVersion version;

    private Favicon favicon;
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
        return Optional.fromNullable(this.favicon);
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
    public void setFavicon(Favicon favicon) {
        this.favicon = favicon;
    }

    @Override
    public Optional<Players> getPlayers() {
        if (this.hidePlayers) {
            return Optional.absent();
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
