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
package org.lanternpowered.server.block.trait;

import org.lanternpowered.server.data.key.LanternKeys;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.state.BooleanStateProperty;

public final class LanternBooleanTraits {

    public static final BooleanStateProperty SNOWY = LanternBooleanTrait.minecraft("snowy", Keys.SNOWED);

    public static final BooleanStateProperty PERSISTENT = LanternBooleanTrait.minecraft("persistent", Keys.PERSISTENT);

    public static final BooleanStateProperty IS_WET = LanternBooleanTrait.minecraft("wet", Keys.IS_WET);

    public static final BooleanStateProperty OCCUPIED = LanternBooleanTrait.minecraft("occupied", Keys.OCCUPIED);

    public static final BooleanStateProperty ENABLED = LanternBooleanTrait.minecraft("enabled", LanternKeys.ENABLED);

    public static final BooleanStateProperty TRIGGERED = LanternBooleanTrait.minecraft("triggered", LanternKeys.TRIGGERED);

    public static final BooleanStateProperty POWERED = LanternBooleanTrait.minecraft("powered", Keys.POWERED);

    public static final BooleanStateProperty EXPLODE = LanternBooleanTrait.minecraft("explode", LanternKeys.EXPLODE);

    public static final BooleanStateProperty HAS_MUSIC_DISC = LanternBooleanTrait.minecraft("has_record", LanternKeys.HAS_MUSIC_DISC);

    public static final BooleanStateProperty LIT = LanternBooleanTrait.minecraft("lit", Keys.LIT);

    public static final BooleanStateProperty WATERLOGGED = LanternBooleanTrait.minecraft("waterlogged", LanternKeys.WATERLOGGED);

    public static final BooleanStateProperty CONNECTED_NORTH = LanternBooleanTrait.minecraft("north", LanternKeys.CONNECTED_NORTH);

    public static final BooleanStateProperty CONNECTED_SOUTH = LanternBooleanTrait.minecraft("south", LanternKeys.CONNECTED_SOUTH);

    public static final BooleanStateProperty CONNECTED_EAST = LanternBooleanTrait.minecraft("east", LanternKeys.CONNECTED_EAST);

    public static final BooleanStateProperty CONNECTED_WEST = LanternBooleanTrait.minecraft("west", LanternKeys.CONNECTED_WEST);

}
