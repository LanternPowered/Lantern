package org.lanternpowered.server.command;

import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.game.LanternMinecraftVersion;
import org.lanternpowered.server.text.translation.TranslationManager;
import org.spongepowered.api.Game;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.spec.CommandSpec;

public final class CommandVersion implements Command {

    private final Game game;

    private final Translation description;
    private final Translation minecraftVersion;
    private final Translation implementationVersion;
    private final Translation apiVersion;

    public CommandVersion(LanternGame game) {
        TranslationManager manager = game.getRegistry().getTranslationManager();

        this.game = game;
        this.description = manager.get("commands.version.description");
        this.minecraftVersion = manager.get("commands.version.minecraft");
        this.implementationVersion = manager.get("commands.version.implementation");
        this.apiVersion = manager.get("commands.version.api");
    }

    @Override
    public CommandSpec build() {
        return CommandSpec.builder()
                .permission("minecraft.command.version")
                .description(Texts.of(this.description))
                .executor((src, args) -> {
                    src.sendMessage(Texts.of(minecraftVersion, LanternMinecraftVersion.CURRENT.getName(),
                            LanternMinecraftVersion.CURRENT.getProtocol()));
                    src.sendMessage(Texts.of(apiVersion, game.getPlatform().getApiVersion()));
                    src.sendMessage(Texts.of(implementationVersion, game.getPlatform().getVersion()));
                    return CommandResult.success();
                }).build();
    }
}
