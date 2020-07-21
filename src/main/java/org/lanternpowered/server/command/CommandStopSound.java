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
package org.lanternpowered.server.command;

import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.vanilla.packet.type.play.StopSoundsPacket;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.effect.sound.SoundCategory;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

public final class CommandStopSound extends CommandProvider {

    public CommandStopSound() {
        super(2, "stopsound");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        specBuilder
                .arguments(
                        GenericArguments.player(Text.of("player")),
                        GenericArguments.optional(GenericArguments.catalogedElement(Text.of("category"), SoundCategory.class)),
                        GenericArguments.optional(GenericArguments.catalogedElement(Text.of("sound"), SoundType.class))
                )
                .executor((src, args) -> {
                    SoundCategory category = args.<SoundCategory>getOne("category").orElse(null);
                    String type = args.<SoundType>getOne("sound").map(CatalogType::getKey).map(ResourceKey::toString).orElse(null);
                    LanternPlayer player = args.<LanternPlayer>getOne("player").get();
                    player.getConnection().send(new StopSoundsPacket(type, category));
                    return CommandResult.success();
                });
    }
}
