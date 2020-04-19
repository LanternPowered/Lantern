/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.world;

import org.lanternpowered.server.data.type.MoonPhase;

public class TimeData {

    private long dayTime;
    private long age;
    private MoonPhase moonPhase = MoonPhase.NEW_MOON;

    public MoonPhase getMoonPhase() {
        return this.moonPhase;
    }

    public void setMoonPhase(MoonPhase moonPhase) {
        this.moonPhase = moonPhase;
    }

    public long getDayTime() {
        return this.dayTime;
    }

    public void setDayTime(long dayTime) {
        this.moonPhase = this.moonPhase.rotate((int) (dayTime / TimeUniverse.TICKS_IN_A_DAY));
        this.dayTime = dayTime % TimeUniverse.TICKS_IN_A_DAY;
    }

    public long getAge() {
        return this.age;
    }

    public void setAge(long age) {
        this.age = age;
    }
}
