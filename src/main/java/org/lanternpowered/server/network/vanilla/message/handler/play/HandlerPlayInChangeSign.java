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
package org.lanternpowered.server.network.vanilla.message.handler.play;

import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChangeSign;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.tileentity.ImmutableSignData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.text.Text;

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
