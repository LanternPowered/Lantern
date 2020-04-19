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
package org.lanternpowered.testserver.plugin;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.title.Title;

@Plugin(id = "test_command", authors = "Meronat", version = "1.0.0")
public class TestCommandPlugin {

    @Inject
    private Logger logger;

    @Listener
    public void onStart(GameInitializationEvent event) {
        this.logger.info("TestCommand plugin enabled!");

        Sponge.getCommandManager().register(this, CommandSpec.builder()
                .executor((src, args) -> {
                    final Text message = Text.of(TextColors.GREEN, TextStyles.UNDERLINE, "Test", TextStyles.RESET, " 1234");
                    if (src instanceof Player) {
                        ((Player) src).sendTitle(Title.builder().subtitle(message).build());
                    } else {
                        src.sendMessage(message);
                    }
                    return CommandResult.success();
                })
                .build(), "test-command", "tc");
    }
}
