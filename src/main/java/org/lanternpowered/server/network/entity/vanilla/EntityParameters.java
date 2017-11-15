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
package org.lanternpowered.server.network.entity.vanilla;

import com.flowpowered.math.vector.Vector3f;
import org.lanternpowered.server.network.entity.parameter.ParameterType;
import org.lanternpowered.server.network.entity.parameter.ParameterTypeCollection;
import org.lanternpowered.server.network.entity.parameter.ParameterValueTypes;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.Optional;
import java.util.UUID;

public final class EntityParameters {

    public static final class Base {

        public static final ParameterTypeCollection PARAMETERS = new ParameterTypeCollection();

        /**
         * Bit mask Meaning
         * 0x01     On Fire
         * 0x02     Crouched
         * 0x08     Sprinting
         * 0x10     Eating/drinking/blocking
         * 0x20     Invisible
         * 0x40     Glowing effect
         * 0x80     Flying with elytra
         */
        public static final ParameterType<Byte> FLAGS = PARAMETERS.newParameterType(ParameterValueTypes.BYTE);

        /**
         * The air level of the entity.
         */
        public static final ParameterType<Integer> AIR_LEVEL = PARAMETERS.newParameterType(ParameterValueTypes.INTEGER);

        /**
         * The custom name of the entity.
         */
        public static final ParameterType<Optional<Text>> CUSTOM_NAME = PARAMETERS.newParameterType(ParameterValueTypes.OPTIONAL_TEXT);

        /**
         * Whether the custom name is always visible.
         */
        public static final ParameterType<Boolean> CUSTOM_NAME_VISIBLE = PARAMETERS.newParameterType(ParameterValueTypes.BOOLEAN);

        /**
         * Whether the entity is silent.
         */
        public static final ParameterType<Boolean> IS_SILENT = PARAMETERS.newParameterType(ParameterValueTypes.BOOLEAN);

        /**
         * Whether the entity has no gravity.
         */
        public static final ParameterType<Boolean> NO_GRAVITY = PARAMETERS.newParameterType(ParameterValueTypes.BOOLEAN);

        private Base() {
        }
    }

    public static final class Living {

        public static final ParameterTypeCollection PARAMETERS = Base.PARAMETERS.copy();

        /**
         * Bit mask Meaning
         * 0x01     Is hand active
         * 0x02     Active hand (0 = main hand, 1 = offhand)
         */
        public static final ParameterType<Byte> HAND_DATA = PARAMETERS.newParameterType(ParameterValueTypes.BYTE);

        /**
         * The health of the entity.
         */
        public static final ParameterType<Float> HEALTH = PARAMETERS.newParameterType(ParameterValueTypes.FLOAT);

        /**
         * The potion effect color of the particles that spawn around the player.
         */
        public static final ParameterType<Integer> POTION_EFFECT_COLOR = PARAMETERS.newParameterType(ParameterValueTypes.INTEGER);

        /**
         * Whether the potion effect particles are ambient.
         */
        public static final ParameterType<Boolean> POTION_EFFECT_AMBIENT = PARAMETERS.newParameterType(ParameterValueTypes.BOOLEAN);

        /**
         * The amount of arrows that are in the entity.
         */
        public static final ParameterType<Integer> ARROWS_IN_ENTITY = PARAMETERS.newParameterType(ParameterValueTypes.INTEGER);

        private Living() {
        }
    }

    public static final class ArmorStand {

        public static final ParameterTypeCollection PARAMETERS = Living.PARAMETERS.copy();

        /**
         * Bit mask Meaning
         * 0x01	    Small
         * 0x04	    Arms
         * 0x08	    No base plate
         * 0x10	    Marker
         */
        public static final ParameterType<Byte> FLAGS = PARAMETERS.newParameterType(ParameterValueTypes.BYTE);

        public static final ParameterType<Vector3f> HEAD_ROTATION = PARAMETERS.newParameterType(ParameterValueTypes.VECTOR_3F);

        public static final ParameterType<Vector3f> BODY_ROTATION = PARAMETERS.newParameterType(ParameterValueTypes.VECTOR_3F);

        public static final ParameterType<Vector3f> LEFT_ARM_ROTATION = PARAMETERS.newParameterType(ParameterValueTypes.VECTOR_3F);

        public static final ParameterType<Vector3f> RIGHT_ARM_ROTATION = PARAMETERS.newParameterType(ParameterValueTypes.VECTOR_3F);

        public static final ParameterType<Vector3f> LEFT_LEG_ROTATION = PARAMETERS.newParameterType(ParameterValueTypes.VECTOR_3F);

        public static final ParameterType<Vector3f> RIGHT_LEG_ROTATION = PARAMETERS.newParameterType(ParameterValueTypes.VECTOR_3F);

        private ArmorStand() {
        }
    }

    public static final class Insentient {

        public static final ParameterTypeCollection PARAMETERS = Living.PARAMETERS.copy();

        /**
         * Bit mask Meaning
         * 0x01     NoAI
         * 0x02     Left handed
         */
        public static final ParameterType<Byte> FLAGS = PARAMETERS.newParameterType(ParameterValueTypes.BYTE);

        private Insentient() {
        }
    }

    public static final class Humanoid {

        public static final ParameterTypeCollection PARAMETERS = Living.PARAMETERS.copy();

        /**
         * Additional yellow hearts.
         */
        public static final ParameterType<Float> ADDITIONAL_HEARTS = PARAMETERS.newParameterType(ParameterValueTypes.FLOAT);

        /**
         * The score of the player. This is displayed on the respawn screen.
         */
        public static final ParameterType<Integer> SCORE = PARAMETERS.newParameterType(ParameterValueTypes.INTEGER);

        /**
         * The displayed skin parts.
         */
        public static final ParameterType<Byte> SKIN_PARTS = PARAMETERS.newParameterType(ParameterValueTypes.BYTE);

        /**
         * The main hand. (0: Left, 1: Right)
         */
        public static final ParameterType<Byte> MAIN_HAND = PARAMETERS.newParameterType(ParameterValueTypes.BYTE);

        /**
         * Th entity on the left shoulder.
         */
        public static final ParameterType<Optional<DataView>> LEFT_SHOULDER_ENTITY = PARAMETERS.newParameterType(ParameterValueTypes.NBT_TAG);

        /**
         * Th entity on the right shoulder.
         */
        public static final ParameterType<Optional<DataView>> RIGHT_SHOULDER_ENTITY = PARAMETERS.newParameterType(ParameterValueTypes.NBT_TAG);

        private Humanoid() {
        }
    }

    public static final class AbstractSlime {

        public static final ParameterTypeCollection PARAMETERS = Insentient.PARAMETERS.copy();

        /**
         * The size of the slime.
         */
        public static final ParameterType<Integer> SIZE = PARAMETERS.newParameterType(ParameterValueTypes.INTEGER);

        private AbstractSlime() {
        }
    }

    public static final class Slime {

        public static final ParameterTypeCollection PARAMETERS = AbstractSlime.PARAMETERS.copy();

        private Slime() {
        }
    }

    public static final class MagmaCube {

        public static final ParameterTypeCollection PARAMETERS = AbstractSlime.PARAMETERS.copy();

        private MagmaCube() {
        }
    }

    public static final class Bat {

        public static final ParameterTypeCollection PARAMETERS = Insentient.PARAMETERS.copy();

        /**
         * Bit mask Meaning
         * 0x01	    Is hanging
         */
        public static final ParameterType<Byte> FLAGS = PARAMETERS.newParameterType(ParameterValueTypes.BYTE);

        private Bat() {
        }
    }

    public static final class Ageable {

        public static final ParameterTypeCollection PARAMETERS = Insentient.PARAMETERS.copy();

        /**
         * Whether the entity is a baby.
         */
        public static final ParameterType<Boolean> IS_BABY = PARAMETERS.newParameterType(ParameterValueTypes.BOOLEAN);

        private Ageable() {
        }
    }

    public static final class AbstractHorse {

        public static final ParameterTypeCollection PARAMETERS = Ageable.PARAMETERS.copy();

        /**
         * Bit mask	Meaning
         * 0x01     Unused
         * 0x02     Is Tame
         * 0x04     Is saddled
         * 0x08     Has Chest
         * 0x10     Is Bred
         * 0x20     Is eating
         * 0x40     Is rearing
         * 0x80     Is mouth open
         */
        public static final ParameterType<Byte> FLAGS = PARAMETERS.newParameterType(ParameterValueTypes.BYTE);

        /**
         * The owner of the horse.
         */
        public static final ParameterType<Optional<UUID>> OWNER = PARAMETERS.newParameterType(ParameterValueTypes.OPTIONAL_UUID);

        private AbstractHorse() {
        }
    }

    public static final class Horse {

        public static final ParameterTypeCollection PARAMETERS = AbstractHorse.PARAMETERS.copy();

        public static final ParameterType<Integer> VARIANT = PARAMETERS.newParameterType(ParameterValueTypes.INTEGER);

        public static final ParameterType<Integer> ARMOR = PARAMETERS.newParameterType(ParameterValueTypes.INTEGER);

        private Horse() {
        }
    }

    public static final class ChestedHorse {

        public static final ParameterTypeCollection PARAMETERS = AbstractHorse.PARAMETERS.copy();

        public static final ParameterType<Boolean> HAS_CHEST = PARAMETERS.newParameterType(ParameterValueTypes.BOOLEAN);

        private ChestedHorse() {
        }
    }

    public static final class ZombieHorse {

        public static final ParameterTypeCollection PARAMETERS = AbstractHorse.PARAMETERS.copy();

        private ZombieHorse() {
        }
    }

    public static final class SkeletonHorse {

        public static final ParameterTypeCollection PARAMETERS = AbstractHorse.PARAMETERS.copy();

        private SkeletonHorse() {
        }
    }

    public static final class Donkey {

        public static final ParameterTypeCollection PARAMETERS = ChestedHorse.PARAMETERS.copy();

        private Donkey() {
        }
    }

    public static final class Mule {

        public static final ParameterTypeCollection PARAMETERS = ChestedHorse.PARAMETERS.copy();

        private Mule() {
        }
    }

    public static final class AbstractZombie {

        public static final ParameterTypeCollection PARAMETERS = Ageable.PARAMETERS.copy();

        public static final ParameterType<Integer> UNUSED = PARAMETERS.newParameterType(ParameterValueTypes.INTEGER);

        public static final ParameterType<Boolean> HANDS_UP = PARAMETERS.newParameterType(ParameterValueTypes.BOOLEAN);

        private AbstractZombie() {
        }
    }

    public static final class Zombie {

        public static final ParameterTypeCollection PARAMETERS = AbstractZombie.PARAMETERS.copy();

        private Zombie() {
        }
    }

    public static final class ZombieVillager {

        public static final ParameterTypeCollection PARAMETERS = AbstractZombie.PARAMETERS.copy();

        public static final ParameterType<Boolean> IS_CONVERTING = PARAMETERS.newParameterType(ParameterValueTypes.BOOLEAN);

        public static final ParameterType<Integer> PROFESSION = PARAMETERS.newParameterType(ParameterValueTypes.INTEGER);

        private ZombieVillager() {
        }
    }

    public static final class Husk {

        public static final ParameterTypeCollection PARAMETERS = AbstractZombie.PARAMETERS.copy();

        private Husk() {
        }
    }

    public static final class AbstractSkeleton {

        public static final ParameterTypeCollection PARAMETERS = Insentient.PARAMETERS.copy();

        public static final ParameterType<Boolean> IS_SWINGING_ARMS = PARAMETERS.newParameterType(ParameterValueTypes.BOOLEAN);

        private AbstractSkeleton() {
        }
    }

    public static final class Skeleton {

        public static final ParameterTypeCollection PARAMETERS = AbstractSkeleton.PARAMETERS.copy();

        private Skeleton() {
        }
    }

    public static final class WitherSkeleton {

        public static final ParameterTypeCollection PARAMETERS = AbstractSkeleton.PARAMETERS.copy();

        private WitherSkeleton() {
        }
    }

    public static final class Stray {

        public static final ParameterTypeCollection PARAMETERS = AbstractSkeleton.PARAMETERS.copy();

        private Stray() {
        }
    }

    public static final class AbstractGuardian {

        public static final ParameterTypeCollection PARAMETERS = Insentient.PARAMETERS.copy();

        public static final ParameterType<Boolean> IS_RETRACTING_SPIKES = PARAMETERS.newParameterType(ParameterValueTypes.BOOLEAN);

        public static final ParameterType<Integer> TARGET = PARAMETERS.newParameterType(ParameterValueTypes.INTEGER);

        private AbstractGuardian() {
        }
    }

    public static final class Guardian {

        public static final ParameterTypeCollection PARAMETERS = AbstractGuardian.PARAMETERS.copy();

        private Guardian() {
        }
    }

    public static final class ElderGuardian {

        public static final ParameterTypeCollection PARAMETERS = AbstractGuardian.PARAMETERS.copy();

        private ElderGuardian() {
        }
    }

    public static final class Pig {

        public static final ParameterTypeCollection PARAMETERS = Ageable.PARAMETERS.copy();

        public static final ParameterType<Boolean> HAS_SADDLE = PARAMETERS.newParameterType(ParameterValueTypes.BOOLEAN);

        private Pig() {
        }
    }

    public static final class Rabbit {

        public static final ParameterTypeCollection PARAMETERS = Ageable.PARAMETERS.copy();

        public static final ParameterType<Integer> VARIANT = PARAMETERS.newParameterType(ParameterValueTypes.INTEGER);

        private Rabbit() {
        }
    }

    public static final class PolarBear {

        public static final ParameterTypeCollection PARAMETERS = Ageable.PARAMETERS.copy();

        public static final ParameterType<Boolean> STANDING_UP = PARAMETERS.newParameterType(ParameterValueTypes.BOOLEAN);

        private PolarBear() {
        }
    }

    public static final class Sheep {

        public static final ParameterTypeCollection PARAMETERS = Ageable.PARAMETERS.copy();

        /**
         * Bit mask	Meaning
         * 0x0F	    Color
         * 0x10	    Is sheared
         */
        public static final ParameterType<Byte> FLAGS = PARAMETERS.newParameterType(ParameterValueTypes.BYTE);

        private Sheep() {
        }
    }

    public static final class TameableAnimal {

        public static final ParameterTypeCollection PARAMETERS = Ageable.PARAMETERS.copy();

        /**
         * Bit mask	Meaning
         * 0x01	    Is sitting
         * 0x02	    Is angry
         * 0x04	    Is tamed
         */
        public static final ParameterType<Byte> FLAGS = PARAMETERS.newParameterType(ParameterValueTypes.BYTE);

        public static final ParameterType<Optional<UUID>> OWNER = PARAMETERS.newParameterType(ParameterValueTypes.OPTIONAL_UUID);

        private TameableAnimal() {
        }
    }

    public static final class Ocelot {

        public static final ParameterTypeCollection PARAMETERS = TameableAnimal.PARAMETERS.copy();

        public static final ParameterType<Integer> VARIANT = PARAMETERS.newParameterType(ParameterValueTypes.INTEGER);

        private Ocelot() {
        }
    }

    public static final class Wolf {

        public static final ParameterTypeCollection PARAMETERS = TameableAnimal.PARAMETERS.copy();

        /**
         * 0 - maxHealth, only works when tamed
         */
        public static final ParameterType<Float> TAIL_ROTATION = PARAMETERS.newParameterType(ParameterValueTypes.FLOAT);

        public static final ParameterType<Boolean> IS_BEGGING = PARAMETERS.newParameterType(ParameterValueTypes.BOOLEAN);

        public static final ParameterType<Integer> COLLAR_COLOR = PARAMETERS.newParameterType(ParameterValueTypes.INTEGER);

        private Wolf() {
        }
    }

    public static final class Villager {

        public static final ParameterTypeCollection PARAMETERS = Ageable.PARAMETERS.copy();

        public static final ParameterType<Integer> PROFESSION = PARAMETERS.newParameterType(ParameterValueTypes.INTEGER);

        private Villager() {
        }
    }

    public static final class IronGolem {

        public static final ParameterTypeCollection PARAMETERS = Insentient.PARAMETERS.copy();

        /**
         * Bit mask	Meaning
         * 0x01	    Is player-created
         */
        public static final ParameterType<Byte> FLAGS = PARAMETERS.newParameterType(ParameterValueTypes.BYTE);

        private IronGolem() {
        }
    }

    public static final class Snowman {

        public static final ParameterTypeCollection PARAMETERS = Insentient.PARAMETERS.copy();

        /**
         * Bit mask	Meaning
         * 0x10	    No pumpkin hat
         */
        public static final ParameterType<Byte> FLAGS = PARAMETERS.newParameterType(ParameterValueTypes.BYTE);

        private Snowman() {
        }
    }

    public static final class Vex {

        public static final ParameterTypeCollection PARAMETERS = Insentient.PARAMETERS.copy();

        /**
         * Bit mask	Meaning
         * 0x01	    Attack mode
         */
        public static final ParameterType<Byte> FLAGS = PARAMETERS.newParameterType(ParameterValueTypes.BYTE);

        private Vex() {
        }
    }

    public static final class Item {

        public static final ParameterTypeCollection PARAMETERS = Base.PARAMETERS.copy();

        public static final ParameterType<ItemStack> ITEM = PARAMETERS.newParameterType(ParameterValueTypes.ITEM_STACK);

        private Item() {
        }
    }

    public static final class EnderDragon {

        public static final ParameterTypeCollection PARAMETERS = Insentient.PARAMETERS.copy();

        /**
         * The phase of the dragon.
         */
        public static final ParameterType<Integer> PHASE = PARAMETERS.newParameterType(ParameterValueTypes.INTEGER);

        private EnderDragon() {
        }
    }

    public static final class Fireworks {

        public static final ParameterTypeCollection PARAMETERS = Base.PARAMETERS.copy();

        public static final ParameterType<ItemStack> ITEM = PARAMETERS.newParameterType(ParameterValueTypes.ITEM_STACK);

        public static final ParameterType<Integer> ELYTRA_BOOST_PLAYER = PARAMETERS.newParameterType(ParameterValueTypes.INTEGER);

        private Fireworks() {
        }
    }

    private EntityParameters() {
    }
}
