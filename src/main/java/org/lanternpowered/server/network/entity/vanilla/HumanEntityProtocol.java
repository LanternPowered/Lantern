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

import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.TabListPacket;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;

import java.util.Collections;
import java.util.Objects;

public class HumanEntityProtocol extends HumanoidEntityProtocol<LanternEntity> {

    private String lastName;

    public HumanEntityProtocol(LanternEntity entity) {
        super(entity);
    }

    @Override
    protected void spawn(EntityProtocolUpdateContext context) {
        spawn(context, this.entity.getTranslation().get());
    }

    private void spawn(EntityProtocolUpdateContext context, String name) {
        final LanternGameProfile gameProfile = new LanternGameProfile(this.entity.getUniqueId(), name);
        final TabListPacket.Entry.Add addEntry = new TabListPacket.Entry.Add(
                gameProfile, GameModes.SURVIVAL, null, 0);
        context.sendToAllExceptSelf(() -> new TabListPacket(Collections.singleton(addEntry)));
        super.spawn(context);
        final TabListPacket.Entry.Remove removeEntry = new TabListPacket.Entry.Remove(gameProfile);
        context.sendToAllExceptSelf(() -> new TabListPacket(Collections.singleton(removeEntry)));
    }

    @Override
    protected void update(EntityProtocolUpdateContext context) {
        final String name = this.entity.getTranslation().get();
        if (!Objects.equals(this.lastName, name)) {
            spawn(context, name);
            update0(EntityProtocolUpdateContext.empty());
            this.lastName = name;
        } else {
            update0(context);
        }
    }

    protected void update0(EntityProtocolUpdateContext context) {
        super.update(context);
    }
}
