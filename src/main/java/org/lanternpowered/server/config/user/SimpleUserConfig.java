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

import ninja.leaping.configurate.objectmapping.Setting;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SimpleUserConfig extends UserConfig<UserEntry> {

    @Setting(value = "entries")
    private List<UserEntry> entries = new ArrayList<>();

    public SimpleUserConfig(Path path, boolean hocon) throws IOException {
        super(path, hocon);
    }

    @Override
    protected List<UserEntry> getBackingList() {
        return this.entries;
    }
}
