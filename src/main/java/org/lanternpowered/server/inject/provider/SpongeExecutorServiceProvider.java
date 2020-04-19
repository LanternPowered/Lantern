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
package org.lanternpowered.server.inject.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.TaskExecutorService;

public abstract class SpongeExecutorServiceProvider implements Provider<TaskExecutorService> {

    @Inject protected Scheduler scheduler;
    @Inject protected PluginContainer container;

    public static final class Synchronous extends SpongeExecutorServiceProvider {

        @Override
        public TaskExecutorService get() {
            return this.scheduler.createSyncExecutor(this.container);
        }
    }

    public static final class Asynchronous extends SpongeExecutorServiceProvider {

        @Override
        public TaskExecutorService get() {
            return this.scheduler.createAsyncExecutor(this.container);
        }
    }
}
