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
package org.lanternpowered.server.inventory.vanilla;

import org.lanternpowered.server.inventory.ICarriedInventory;
import org.lanternpowered.server.inventory.IEquipmentInventory;
import org.lanternpowered.server.inventory.type.LanternInventoryColumn;
import org.spongepowered.api.entity.Equipable;

public class LanternPlayerArmorInventory extends LanternInventoryColumn implements IEquipmentInventory, ICarriedInventory<Equipable> {

}
