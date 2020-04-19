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
package org.lanternpowered.server.item.behavior.vanilla.consumable;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.item.behavior.vanilla.ConsumableInteractionBehavior;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;

public class MilkConsumer implements ConsumableInteractionBehavior.Consumer {

    @Override
    public void apply(Player player, BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        player.offer(Keys.POTION_EFFECTS, new ArrayList<>());
    }
}
