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
package org.lanternpowered.server.game.registry.type.util;

import org.lanternpowered.server.config.user.ban.LanternBanType;
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;
import org.lanternpowered.server.game.registry.EarlyRegistration;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.BanType;
import org.spongepowered.api.util.ban.BanTypes;

public final class BanTypeRegistryModule extends DefaultCatalogRegistryModule<BanType> {

    public BanTypeRegistryModule() {
        super(BanTypes.class);
    }

    @EarlyRegistration
    @Override
    public void registerDefaults() {
        register(new LanternBanType(CatalogKey.minecraft("profile"), Ban.Profile.class));
        register(new LanternBanType(CatalogKey.minecraft("ip"), Ban.Ip.class));
    }
}
