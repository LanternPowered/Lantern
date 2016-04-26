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
package org.lanternpowered.server.command.element;

import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.util.GuavaCollectors;
import org.spongepowered.api.util.StartsWithPredicate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

/**
 * This {@link CommandElement} will delegate the tab completation
 * through the {@link CommandElementCompleter} if the delegate element
 * wasn't able to complete anything.
 */
public class DelegateCompleterElement extends CommandElement {

    public static DelegateCompleterElement vector3d(CommandElement element, CommandElementCompleter xCompletor,
            CommandElementCompleter yCompletor, CommandElementCompleter zCompletor) {
        return new DelegateCompleterElement(element, (src, args, context) -> {
            if (!args.nextIfPresent().isPresent()) {
                return Collections.emptyList();
            }
            if (args.nextIfPresent().isPresent()) {
                if (args.nextIfPresent().isPresent()) {
                    // Store the current state
                    Object state = args.getState();
                    if (args.nextIfPresent().isPresent()) {
                        // We finished the vector3d, reset before the last arg
                        args.setState(state);
                        Lantern.getLogger().warn("Attempted to complete to many args, vector3d has only 3 components.");
                    } else {
                        // The z is being completed
                        return zCompletor.complete(src, args, context);
                    }
                } else {
                    // The y is being completed
                    return yCompletor.complete(src, args, context);
                }
            } else {
                // The x is being completed
                return xCompletor.complete(src, args, context);
            }
            return Collections.emptyList();
        });
    }

    public static DelegateCompleterElement defaultValues(CommandElement element, boolean matchBegin, Object... args) {
        List<String> arguments = Arrays.asList(args).stream().map(Object::toString).collect(GuavaCollectors.toImmutableList());
        CommandElementCompleter completer;
        if (matchBegin) {
            completer = (src, args1, context) -> {
                Optional<String> arg = args1.nextIfPresent();
                if (arg.isPresent()) {
                    return arguments.stream().filter(new StartsWithPredicate(arg.get())).collect(GuavaCollectors.toImmutableList());
                } else {
                    return Collections.emptyList();
                }
            };
        } else {
            completer = (src, args1, context) -> {
                Optional<String> arg = args1.nextIfPresent();
                if (arg.isPresent()) {
                    return arguments;
                } else {
                    return Collections.emptyList();
                }
            };
        }
        return new DelegateCompleterElement(element, completer);
    }

    public static DelegateCompleterElement of(CommandElement element, CommandElementCompleter completer) {
        return new DelegateCompleterElement(element, completer);
    }

    private final CommandElement element;
    private final CommandElementCompleter completer;

    DelegateCompleterElement(CommandElement element, CommandElementCompleter completer) {
        super(element.getKey());
        this.element = element;
        this.completer = completer;
    }

    @Override
    public void parse(CommandSource source, CommandArgs args, CommandContext context) throws ArgumentParseException {
        this.element.parse(source, args, context);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        Object state = args.getState();
        List<String> result = this.element.complete(src, args, context);
        // TODO: Why is there a empty string in the vector3d tab completation?
        if (!result.isEmpty() && result.size() != 1 && !result.get(0).isEmpty()) {
            return result;
        }
        args.setState(state);
        return this.completer.complete(src, args, context);
    }
}
