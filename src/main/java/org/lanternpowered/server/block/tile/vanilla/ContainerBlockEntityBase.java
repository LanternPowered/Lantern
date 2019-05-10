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

import org.lanternpowered.server.block.tile.LanternBlockEntity;
import org.lanternpowered.server.block.vanilla.container.action.ContainerAnimationAction;
import org.lanternpowered.server.inventory.AbstractContainer;
import org.lanternpowered.server.inventory.InventoryViewerListener;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;

import java.util.HashSet;
import java.util.Set;

public abstract class ContainerBlockEntityBase extends LanternBlockEntity implements InventoryViewerListener {

    protected final Set<Player> viewers = new HashSet<>();

    /**
     * The delay that will be used to play
     * the open/close sounds.
     */
    private int soundDelay;

    @Override
    public void onViewerAdded(Player viewer, AbstractContainer container, Callback callback) {
        if (this.viewers.add(viewer) && this.viewers.size() == 1) {
            this.soundDelay = getOpenSoundDelay();

            final Location location = getLocation();
            final LanternWorld world = (LanternWorld) location.getWorld();
            world.addBlockAction(location.getBlockPosition(), getBlock().getType(), ContainerAnimationAction.OPEN);
        }
    }

    @Override
    public void onViewerRemoved(Player viewer, AbstractContainer container, Callback callback) {
        if (this.viewers.remove(viewer) && this.viewers.size() == 0) {
            this.soundDelay = getCloseSoundDelay();

            final Location location = getLocation();
            final LanternWorld world = (LanternWorld) location.getWorld();
            world.addBlockAction(location.getBlockPosition(), getBlock().getType(), ContainerAnimationAction.CLOSE);
        }
    }

    /**
     * Gets the delay that should be used to
     * play the open sound.
     *
     * @return The open sound delay
     */
    protected int getOpenSoundDelay() {
        return 5;
    }

    /**
     * Gets the delay that should be used to
     * play the open sound.
     *
     * @return The open sound delay
     */
    protected int getCloseSoundDelay() {
        return 10;
    }

    /**
     * Plays the open sound at the {@link Location}.
     *
     * @param location The location
     */
    protected abstract void playOpenSound(Location location);

    /**
     * Plays the close sound at the {@link Location}.
     *
     * @param location The location
     */
    protected abstract void playCloseSound(Location location);

    @Override
    public void pulse() {
        super.pulse();

        if (this.soundDelay > 0 && --this.soundDelay == 0) {
            final Location location = getLocation();
            if (this.viewers.size() > 0) {
                playOpenSound(location);
            } else {
                playCloseSound(location);
            }
        }
    }
}
