/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.entity;

import org.lanternpowered.server.data.LocalKeyRegistry;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.inventory.vanilla.VanillaInventoryArchetypes;
import org.lanternpowered.server.network.entity.EntityProtocolTypes;
import org.lanternpowered.server.profile.LanternProfileProperty;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.Human;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.equipment.EquipmentInventory;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.text.Text;

import java.util.HashSet;
import java.util.UUID;

public class LanternHuman extends LanternAgent implements Human, AbstractArmorEquipable {

    private final EquipmentInventory equipmentInventory;

    public LanternHuman(UUID uniqueId) {
        super(uniqueId);
        setEntityProtocolType(EntityProtocolTypes.HUMAN);
        this.equipmentInventory = VanillaInventoryArchetypes.ENTITY_EQUIPMENT.build();
    }

    @Override
    public void registerKeys() {
        super.registerKeys();

        final LocalKeyRegistry<?> c = getKeyRegistry();
        c.register(LanternKeys.DISPLAYED_SKIN_PARTS, new HashSet<>());
        c.register(LanternKeys.POSE, Pose.STANDING);
        c.register(Keys.SKIN, LanternProfileProperty.EMPTY_TEXTURES);
    }

    @Override
    public String getName() {
        return get(Keys.DISPLAY_NAME).map(Text::toPlain).orElse("Unknown");
    }

    @Override
    public CarriedInventory<? extends Carrier> getInventory() {
        return this.equipmentInventory;
    }
}
