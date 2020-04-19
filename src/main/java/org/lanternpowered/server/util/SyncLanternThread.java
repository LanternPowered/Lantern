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
package org.lanternpowered.server.util;

/**
 * A thread type that is used for the main lantern threads, this means all the
 * world update threads, the initialization and main thread.
 */
public class SyncLanternThread extends LanternThread {

    public SyncLanternThread(Runnable target, String name) {
        super(target, name);
    }

    public SyncLanternThread(Runnable target) {
        super(target);
    }
}
