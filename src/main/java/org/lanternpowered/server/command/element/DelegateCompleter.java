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
package org.lanternpowered.server.command.element;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;

import java.util.List;

@FunctionalInterface
public interface DelegateCompleter {

    /**
     * Fetch completions for command arguments.
     *
     * @param src The source requesting tab completions
     * @param args The arguments currently provided
     * @param context The context to store state in
     * @param original The original completer
     * @return Any relevant completions
     */
    List<String> complete(CommandSource src, CommandArgs args, CommandContext context, Completer original);
}
