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
package org.lanternpowered.server.block;

import org.spongepowered.api.block.BlockSoundGroup;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

public final class BlockSoundGroups {

    // SORTFIELDS:ON

    public static final BlockSoundGroup ANVIL = DummyObjectProvider.createFor(BlockSoundGroup.class, "ANVIL");

    public static final BlockSoundGroup CLOTH = DummyObjectProvider.createFor(BlockSoundGroup.class, "CLOTH");

    public static final BlockSoundGroup GLASS = DummyObjectProvider.createFor(BlockSoundGroup.class, "GLASS");

    public static final BlockSoundGroup GRASS = DummyObjectProvider.createFor(BlockSoundGroup.class, "GRASS");

    public static final BlockSoundGroup GRAVEL = DummyObjectProvider.createFor(BlockSoundGroup.class, "GRAVEL");

    public static final BlockSoundGroup LADDER = DummyObjectProvider.createFor(BlockSoundGroup.class, "LADDER");

    public static final BlockSoundGroup METAL = DummyObjectProvider.createFor(BlockSoundGroup.class, "METAL");

    public static final BlockSoundGroup SAND = DummyObjectProvider.createFor(BlockSoundGroup.class, "SAND");

    public static final BlockSoundGroup SLIME = DummyObjectProvider.createFor(BlockSoundGroup.class, "SLIME");

    public static final BlockSoundGroup SNOW = DummyObjectProvider.createFor(BlockSoundGroup.class, "SNOW");

    public static final BlockSoundGroup STONE = DummyObjectProvider.createFor(BlockSoundGroup.class, "STONE");

    public static final BlockSoundGroup WOOD = DummyObjectProvider.createFor(BlockSoundGroup.class, "WOOD");

    // SORTFIELDS:OFF

    private BlockSoundGroups() {
    }
}
