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
package org.lanternpowered.server.command.targeted;

import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

public class TargetedVector3dElement extends CommandElement {

    public static TargetedVector3dElement of(Text key) {
        return new TargetedVector3dElement(key);
    }

    private final CommandElement delegate;

    private TargetedVector3dElement(Text key) {
        super(key);
        this.delegate = GenericArguments.vector3d(key);
    }

    @Override
    public void parse(CommandSource source, CommandArgs args, CommandContext context) throws ArgumentParseException {
        this.delegate.parse(source, args, context);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        // Shouldn't be called
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        // The state of the args before we call the delegate
        Object state = args.getState();
        List<String> result = this.delegate.complete(src, args, context);
        // Return if there already a result is or if the source does not use targeted blocks
        // TODO: Why is there a empty string in the vector3d tab completation?
        if ((!result.isEmpty() && result.size() != 1 && !result.get(0).isEmpty()) || !(src instanceof TargetingCommandSource)) {
            return result;
        }
        TargetingCommandSource source = (TargetingCommandSource) src;
        Optional<Vector3i> position = source.getTargetBlock();
        // No position found
        if (!position.isPresent()) {
            return result;
        }
        // Reset the state to get the arg
        args.setState(state);
        if (!args.nextIfPresent().isPresent()) {
            return result;
        }
        if (args.nextIfPresent().isPresent()) {
            if (args.nextIfPresent().isPresent()) {
                // Store the current state
                state = args.getState();
                if (args.nextIfPresent().isPresent()) {
                    // We finished the vector3d, reset before the last arg
                    args.setState(state);
                    Lantern.getLogger().warn("Attempted to complete to many args, vector3d has only 3 components.");
                } else {
                    // The z is being completed
                    return Collections.singletonList(position.get().getZ() + "");
                }
            } else {
                // The y is being completed
                return Collections.singletonList(position.get().getY() + "");
            }
        } else {
            // The x is being completed
            return Collections.singletonList(position.get().getX() + "");
        }
        return result;
    }
}
