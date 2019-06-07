/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import org.lanternpowered.server.world.LanternWorld;
import org.lanternpowered.server.world.LanternWorldProperties;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class CommandHelper {

    public static final String PLAIN_WORLD_KEY = "world";

    /**
     * The key that is used by default for world parameters.
     */
    public static final Text WORLD_KEY = Text.of(PLAIN_WORLD_KEY);

    public static LanternWorld getWorld(@Nullable CommandSource src,
            CommandContext args) throws CommandException {
        return getWorld(src, args, PLAIN_WORLD_KEY);
    }

    public static LanternWorld getWorld(@Nullable CommandSource src,
            CommandContext args, String key) throws CommandException {
        return getWorld0(src, args.getOne(key));
    }

    public static LanternWorld getWorld(@Nullable CommandSource src,
            CommandContext args, Text key) throws CommandException {
        return getWorld0(src, args.getOne(key));
    }

    private static LanternWorld getWorld0(@Nullable CommandSource src,
            Optional<WorldProperties> optWorldProperties) throws CommandException {
        final WorldProperties worldProperties = getWorldProperties0(src, optWorldProperties);
        return (LanternWorld) Sponge.getServer().getWorld(worldProperties.getUniqueId()).orElseThrow(() -> new CommandException(
                t("The world %s must be loaded.", worldProperties.getWorldName())));
    }

    public static LanternWorldProperties getWorldProperties(@Nullable CommandSource src,
            CommandContext args) throws CommandException {
        return getWorldProperties(src, args, PLAIN_WORLD_KEY);
    }

    public static LanternWorldProperties getWorldProperties(@Nullable CommandSource src,
            CommandContext args, String key) throws CommandException {
        return getWorldProperties0(src, args.getOne(key));
    }

    public static LanternWorldProperties getWorldProperties(@Nullable CommandSource src,
            CommandContext args, Text key) throws CommandException {
        return getWorldProperties0(src, args.getOne(key));
    }

    private static LanternWorldProperties getWorldProperties0(@Nullable CommandSource src,
            Optional<WorldProperties> optWorldProperties) throws CommandException {
        WorldProperties world;
        if (optWorldProperties.isPresent()) {
            world = optWorldProperties.get();
        } else if (src instanceof Locatable) {
            world = ((Locatable) src).getWorld().getProperties();
        } else {
            world = Sponge.getServer().getDefaultWorld().orElse(null);
            if (world == null) {
                // Shouldn't happen
                throw new CommandException(Text.of("Unable to find the default world."));
            }
        }
        return (LanternWorldProperties) world;
    }

    private CommandHelper() {
    }
}
