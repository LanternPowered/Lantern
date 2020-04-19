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
package org.lanternpowered.server.block.entity.vanilla;

import org.lanternpowered.server.inventory.AbstractContainer;
import org.lanternpowered.server.network.block.BlockEntityProtocolTypes;
import org.spongepowered.api.block.entity.EnderChest;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;

import java.util.concurrent.ThreadLocalRandom;

public class LanternEnderChest extends ContainerBlockEntityBase implements EnderChest {

    public LanternEnderChest() {
        setProtocolType(BlockEntityProtocolTypes.DEFAULT);
    }

    @Override
    public void onViewerRemoved(Player viewer, AbstractContainer container, Callback callback) {
        super.onViewerRemoved(viewer, container, callback);
        callback.remove();
    }

    @Override
    protected void playOpenSound(Location location) {
        location.getWorld().playSound(SoundTypes.BLOCK_ENDER_CHEST_OPEN, SoundCategories.BLOCK,
                location.getPosition().add(0.5, 0.5, 0.5), 0.5, ThreadLocalRandom.current().nextDouble() * 0.1 + 0.9);
    }

    @Override
    protected void playCloseSound(Location location) {
        location.getWorld().playSound(SoundTypes.BLOCK_ENDER_CHEST_CLOSE, SoundCategories.BLOCK,
                location.getPosition().add(0.5, 0.5, 0.5), 0.5, ThreadLocalRandom.current().nextDouble() * 0.1 + 0.9);
    }
}
