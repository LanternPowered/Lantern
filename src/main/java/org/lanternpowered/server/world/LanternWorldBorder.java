package org.lanternpowered.server.world;

import org.spongepowered.api.world.WorldBorder;

import com.flowpowered.math.vector.Vector3d;

public class LanternWorldBorder implements WorldBorder {

    private final LanternWorld world;

    // Whether the first tick has occurred
    private boolean firstTick;

    public LanternWorldBorder(LanternWorld world) {
        this.world = world;
    }

    void pulse() {
        if (!this.firstTick) {
            this.world.getProperties().updateCurrentBorderTime();
            this.firstTick = true;
        }
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
}
