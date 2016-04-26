/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
package org.lanternpowered.server.command;

import com.flowpowered.math.GenericMath;
import com.flowpowered.math.vector.Vector3d;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.effect.sound.SoundCategory;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.text.Text;

public final class CommandPlaySound {

    public static final String PERMISSION = "minecraft.command.playsound";
    private static final Vector3d FOUR = new Vector3d(4.0, 4.0, 4.0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .arguments(
                        GenericArguments.catalogedElement(Text.of("sound"), SoundType.class),
                        GenericArguments.catalogedElement(Text.of("category"), SoundCategory.class),
                        GenericArguments.player(Text.of("player")),
                        GenericArguments.optional(GenericArguments.vector3d(Text.of("position"))),
                        GenericArguments.optional(GenericArguments.doubleNum(Text.of("volume"))),
                        GenericArguments.optional(GenericArguments.doubleNum(Text.of("pitch"))),
                        GenericArguments.optional(GenericArguments.doubleNum(Text.of("minimumVolume"))))
                .permission(PERMISSION)
                .executor((src, args) -> {
                    SoundType soundType = args.<SoundType>getOne("sound").get();
                    SoundCategory soundCategory = args.<SoundCategory>getOne("category").get();
                    LanternPlayer player = args.<LanternPlayer>getOne("player").get();

                    double volume = GenericMath.clamp(args.<Double>getOne("volume").orElse(1.0), 0.0, Double.MAX_VALUE);
                    // Volume greater then 1 will increase the distance the sound can be heared
                    double soundDistance = volume <= 1.0 ? 16.0 : volume * 16.0;
                    double pitch = GenericMath.clamp(args.<Double>getOne("pitch").orElse(1.0), 0.5, 2.0);
                    double minVolume = GenericMath.clamp(args.<Double>getOne("minimumVolume").orElse(0.0), 0.0, 1.0);

                    Vector3d playerPos = player.getPosition();
                    Vector3d position = args.<Vector3d>getOne("position").orElse(playerPos);

                    // The sound is played outside of the default volume and there
                    // is a minimum volume specified
                    if (minVolume > 0.0 && playerPos != position && position.distanceSquared(playerPos) > soundDistance * soundDistance) {
                        position = position.sub(playerPos).normalize().mul(FOUR).add(position);
                        volume = minVolume;
                    }

                    player.playSound(soundType, soundCategory, position, volume, pitch);
                    return CommandResult.success();
                })
                .build();
    }

    private CommandPlaySound() {
    }
}
