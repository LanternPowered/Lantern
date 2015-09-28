package org.lanternpowered.server.plugin;

import org.spongepowered.api.Game;
import org.spongepowered.api.plugin.PluginContainer;

public final class MinecraftPluginContainer implements PluginContainer {

    private final Game game;

    public MinecraftPluginContainer(Game game) {
        this.game = game;
    }

    @Override
    public String getId() {
        return "minecraft";
    }

    @Override
    public String getName() {
        return "Minecraft";
    }

    @Override
    public String getVersion() {
        return this.game.getPlatform().getVersion();
    }

    @Override
    public Object getInstance() {
        return this.game.getServer();
    }
}
