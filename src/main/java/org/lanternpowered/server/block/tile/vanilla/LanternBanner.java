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
import org.lanternpowered.server.data.ValueCollection;
import org.lanternpowered.server.network.tile.TileEntityProtocolTypes;
import org.spongepowered.api.block.tileentity.Banner;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;

import java.util.ArrayList;

public class LanternBanner extends LanternTileEntity implements Banner {

    public LanternBanner() {
        setProtocolType(TileEntityProtocolTypes.BANNER);
    }

    @Override
    public void registerKeys() {
        super.registerKeys();

        final ValueCollection c = getValueCollection();
        c.registerNonRemovable(Keys.BANNER_BASE_COLOR, DyeColors.WHITE);
        c.registerNonRemovable(Keys.BANNER_PATTERNS, new ArrayList<>());
    }
}
