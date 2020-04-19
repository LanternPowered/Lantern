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
package org.lanternpowered.server.block;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.math.vector.Vector3i;

public class LanternBlockSnapshotBuilder extends BlockSnapshotBuilder {

    @Override
    public BlockSnapshotBuilder world(WorldProperties worldProperties) {
        return super.world(checkNotNull(worldProperties, "worldProperties"));
    }

    @Override
    public BlockSnapshotBuilder position(Vector3i position) {
        return super.position(checkNotNull(position, "position"));
    }

    @Override
    public BlockSnapshotBuilder from(Location location) {
        return super.from(checkNotNull(location, "location"));
    }

    @Override
    public BlockSnapshot build() {
        checkState(this.position != null, "The position must be set.");
        checkState(this.worldUUID != null, "The world must be set.");
        return super.build();
    }
}
