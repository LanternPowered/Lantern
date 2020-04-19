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

import static org.lanternpowered.server.inject.provider.ProviderHelper.provideNameOrFail;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lanternpowered.api.inject.InjectionPoint;

public class NamedLog4jLoggerProvider implements Provider<Logger> {

    @Inject private InjectionPoint point;

    @Override
    public Logger get() {
        return LogManager.getLogger(provideNameOrFail(this.point));
    }
}
