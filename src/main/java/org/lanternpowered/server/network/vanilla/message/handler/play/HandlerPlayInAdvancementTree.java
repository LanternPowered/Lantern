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
package org.lanternpowered.server.network.vanilla.message.handler.play;

import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.game.registry.type.advancement.AdvancementTreeRegistryModule;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInAdvancementTree;
import org.spongepowered.api.CatalogKey;

public final class HandlerPlayInAdvancementTree implements Handler<MessagePlayInAdvancementTree> {

    @Override
    public void handle(NetworkContext context, MessagePlayInAdvancementTree message) {
        if (message instanceof MessagePlayInAdvancementTree.Open) {
            final String id = ((MessagePlayInAdvancementTree.Open) message).getId();
            context.getSession().getPlayer().offer(LanternKeys.OPEN_ADVANCEMENT_TREE,
                    AdvancementTreeRegistryModule.get().get(CatalogKey.resolve(id)));
        } else {
            // Do we need the close event?
        }
    }
}
