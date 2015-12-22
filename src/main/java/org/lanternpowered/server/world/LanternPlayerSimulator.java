/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
package org.lanternpowered.server.world;

import javax.annotation.Nullable;

import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.PlayerSimulator;

import com.flowpowered.math.vector.Vector3i;

public class LanternPlayerSimulator implements PlayerSimulator {

    private final LanternWorld world;
    @Nullable private final LanternPlayer player;

    public LanternPlayerSimulator(LanternWorld world, @Nullable LanternPlayer player) {
        this.player = player;
        this.world = world;
    }

    @Override
    public void interactBlock(Vector3i position, Direction side) {
        this.interactBlock(position.getX(), position.getY(), position.getZ(), side);
    }

    @Override
    public void interactBlock(int x, int y, int z, Direction side) {
        this.interactBlockWith(x, y, z, null, side);
    }

    @Override
    public void interactBlockWith(Vector3i position, @Nullable ItemStack itemStack, Direction side) {
        this.interactBlockWith(position.getX(), position.getY(), position.getZ(), itemStack, side);
    }

    @Override
    public void interactBlockWith(int x, int y, int z, @Nullable ItemStack itemStack, Direction side) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean digBlock(Vector3i position) {
        return this.digBlock(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public boolean digBlock(int x, int y, int z) {
        return this.digBlockWith(x, y, z, null);
    }

    @Override
    public boolean digBlockWith(Vector3i position, @Nullable ItemStack itemStack) {
        return this.digBlockWith(position.getX(), position.getY(), position.getZ(), itemStack);
    }

    @Override
    public boolean digBlockWith(int x, int y, int z, @Nullable ItemStack itemStack) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getBlockDigTimeWith(Vector3i position, @Nullable ItemStack itemStack) {
        return this.getBlockDigTimeWith(position.getX(), position.getY(), position.getZ(), itemStack);
    }

    @Override
    public int getBlockDigTimeWith(int x, int y, int z, @Nullable ItemStack itemStack) {
        // TODO Auto-generated method stub
        return 0;
    }
}
