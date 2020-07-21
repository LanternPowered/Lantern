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
package org.lanternpowered.server.network.vanilla.packet.type.play;

import org.lanternpowered.server.network.packet.Packet;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlot;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class PacketPlayOutScoreboardDisplayObjective implements Packet {

    @Nullable private final String objectiveName;
    private final DisplaySlot displaySlot;

    public PacketPlayOutScoreboardDisplayObjective(@Nullable String objectiveName, DisplaySlot displaySlot) {
        this.objectiveName = objectiveName;
        this.displaySlot = displaySlot;
    }

    @Nullable
    public String getObjectiveName() {
        return this.objectiveName;
    }

    public DisplaySlot getDisplaySlot() {
        return this.displaySlot;
    }
}
