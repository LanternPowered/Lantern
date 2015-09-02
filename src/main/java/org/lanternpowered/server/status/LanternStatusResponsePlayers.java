package org.lanternpowered.server.status;

import java.util.List;

import org.spongepowered.api.GameProfile;
import org.spongepowered.api.event.server.PingServerEvent;

import com.google.common.collect.ImmutableList;

public class LanternStatusResponsePlayers implements PingServerEvent.Response.Players {

    private final List<GameProfile> profiles;

    private int online;
    private int maximum;

    public LanternStatusResponsePlayers(List<GameProfile> profiles, int online, int maximum) {
        this.profiles = ImmutableList.copyOf(profiles);
        this.maximum = maximum;
        this.online = online;
    }

    @Override
    public int getOnline() {
        return this.online;
    }

    @Override
    public int getMax() {
        return this.maximum;
    }

    @Override
    public List<GameProfile> getProfiles() {
        return this.profiles;
    }

    @Override
    public void setMax(int max) {
        this.maximum = max;
    }

    @Override
    public void setOnline(int online) {
        this.online = online;
    }
}
