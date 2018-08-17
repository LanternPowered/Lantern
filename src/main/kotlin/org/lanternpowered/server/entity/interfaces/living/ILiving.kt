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
package org.lanternpowered.server.entity.interfaces.living

import org.lanternpowered.api.ext.*
import org.lanternpowered.server.entity.AbstractArmorEquipable
import org.lanternpowered.server.entity.interfaces.IEntity
import org.lanternpowered.server.entity.interfaces.IMerchantEntity
import org.lanternpowered.api.entity.shard.AIShard
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.ai.GoalType
import org.spongepowered.api.entity.living.Aerial
import org.spongepowered.api.entity.living.Ageable
import org.spongepowered.api.entity.living.Agent
import org.spongepowered.api.entity.living.Ambient
import org.spongepowered.api.entity.living.Aquatic
import org.spongepowered.api.entity.living.ArmorStand
import org.spongepowered.api.entity.living.Bat
import org.spongepowered.api.entity.living.Creature
import org.spongepowered.api.entity.living.Hostile
import org.spongepowered.api.entity.living.Human
import org.spongepowered.api.entity.living.Humanoid
import org.spongepowered.api.entity.living.Living
import org.spongepowered.api.entity.living.Ranger
import org.spongepowered.api.entity.living.Squid
import org.spongepowered.api.entity.living.Villager
import java.util.Optional

interface ILiving : IEntity, Living

interface IAgent : ILiving, Agent {

    override fun getTarget() = getShard<AIShard>().mapIfNotNull { it.target }.optional()

    override fun setTarget(target: Entity?) {
        getShard<AIShard>().ifNotNull { it.target = target }
    }

    override fun <T : Agent> getGoal(type: GoalType) = getShard<AIShard>().mapIfNotNull { it.getGoal<T>(type) } ?: Optional.empty()
}

interface ICreature : IAgent, Creature
interface IRanger : IAgent, Ranger

interface IAgeable : ICreature, Ageable {

    @JvmDefault override fun setScaleForAge() {}
}

interface IAerial : IAgent, Aerial
interface IAmbient : IAgent, Ambient
interface IAquatic : ICreature, Aquatic
interface IHostile : ILiving, Hostile

interface IArmorStand : ILiving, ArmorStand, AbstractArmorEquipable
interface IBat : IAerial, IAmbient, Bat
interface ISquid : IAquatic, Squid
interface IHumanoid : ILiving, Humanoid, AbstractArmorEquipable
interface IHuman : IHumanoid, Human

interface IVillager : IAgeable, IMerchantEntity, Villager {
    @JvmDefault override fun isTrading() = customer.isPresent
}
