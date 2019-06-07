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
package org.lanternpowered.server.item;

import static com.google.common.base.Preconditions.checkNotNull;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetCooldown;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.CooldownTracker;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.entity.living.humanoid.player.CooldownEvent;
import org.spongepowered.api.item.ItemType;

import java.util.OptionalDouble;
import java.util.OptionalInt;

public class LanternCooldownTracker implements CooldownTracker {

    private final LanternPlayer player;
    private final Object2LongMap<ItemType> map = new Object2LongOpenHashMap<>();

    public LanternCooldownTracker(LanternPlayer player) {
        this.map.defaultReturnValue(-1L);
        this.player = player;
    }

    @Override
    public boolean setCooldown(ItemType itemType, int ticks) {
        checkNotNull(itemType, "itemType");
        final long current = LanternGame.currentTimeTicks();
        long time = this.map.getLong(itemType) - current;
        if (time <= 0 && ticks <= 0) {
            return false;
        }
        final CooldownEvent.Set event = SpongeEventFactory.createCooldownEventSet(CauseStack.current().getCurrentCause(),
                ticks, ticks, itemType, time <= 0 ? OptionalInt.empty() : OptionalInt.of((int) time), this.player);
        Sponge.getEventManager().post(event);
        if (event.isCancelled()) {
            return false;
        }
        ticks = event.getNewCooldown();
        if (ticks > 0) {
            this.map.put(itemType, current + ticks);
        } else if (time > 0) {
            this.map.removeLong(itemType);
            ticks = 0;
        } else {
            ticks = -1;
        }
        if (ticks >= 0) {
            this.player.getConnection().send(new MessagePlayOutSetCooldown(itemType, ticks));
        }
        return true;
    }

    @Override
    public boolean resetCooldown(ItemType itemType) {
        return setCooldown(itemType, 0);
    }

    @Override
    public OptionalInt getCooldown(ItemType itemType) {
        checkNotNull(itemType, "itemType");
        final long time = this.map.getLong(itemType);
        if (time != -1L) {
            final long current = LanternGame.currentTimeTicks();
            if (time > current) {
                return OptionalInt.of((int) (time - current));
            }
        }
        return OptionalInt.empty();
    }

    @Override
    public boolean hasCooldown(ItemType itemType) {
        checkNotNull(itemType, "itemType");
        final long time = this.map.getLong(itemType);
        if (time != -1L) {
            final long current = LanternGame.currentTimeTicks();
            if (time > current) {
                return true;
            }
        }
        return false;
    }

    @Override
    public OptionalDouble getFractionRemaining(ItemType type) {
        // TODO: Properly implement this
        return hasCooldown(type) ? OptionalDouble.of(1.0) : OptionalDouble.empty();
    }

    public void process() {
        final long current = LanternGame.currentTimeTicks();
        this.map.object2LongEntrySet().removeIf(entry -> {
            if (entry.getLongValue() < current) {
                SpongeEventFactory.createCooldownEventEnd(CauseStack.current().getCurrentCause(),
                        entry.getKey(), this.player);
                return true;
            }
            return false;
        });
    }
}
