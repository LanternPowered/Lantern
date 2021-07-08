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
package org.lanternpowered.server.entity.interfaces.living.animal

import org.lanternpowered.server.entity.interfaces.living.IAgeable
import org.spongepowered.api.entity.living.animal.Animal
import org.spongepowered.api.entity.living.animal.Chicken
import org.spongepowered.api.entity.living.animal.Cow
import org.spongepowered.api.entity.living.animal.Donkey
import org.spongepowered.api.entity.living.animal.Horse
import org.spongepowered.api.entity.living.animal.Llama
import org.spongepowered.api.entity.living.animal.Mooshroom
import org.spongepowered.api.entity.living.animal.Mule
import org.spongepowered.api.entity.living.animal.Ocelot
import org.spongepowered.api.entity.living.animal.Parrot
import org.spongepowered.api.entity.living.animal.Pig
import org.spongepowered.api.entity.living.animal.PolarBear
import org.spongepowered.api.entity.living.animal.Rabbit
import org.spongepowered.api.entity.living.animal.RideableHorse
import org.spongepowered.api.entity.living.animal.Sheep
import org.spongepowered.api.entity.living.animal.SkeletonHorse
import org.spongepowered.api.entity.living.animal.Wolf
import org.spongepowered.api.entity.living.animal.ZombieHorse

interface IAnimal : IAgeable, Animal

interface IChicken : IAnimal, Chicken
interface IOcelot : IAnimal, Ocelot
interface IParrot : IAnimal, Parrot
interface IPig : IAnimal, Pig
interface IPolarBear : IAnimal, PolarBear
interface IRabbit : IAnimal, Rabbit
interface ISheep : IAnimal, Sheep
interface ISkeletonHorse : IHorseBase, SkeletonHorse
interface IWolf : IAnimal, Wolf
interface IZombieHorse : IHorseBase, ZombieHorse

interface ICow : IAnimal, Cow
interface IMooshroom : ICow, Mooshroom

interface IHorseBase : IAnimal, Horse

interface ILlama : IHorseBase, Llama
interface IHorse : IHorseBase, RideableHorse
interface IDonkey : IHorseBase, Donkey
interface IMule : IHorseBase, Mule
