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

import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChangeSign;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.manipulator.immutable.ImmutableSignData;
import org.spongepowered.api.data.manipulator.mutable.SignData;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.text.Text;
import org.spongepowered.math.vector.Vector3i;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public final class HandlerPlayInChangeSign implements Handler<MessagePlayInChangeSign> {

    @Override
    public void handle(NetworkContext context, MessagePlayInChangeSign message) {
        final LanternPlayer player = context.getSession().getPlayer();
        final Optional<Vector3i> openedSignPos = player.getOpenedSignPosition();
        final Vector3i signPos = message.getPosition();
        player.resetOpenedSignPosition();
        if (!openedSignPos.isPresent() || !openedSignPos.get().equals(signPos)) {
            return;
        }
        final Optional<TileEntity> optTileEntity = player.getWorld().getTileEntity(signPos);
        optTileEntity
                .filter(tile -> tile instanceof Sign)
                .ifPresent(tile -> {
                    final Sign sign = (Sign) tile;
                    final SignData signData = sign.getSignData();
                    final ImmutableSignData originalSignData = signData.asImmutable();
                    signData.set(Keys.SIGN_LINES, Arrays.stream(message.getLines())
                            .<Text>map(Text::of)
                            .collect(Collectors.toList()));
                    try (CauseStack.Frame frame = CauseStack.current().pushCauseFrame()) {
                        frame.pushCause(player);
                        frame.addContext(EventContextKeys.PLAYER, player);
                        final ChangeSignEvent event = SpongeEventFactory.createChangeSignEvent(
                                frame.getCurrentCause(), originalSignData, signData, sign);
                        Sponge.getEventManager().post(event);
                        if (event.isCancelled()) {
                            return;
                        }
                        sign.offer(signData);
                    }
                });
    }
}
