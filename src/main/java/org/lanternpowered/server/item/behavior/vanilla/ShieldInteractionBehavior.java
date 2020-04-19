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
package org.lanternpowered.server.item.behavior.vanilla;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.item.behavior.types.InteractWithItemBehavior;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public class ShieldInteractionBehavior implements InteractWithItemBehavior {

    @Override
    public BehaviorResult tryInteract(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final Optional<Player> optPlayer = context.getContext(ContextKeys.PLAYER);
        if (optPlayer.isPresent()) {
            optPlayer.get().offer(LanternKeys.ACTIVE_HAND, Optional.of(context.requireContext(ContextKeys.INTERACTION_HAND)));
            return BehaviorResult.SUCCESS;
        }
        return BehaviorResult.PASS;
    }
}
