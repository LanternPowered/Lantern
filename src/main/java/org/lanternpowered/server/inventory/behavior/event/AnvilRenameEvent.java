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
package org.lanternpowered.server.inventory.behavior.event;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.inventory.client.AnvilClientContainer;

/**
 * Will be thrown when a name is being inputted in
 * the {@link AnvilClientContainer}.
 */
public final class AnvilRenameEvent implements ContainerEvent {

    private final String name;

    public AnvilRenameEvent(String name) {
        this.name = checkNotNull(name, "name");
    }

    /**
     * Gets the received name.
     *
     * @return The name
     */
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", this.name)
                .toString();
    }
}
