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
package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.game.registry.type.effect.PotionEffectTypeRegistryModule;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInAcceptBeaconEffects;
import org.spongepowered.api.effect.potion.PotionEffectType;

public final class CodecPlayInAcceptBeaconEffects implements Codec<MessagePlayInAcceptBeaconEffects> {

    @Override
    public MessagePlayInAcceptBeaconEffects decode(CodecContext context, ByteBuffer buf) throws CodecException {
        final PotionEffectTypeRegistryModule registryModule = PotionEffectTypeRegistryModule.INSTANCE;
        final PotionEffectType primary = registryModule.getByInternalId(buf.readVarInt()).orElse(null);
        final PotionEffectType secondary = registryModule.getByInternalId(buf.readVarInt()).orElse(null);
        return new MessagePlayInAcceptBeaconEffects(primary, secondary);
    }
}
