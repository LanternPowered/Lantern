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
package org.lanternpowered.server.item.recipe.fuel;

import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.Collection;
import java.util.Optional;
import java.util.OptionalInt;

public interface IFuelRegistry {

    <A extends IFuel> A register(A fuel);

    Collection<IFuel> getAll();

    Optional<IFuel> findMatching(ItemStackSnapshot ingredient);

    OptionalInt getResult(ItemStackSnapshot ingredient);
}
