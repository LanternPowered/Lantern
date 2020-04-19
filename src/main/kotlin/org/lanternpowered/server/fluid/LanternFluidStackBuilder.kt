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
package org.lanternpowered.server.fluid

import org.spongepowered.api.fluid.FluidStack

class LanternFluidStackBuilder : AbstractFluidStackBuilder<FluidStack, FluidStack.Builder>(FluidStack::class.java), FluidStack.Builder {

    override fun build() = buildStack()
}
