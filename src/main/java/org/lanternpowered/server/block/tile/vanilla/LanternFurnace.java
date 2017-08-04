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
package org.lanternpowered.server.block.tile.vanilla;

import org.lanternpowered.server.block.tile.LanternTileEntity;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;

public class LanternFurnace extends LanternTileEntity {

    @Override
    public void registerKeys() {
        super.registerKeys();
        registerKey(Keys.MAX_BURN_TIME, 0, 0, Integer.MAX_VALUE);
        registerKey(Keys.PASSED_BURN_TIME, 0, 0, Keys.MAX_BURN_TIME);
        registerKey(Keys.MAX_COOK_TIME, 0, 0, Integer.MAX_VALUE);
        registerKey(Keys.PASSED_COOK_TIME, 0, 0, Keys.MAX_COOK_TIME);
    }

    @Override
    public BlockState getBlock() {
        final BlockState block = getLocation().getBlock();
        return block.getType() == BlockTypes.FURNACE || block.getType() == BlockTypes.LIT_FURNACE ? block :
                BlockTypes.FURNACE.getDefaultState();
    }
}
