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
package org.lanternpowered.server.world;

import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector3d;
import org.lanternpowered.server.world.pregen.LanternChunkPreGenerateTask;
import org.spongepowered.api.world.ChunkPreGenerate;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldBorder;

final class LanternWorldBorder implements WorldBorder {

    private final LanternWorld world;

    LanternWorldBorder(LanternWorld world) {
        world.getProperties().updateCurrentBorderTime();
        this.world = world;
    }

    @Override
    public double getNewDiameter() {
        return this.world.getProperties().getWorldBorderTargetDiameter();
    }

    @Override
    public double getDiameter() {
        return this.world.getProperties().getWorldBorderDiameter();
    }

    @Override
    public void setDiameter(double diameter) {
        this.world.getProperties().setWorldBorderDiameter(diameter);
    }

    @Override
    public void setDiameter(double diameter, long time) {
        this.setDiameter(this.getDiameter(), diameter, time);
    }

    @Override
    public void setDiameter(double startDiameter, double endDiameter, long time) {
        this.world.getProperties().setBorderDiameter(startDiameter, endDiameter, time);
    }

    @Override
    public long getTimeRemaining() {
        return this.world.getProperties().getWorldBorderTimeRemaining();
    }

    @Override
    public void setCenter(double x, double z) {
        this.world.getProperties().setWorldBorderCenter(x, z);
    }

    @Override
    public Vector3d getCenter() {
        return this.world.getProperties().getWorldBorderCenter();
    }

    @Override
    public int getWarningTime() {
        return this.world.getProperties().getWorldBorderWarningTime();
    }

    @Override
    public void setWarningTime(int time) {
        this.world.getProperties().setWorldBorderWarningTime(time);
    }

    @Override
    public int getWarningDistance() {
        return this.world.getProperties().getWorldBorderWarningDistance();
    }

    @Override
    public void setWarningDistance(int distance) {
        this.world.getProperties().setWorldBorderWarningDistance(distance);
    }

    @Override
    public double getDamageThreshold() {
        return this.world.getProperties().getWorldBorderDamageThreshold();
    }

    @Override
    public void setDamageThreshold(double distance) {
        this.world.getProperties().setWorldBorderDamageThreshold(distance);
    }

    @Override
    public double getDamageAmount() {
        return this.world.getProperties().getWorldBorderDamageAmount();
    }

    @Override
    public void setDamageAmount(double damage) {
        this.world.getProperties().setWorldBorderDamageAmount(damage);
    }

    @Override
    public ChunkPreGenerate.Builder newChunkPreGenerate(World world) {
        checkNotNull(world, "world");
        return new LanternChunkPreGenerateTask.Builder(world, getCenter(), getDiameter());
    }
}
