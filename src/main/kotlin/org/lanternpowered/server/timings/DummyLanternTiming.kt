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
package org.lanternpowered.server.timings

import co.aikar.timings.Timing

object DummyLanternTiming : Timing {
    override fun startTiming(): Timing = this
    override fun stopTiming() {}
    override fun startTimingIfSync() {}
    override fun stopTimingIfSync() {}
    override fun abort() {}
    override fun close() {}
}
