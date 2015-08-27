package org.lanternpowered.server.block;

import org.lanternpowered.server.game.LanternGame;

import org.spongepowered.api.block.ScheduledBlockUpdate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class LanternScheduledBlockUpdate implements ScheduledBlockUpdate, Comparable<LanternScheduledBlockUpdate> {

    private final Location<World> location;

    private long endTicks;
    private int priority;
    private int entryId;

    public LanternScheduledBlockUpdate(int entryId, Location<World> location, int ticks, int priority) {
        this.setTicks(ticks);
        this.priority = priority;
        this.location = location;
        this.entryId = entryId;
    }

    @Override
    public Location<World> getLocation() {
        return this.location;
    }

    @Override
    public int getTicks() {
        return (int) (this.endTicks - LanternGame.currentTimeTicks());
    }

    @Override
    public void setTicks(int ticks) {
        this.endTicks = LanternGame.currentTimeTicks() + ticks;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int compareTo(LanternScheduledBlockUpdate o) {
        if (this.endTicks < o.endTicks) {
            return -1;
        }
        if (this.endTicks > o.endTicks) {
            return 1;
        }
        if (this.priority != o.priority) {
            return this.priority - o.priority;
        }
        if (this.entryId < o.entryId) {
            return -1;
        }
        if (this.entryId > o.entryId) {
            return 1;
        }
        return 0;
    }
}
