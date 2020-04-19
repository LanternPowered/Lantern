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

import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.ai.Goal;
import org.spongepowered.api.entity.ai.GoalType;
import org.spongepowered.api.entity.living.Agent;

import java.util.Optional;
import java.util.UUID;

public abstract class LanternAgent extends LanternLiving implements Agent {

    protected LanternAgent(UUID uniqueId) {
        super(uniqueId);
    }

    @Override
    public void registerKeys() {
        super.registerKeys();
        getKeyRegistry().register(Keys.TARGET_ENTITY, Optional.empty());
    }

    @Override
    public <T extends Agent> Optional<Goal<T>> getGoal(GoalType type) {
        return Optional.empty();
    }
}
