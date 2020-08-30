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
package org.lanternpowered.server.inventory.container.layout

import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.server.inventory.container.ClientWindowType
import org.lanternpowered.server.inventory.container.ClientWindowTypes

class RootSmokerContainerLayout : RootSmeltingContainerLayout(TITLE) {

    override val windowType: ClientWindowType get() = ClientWindowTypes.SMOKER

    companion object {

        private val TITLE = translatableTextOf("container.smoker")
    }
}
