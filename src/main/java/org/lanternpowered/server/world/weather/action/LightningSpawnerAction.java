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
package org.lanternpowered.server.world.weather.action;

import com.flowpowered.math.vector.Vector3d;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.netty.util.concurrent.FastThreadLocal;
import org.lanternpowered.api.script.ScriptContext;
import org.lanternpowered.api.script.context.Parameters;
import org.lanternpowered.api.script.function.action.Action;
import org.lanternpowered.server.util.FastThreadLocals;
import org.lanternpowered.server.world.LanternWorld;
import org.lanternpowered.server.world.chunk.LanternChunk;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.Chunk;

import java.util.Random;

public class LightningSpawnerAction implements Action {

    private static final FastThreadLocal<Random> RANDOM = FastThreadLocals.withInitial(Random::new);

    @Expose
    @SerializedName("attempts-per-chunk")
    private int attemptsPerChunk = 2;

    @Expose
    @SerializedName("chance")
    private float chance = 0.0000002f;

    @Override
    public void run(ScriptContext scriptContext) {
        final LanternWorld world = (LanternWorld) scriptContext.get(Parameters.WORLD).get();
        final Cause cause = Cause.source(world.getWeather()).named("World", world).build();

        final Random random = RANDOM.get();
        final Iterable<Chunk> chunks = world.getLoadedChunks();
        final int chance = (int) (1f / Math.max(this.chance, 0.000000000001f));

        for (Chunk chunk : chunks) {
            for (int i = 0; i < this.attemptsPerChunk; i++) {
                final LanternChunk chunk1 = (LanternChunk) chunk;
                if (random.nextInt(chance) != 0) {
                    continue;
                }

                final int value = random.nextInt(0x10000);
                final int x = chunk1.getX() << 4 | value & 0xf;
                final int z = chunk1.getZ() << 4 | (value >> 4) & 0xf;

                final Entity entity = world.createEntity(EntityTypes.LIGHTNING, new Vector3d(x, world.getHighestYAt(x, z), z));
                world.spawnEntity(entity, cause);
            }
        }
    }
}
