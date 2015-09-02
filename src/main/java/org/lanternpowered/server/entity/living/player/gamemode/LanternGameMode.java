package org.lanternpowered.server.entity.living.player.gamemode;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.text.translation.Translation;

public class LanternGameMode implements GameMode {

    private final String name;
    private final int internalId;

    public LanternGameMode(String name, int internalId) {
        this.internalId = internalId;
        this.name = name;
    }

    @Override
    public String getId() {
        return this.name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Translation getTranslation() {
        return LanternGame.get().getRegistry().getTranslationManager().get("gameMode." + this.name);
    }

    public int getInternalId() {
        return this.internalId;
    }
}
