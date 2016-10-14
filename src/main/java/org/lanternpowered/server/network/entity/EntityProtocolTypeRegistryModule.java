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
package org.lanternpowered.server.network.entity;

import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.game.registry.PluginCatalogRegistryModule;
import org.lanternpowered.server.network.entity.vanilla.ChickenEntityProtocol;
import org.lanternpowered.server.network.entity.vanilla.EnderDragonEntityProtocol;
import org.lanternpowered.server.network.entity.vanilla.EndermiteEntityProtocol;
import org.lanternpowered.server.network.entity.vanilla.ExperienceOrbEntityProtocol;
import org.lanternpowered.server.network.entity.vanilla.GiantEntityProtocol;
import org.lanternpowered.server.network.entity.vanilla.HumanEntityProtocol;
import org.lanternpowered.server.network.entity.vanilla.HuskEntityProtocol;
import org.lanternpowered.server.network.entity.vanilla.ItemEntityProtocol;
import org.lanternpowered.server.network.entity.vanilla.LightningEntityProtocol;
import org.lanternpowered.server.network.entity.vanilla.MagmaCubeEntityProtocol;
import org.lanternpowered.server.network.entity.vanilla.PaintingEntityProtocol;
import org.lanternpowered.server.network.entity.vanilla.PlayerEntityProtocol;
import org.lanternpowered.server.network.entity.vanilla.RabbitEntityProtocol;
import org.lanternpowered.server.network.entity.vanilla.SilverfishEntityProtocol;
import org.lanternpowered.server.network.entity.vanilla.SlimeEntityProtocol;
import org.lanternpowered.server.network.entity.vanilla.VillagerEntityProtocol;
import org.lanternpowered.server.network.entity.vanilla.ZombieEntityProtocol;
import org.lanternpowered.server.network.entity.vanilla.ZombieVillagerEntityProtocol;

public class EntityProtocolTypeRegistryModule extends PluginCatalogRegistryModule<EntityProtocolType> {

    public EntityProtocolTypeRegistryModule() {
        super(EntityProtocolTypes.class);
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public void registerDefaults() {
        // Now you are probably thinking, use the method reference: ChickenEntityProtocol::new ??
        // well it's not working, at least not outside the development environment, java is throwing
        // "no such constructor" exceptions...
        // Tested with: oracle jre1.8.0_101
        this.register(LanternEntityProtocolType.of("minecraft", "chicken", LanternEntity.class,
                entity -> new ChickenEntityProtocol<>(entity)));
        this.register(LanternEntityProtocolType.of("minecraft", "ender_dragon", LanternEntity.class,
                entity -> new EnderDragonEntityProtocol<>(entity)));
        this.register(LanternEntityProtocolType.of("minecraft", "endermite", LanternEntity.class,
                entity -> new EndermiteEntityProtocol<>(entity)));
        this.register(LanternEntityProtocolType.of("minecraft", "experience_orb", LanternEntity.class,
                entity -> new ExperienceOrbEntityProtocol<>(entity)));
        this.register(LanternEntityProtocolType.of("minecraft", "giant", LanternEntity.class,
                entity -> new GiantEntityProtocol<>(entity)));
        this.register(LanternEntityProtocolType.of("minecraft", "human", LanternEntity.class,
                entity -> new HumanEntityProtocol(entity)));
        this.register(LanternEntityProtocolType.of("minecraft", "husk", LanternEntity.class,
                entity -> new HuskEntityProtocol<>(entity)));
        this.register(LanternEntityProtocolType.of("minecraft", "item", LanternEntity.class,
                entity -> new ItemEntityProtocol<>(entity)));
        this.register(LanternEntityProtocolType.of("minecraft", "lightning", LanternEntity.class,
                entity -> new LightningEntityProtocol<>(entity)));
        this.register(LanternEntityProtocolType.of("minecraft", "magma_cube", LanternEntity.class,
                entity -> new MagmaCubeEntityProtocol<>(entity)));
        this.register(LanternEntityProtocolType.of("minecraft", "painting", LanternEntity.class,
                entity -> new PaintingEntityProtocol<>(entity)));
        this.register(LanternEntityProtocolType.of("minecraft", "player", LanternPlayer.class,
                entity -> new PlayerEntityProtocol(entity)));
        this.register(LanternEntityProtocolType.of("minecraft", "rabbit", LanternEntity.class,
                entity -> new RabbitEntityProtocol<>(entity)));
        this.register(LanternEntityProtocolType.of("minecraft", "silverfish", LanternEntity.class,
                entity -> new SilverfishEntityProtocol<>(entity)));
        this.register(LanternEntityProtocolType.of("minecraft", "slime", LanternEntity.class,
                entity -> new SlimeEntityProtocol<>(entity)));
        this.register(LanternEntityProtocolType.of("minecraft", "villager", LanternEntity.class,
                entity -> new VillagerEntityProtocol<>(entity)));
        this.register(LanternEntityProtocolType.of("minecraft", "zombie", LanternEntity.class,
                entity -> new ZombieEntityProtocol<>(entity)));
        this.register(LanternEntityProtocolType.of("minecraft", "zombie_villager", LanternEntity.class,
                entity -> new ZombieVillagerEntityProtocol<>(entity)));
    }
}
