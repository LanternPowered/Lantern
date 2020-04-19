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
