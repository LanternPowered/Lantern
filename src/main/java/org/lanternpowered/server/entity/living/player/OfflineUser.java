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
package org.lanternpowered.server.entity.living.player;

import org.lanternpowered.server.data.LocalKeyRegistry;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.vanilla.LanternUserInventory;
import org.lanternpowered.server.inventory.vanilla.VanillaInventoryArchetypes;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.Optional;

public class OfflineUser extends AbstractUser {

    private final LanternUserInventory inventory;

    public OfflineUser(ProxyUser user) {
        super(user);
        this.inventory = VanillaInventoryArchetypes.USER.builder()
                .withCarrier(this).build(Lantern.getMinecraftPlugin());
    }

    @Override
    public void registerKeys() {
        super.registerKeys();

        final LocalKeyRegistry c = getKeyRegistry();
        // A offline can't have a active item, so always return none and reject offers
        c.registerProcessor(Keys.ACTIVE_ITEM).add(builder -> builder
                .valueOfferHandler((valueContainer, value) -> DataTransactionResult.errorResult(value.asImmutable()))
                .retrieveHandler((valueContainer, key) -> Optional.of(ItemStackSnapshot.empty()))
                .failAlwaysRemoveHandler());
    }

    @Override
    public LanternUserInventory getInventory() {
        return this.inventory;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public Optional<Player> getPlayer() {
        return Optional.empty();
    }
}
