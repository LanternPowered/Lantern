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
package org.lanternpowered.server.event;

import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;

public interface LanternEventListener<T extends Event> extends EventListener<T> {

    Object getHandle();
}
