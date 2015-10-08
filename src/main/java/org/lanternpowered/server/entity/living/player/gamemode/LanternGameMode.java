package org.lanternpowered.server.entity.living.player.gamemode;

import org.lanternpowered.server.catalog.SimpleLanternCatalogType;
import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.text.translation.Translation;

public class LanternGameMode extends SimpleLanternCatalogType implements GameMode {

    private final int internalId;

    public LanternGameMode(String identifier, int internalId) {
        super(identifier);
        this.internalId = internalId;
    }

    @Override
    public Translation getTranslation() {
        return LanternGame.get().getRegistry().getTranslationManager().get("gameMode." + this.getName());
    }

    public int getInternalId() {
        return this.internalId;
    }
}
