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
package org.lanternpowered.server.entity.living.player;

import org.lanternpowered.server.data.ValueCollection;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.vanilla.LanternUserInventory;
import org.lanternpowered.server.inventory.vanilla.VanillaInventoryArchetypes;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Keys;
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

        final ValueCollection c = getValueCollection();
        // A offline can't have a active item, so always return none and reject offers
        c.registerProcessor(Keys.ACTIVE_ITEM).add(builder -> builder
                .valueOfferHandler((valueContainer, value) -> DataTransactionResult.errorResult(value.asImmutable()))
                .retrieveHandler((valueContainer, key) -> Optional.of(ItemStackSnapshot.NONE))
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
