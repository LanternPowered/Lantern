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
package org.lanternpowered.server.network.entity.vanilla;

import org.lanternpowered.server.data.type.LanternProfession;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.network.entity.parameter.ParameterList;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.Career;
import org.spongepowered.api.data.type.Professions;

public class VillagerEntityProtocol<E extends LanternEntity> extends AgeableEntityProtocol<E> {

    private int lastProfession;

    public VillagerEntityProtocol(E entity) {
        super(entity);
    }

    private int getProfessionId() {
        return ((LanternProfession) this.entity.get(Keys.CAREER).map(Career::getProfession)
                .orElse(Professions.FARMER)).getInternalId();
    }

    @Override
    protected String getMobType() {
        return "minecraft:villager";
    }

    @Override
    protected void spawn(ParameterList parameterList) {
        super.spawn(parameterList);
        parameterList.add(EntityParameters.Villager.PROFESSION, getProfessionId());
    }

    @Override
    protected void update(ParameterList parameterList) {
        super.update(parameterList);
        final int profession = getProfessionId();
        if (profession != this.lastProfession) {
            parameterList.add(EntityParameters.Villager.PROFESSION, profession);
            this.lastProfession = profession;
        }
    }
}
