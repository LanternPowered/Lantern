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
package org.lanternpowered.server.config.user;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import ninja.leaping.configurate.objectmapping.Setting;
import org.lanternpowered.server.game.DirectoryKeys;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Singleton
public final class OpsConfig extends UserConfig<OpsEntry> {

    // The name of the ops config file
    private static final String FILE_NAME = "ops.json";

    @Setting(value = "entries")
    private List<OpsEntry> entries = new ArrayList<>();

    @Inject
    public OpsConfig(@Named(DirectoryKeys.CONFIG) Path configFolder) throws IOException {
        super(configFolder.resolve(FILE_NAME), false);
        load();
    }

    @Override
    protected List<OpsEntry> getBackingList() {
        return this.entries;
    }
}
