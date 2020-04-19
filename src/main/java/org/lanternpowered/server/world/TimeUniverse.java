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
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWorldTime;
import org.spongepowered.api.world.gamerule.GameRules;

public class TimeUniverse {

    public static final int TICKS_IN_A_DAY = 24000;
    private static final int SYNC_DELAY = 6;

    private final LanternWorld world;

    private TimeData timeData;
    private int syncCounter = 0;
    private MoonPhase lastMoonPhase;
    private boolean lastDoDaylightCycle;

    public TimeUniverse(LanternWorld world) {
        this.world = world;
        this.timeData = this.world.getProperties().getTimeData();
        this.lastDoDaylightCycle = this.world.getGameRule(GameRules.DO_DAYLIGHT_CYCLE);
        this.lastMoonPhase = this.timeData.getMoonPhase();
    }

    void pulse() {
        this.timeData.setAge(this.timeData.getAge() + 1);
        final boolean doDaylightCycle = this.world.getGameRule(GameRules.DO_DAYLIGHT_CYCLE);
        long time = this.timeData.getDayTime();
        if (doDaylightCycle) {
            time++;
            this.timeData.setDayTime(time);
        }
        final MoonPhase moonPhase = this.timeData.getMoonPhase();
        if (this.syncCounter-- < 0 || this.lastDoDaylightCycle != doDaylightCycle || this.lastMoonPhase != moonPhase) {
            this.syncCounter = SYNC_DELAY;
            this.lastDoDaylightCycle = doDaylightCycle;
            this.lastMoonPhase = moonPhase;
            final long time1 = time;
            this.world.broadcast(() -> new MessagePlayOutWorldTime(moonPhase, this.timeData.getAge(), (int) time1, doDaylightCycle));
        }
    }

    public MessagePlayOutWorldTime createUpdateTimeMessage() {
        return new MessagePlayOutWorldTime(this.timeData.getMoonPhase(), this.timeData.getAge(), (int) this.timeData.getDayTime(),
                this.world.getGameRule(GameRules.DO_DAYLIGHT_CYCLE));
    }
}
