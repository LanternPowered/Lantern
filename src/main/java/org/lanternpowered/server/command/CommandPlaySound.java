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

import org.lanternpowered.server.command.element.GenericArguments2;
import org.lanternpowered.server.entity.player.LanternPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.effect.sound.SoundCategory;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.math.GenericMath;
import org.spongepowered.math.vector.Vector3d;

public final class CommandPlaySound extends CommandProvider {

    private static final Vector3d FOUR_VECTOR = new Vector3d(4.0, 4.0, 4.0);

    public CommandPlaySound() {
        super(2, "playsound");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        specBuilder
                .arguments(
                        GenericArguments.catalogedElement(Text.of("sound"), SoundType.class),
                        GenericArguments.catalogedElement(Text.of("category"), SoundCategory.class),
                        GenericArguments.player(Text.of("player")),
                        GenericArguments.optional(GenericArguments2.targetedVector3d(Text.of("position"))),
                        GenericArguments.optional(GenericArguments2.doubleNum(Text.of("volume")), 1.0),
                        GenericArguments.optional(GenericArguments2.doubleNum(Text.of("pitch"), 1.0)),
                        GenericArguments.optional(GenericArguments2.doubleNum(Text.of("minimum-volume"), 0.0)))
                .executor((src, args) -> {
                    SoundType soundType = args.<SoundType>getOne("sound").get();
                    SoundCategory soundCategory = args.<SoundCategory>getOne("category").get();
                    LanternPlayer player = args.<LanternPlayer>getOne("player").get();

                    double volume = GenericMath.clamp(args.<Double>getOne("volume").orElse(1.0), 0.0, Double.MAX_VALUE);
                    // Volume greater then 1 will increase the distance the sound can be heared
                    double soundDistance = volume <= 1.0 ? 16.0 : volume * 16.0;
                    double pitch = GenericMath.clamp(args.<Double>getOne("pitch").orElse(1.0), 0.5, 2.0);
                    double minVolume = GenericMath.clamp(args.<Double>getOne("minimum-volume").orElse(0.0), 0.0, 1.0);

                    Vector3d playerPos = player.getPosition();
                    Vector3d position = args.<Vector3d>getOne("position").orElse(playerPos);

                    // The sound is played outside of the default volume and there
                    // is a minimum volume specified
                    if (minVolume > 0.0 && playerPos != position && position.distanceSquared(playerPos) > soundDistance * soundDistance) {
                        position = position.sub(playerPos).normalize().mul(FOUR_VECTOR).add(playerPos);
                        volume = minVolume;
                    }

                    player.playSound(soundType, soundCategory, position, volume, pitch);
                    return CommandResult.success();
                });
    }
}
