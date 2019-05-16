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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Sets;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWorldBorder;
import org.spongepowered.api.util.temporal.TemporalUnits;
import org.spongepowered.api.world.WorldBorder;

import java.time.Duration;
import java.util.Set;
import java.util.function.Supplier;

public final class LanternWorldBorder implements WorldBorder {

    private static final int BOUNDARY = 29999984;

    // All the players tracking this world border
    private final Set<LanternPlayer> players = Sets.newConcurrentHashSet();

    // World border properties
    double centerX;
    double centerZ;

    // The current radius of the border
    double diameterStart = 60000000f;
    double diameterEnd = this.diameterStart;

    double warningDistance = 5;
    Duration warningTime = Duration.ofSeconds(15);

    double damage = 1;
    double damageThreshold = 5;

    // The remaining time will be stored in this
    // for the first world tick
    long lerpTime;

    // Shrink or growing times
    private long timeStart = -1;
    private long timeEnd;

    public void addPlayer(LanternPlayer player) {
        if (this.players.add(player)) {
            player.getConnection().send(new MessagePlayOutWorldBorder.Initialize(this.centerX, this.centerZ, getDiameter(),
                    getNewDiameter(), getTimeRemainingMillis(), BOUNDARY, getRoundedWarningDistance(), getWarningTimeSeconds()));
        }
    }

    public void removePlayer(LanternPlayer player) {
        this.players.remove(player);
    }

    private void broadcast(Supplier<Message> supplier) {
        if (!this.players.isEmpty()) {
            final Message message = supplier.get();
            this.players.forEach(p -> p.getConnection().send(message));
        }
    }

    int getRoundedWarningDistance() {
        return (int) Math.round(this.warningDistance);
    }

    @Override
    public double getNewDiameter() {
        return this.diameterEnd;
    }

    @Override
    public double getDiameter() {
        if (this.timeStart == -1) {
            updateCurrentTime();
        }

        if (this.diameterStart != this.diameterEnd) {
            final long lerpTime = this.timeEnd - this.timeStart;
            if (lerpTime == 0) {
                return this.diameterStart;
            }

            long elapsedTime = System.currentTimeMillis() - this.timeStart;
            elapsedTime = elapsedTime > lerpTime ? lerpTime : elapsedTime < 0 ? 0 : elapsedTime;

            double d = (double) elapsedTime / lerpTime;
            double diameter;

            if (d == 0.0) {
                diameter = this.diameterStart;
            } else {
                diameter = this.diameterStart + (this.diameterEnd - this.diameterStart) * d;
            }

            this.diameterStart = diameter;
            setCurrentTime(lerpTime - elapsedTime);
            return diameter;
        } else {
            return this.diameterStart;
        }
    }

    @Override
    public void setDiameter(double diameter) {
        setDiameter(diameter, diameter, Duration.ofMillis(0));
    }

    @Override
    public void setDiameter(double diameter, Duration time) {
        setDiameter(getDiameter(), diameter, time);
    }

    @Override
    public void setDiameter(double startDiameter, double endDiameter, Duration duration) {
        checkArgument(startDiameter >= 0, "The start diameter cannot be negative!");
        checkArgument(endDiameter >= 0, "The end diameter cannot be negative!");
        checkNotNull(duration, "duration");

        final long millis = duration.toMillis();

        // Only shrink or grow if needed
        if (millis == 0 || startDiameter == endDiameter) {
            this.diameterStart = endDiameter;
            this.diameterEnd = endDiameter;
            updateCurrentTime(0);
            broadcast(() -> new MessagePlayOutWorldBorder.UpdateDiameter(endDiameter));
        } else {
            this.diameterStart = startDiameter;
            this.diameterEnd = endDiameter;
            updateCurrentTime(millis);
            broadcast(() -> new MessagePlayOutWorldBorder.UpdateLerpedDiameter(startDiameter, endDiameter, millis));
        }
    }

    long getTimeRemainingMillis() {
        if (this.timeStart == -1) {
            updateCurrentTime();
        }
        return Math.max(this.timeEnd - System.currentTimeMillis(), 0);
    }

    @Override
    public Duration getTimeRemaining() {
        return Duration.ofMillis(getTimeRemainingMillis());
    }

    @Override
    public void setCenter(double x, double z) {
        this.centerX = x;
        this.centerZ = z;
        broadcast(() -> new MessagePlayOutWorldBorder.UpdateCenter(this.centerX, this.centerZ));
    }

    @Override
    public Vector3d getCenter() {
        return new Vector3d(this.centerX, 0, this.centerZ);
    }

    int getWarningTimeSeconds() {
        return (int) this.warningTime.get(TemporalUnits.SECONDS);
    }

    @Override
    public Duration getWarningTime() {
        return this.warningTime;
    }

    @Override
    public void setWarningTime(Duration time) {
        this.warningTime = time;
        broadcast(() -> new MessagePlayOutWorldBorder.UpdateWarningTime((int) time.get(TemporalUnits.SECONDS)));
    }

    @Override
    public double getWarningDistance() {
        return this.warningDistance;
    }

    @Override
    public void setWarningDistance(double distance) {
        this.warningDistance = distance;
        broadcast(() -> new MessagePlayOutWorldBorder.UpdateWarningDistance((int) Math.round(distance)));
    }

    @Override
    public double getDamageThreshold() {
        return this.damageThreshold;
    }

    @Override
    public void setDamageThreshold(double distance) {
        this.damageThreshold = distance;
    }

    @Override
    public double getDamageAmount() {
        return this.damage;
    }

    @Override
    public void setDamageAmount(double damage) {
        this.damage = damage;
    }

    void updateCurrentTime() {
        updateCurrentTime(this.lerpTime);
    }

    private void setCurrentTime(long time) {
        updateCurrentTime(time);
        this.lerpTime = time;
    }

    private void updateCurrentTime(long time) {
        this.timeStart = System.currentTimeMillis();
        this.timeEnd = this.timeStart + time;
    }
}
