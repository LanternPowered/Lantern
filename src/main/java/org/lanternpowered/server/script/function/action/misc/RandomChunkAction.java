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
package org.lanternpowered.server.script.function.action.misc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.lanternpowered.api.script.Parameter;
import org.lanternpowered.api.script.ScriptContext;
import org.lanternpowered.api.script.function.action.Action;
import org.lanternpowered.api.script.function.value.IntValueProvider;
import org.lanternpowered.api.script.context.ContextParameters;
import org.lanternpowered.server.world.chunk.LanternChunk;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.Random;

public class RandomChunkAction implements Action {

    private static final Random random = new Random();

    /**
     * The amount of times that the actions should
     * be attempted to execute.
     */
    @Expose
    @SerializedName("actions-per-chunk")
    private IntValueProvider actionsPerChunk = IntValueProvider.constant(1);

    /**
     * The action that should be executed.
     */
    @Expose
    @SerializedName("action")
    private Action action = Action.empty();

    @Override
    public void run(@Parameter("context")ScriptContext context) {
        final Optional<World> world = context.firstValue(World.class);
        if (world.isPresent()) {
            final Iterable<Chunk> chunks = world.get().getLoadedChunks();
            final Optional<Location<World>> targetLocation = context.remove(ContextParameters.TARGET_LOCATION);
            for (Chunk chunk : chunks) {
                for (int i = 0; i < this.actionsPerChunk.get(context); i++) {
                    final LanternChunk chunk1 = (LanternChunk) chunk;

                    final int value = random.nextInt(0xffff);
                    final int x = chunk1.getX() + value & 0xf;
                    final int z = chunk1.getZ() + value >> 4 & 0xf;

                    context.put(ContextParameters.TARGET_LOCATION, new Location<>(world.get(), x, 0, z));
                    this.action.run(context);
                }
            }
            targetLocation.ifPresent(loc -> context.put(ContextParameters.TARGET_LOCATION, loc));
        }
    }
}
