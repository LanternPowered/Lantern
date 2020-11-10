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
package org.lanternpowered.server.network.entity.vanilla

import org.lanternpowered.api.text.Text
import org.lanternpowered.server.entity.Pose
import org.lanternpowered.server.network.entity.parameter.ParameterType
import org.lanternpowered.server.network.entity.parameter.ParameterTypeCollection
import org.lanternpowered.server.network.entity.parameter.ParameterValueTypes
import org.spongepowered.api.data.persistence.DataView
import java.util.Optional
import java.util.OptionalInt

class EntityParameters private constructor() {

    object Base {

        val PARAMETERS = ParameterTypeCollection()

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
        @JvmField
        val FLAGS: ParameterType<Byte> = PARAMETERS.create(ParameterValueTypes.BYTE)

        /**
         * The air level of the entity.
         */
        val AIR_LEVEL: ParameterType<Int> = PARAMETERS.create(ParameterValueTypes.INT)

        /**
         * The custom name of the entity.
         */
        val CUSTOM_NAME: ParameterType<Text?> = PARAMETERS.create(ParameterValueTypes.OPTIONAL_TEXT)

        /**
         * Whether the custom name is always visible.
         */
        val CUSTOM_NAME_VISIBLE: ParameterType<Boolean> = PARAMETERS.create(ParameterValueTypes.BOOLEAN)

        /**
         * Whether the entity is silent.
         */
        val IS_SILENT: ParameterType<Boolean> = PARAMETERS.create(ParameterValueTypes.BOOLEAN)

        /**
         * Whether the entity has no gravity.
         */
        @JvmField
        val NO_GRAVITY: ParameterType<Boolean> = PARAMETERS.create(ParameterValueTypes.BOOLEAN)

        /**
         * The pose of the entity.
         */
        val POSE: ParameterType<Pose> = PARAMETERS.create(ParameterValueTypes.POSE)

        object Flags {
            const val IS_ON_FIRE = 0x1
            const val IS_SNEAKING = 0x2
            const val IS_SPRINTING = 0x8
            const val IS_SWIMMING = 0x10
            const val IS_INVISIBLE = 0x20
            const val IS_GLOWING = 0x40
            const val IS_ELYTRA_FlYING = 0x80
        }
    }

    object Living {

        val PARAMETERS = Base.PARAMETERS.copy()

        /**
         * Bit mask Meaning
         * 0x01     Is hand active
         * 0x02     Active hand (0 = main hand, 1 = offhand)
         * 0x04     In riptide spin attack
         */
        @JvmField
        val FLAGS = PARAMETERS.create(ParameterValueTypes.BYTE)

        /**
         * The health of the entity.
         */
        @JvmField
        val HEALTH = PARAMETERS.create(ParameterValueTypes.FLOAT)

        /**
         * The potion effect color of the particles that spawn around the player.
         */
        @JvmField
        val POTION_EFFECT_COLOR = PARAMETERS.create(ParameterValueTypes.INT)

        /**
         * Whether the potion effect particles are ambient.
         */
        @JvmField
        val POTION_EFFECT_AMBIENT = PARAMETERS.create(ParameterValueTypes.BOOLEAN)

        /**
         * The amount of arrows that are in the entity.
         */
        @JvmField
        val ARROWS_IN_ENTITY = PARAMETERS.create(ParameterValueTypes.INT)

        /**
         * Additional yellow hearts.
         */
        val ADDITIONAL_HEARTS = PARAMETERS.create(ParameterValueTypes.INT)

        /**
         * The sleeping location, if sleeping.
         */
        val SLEEPING_LOCATION = PARAMETERS.create(ParameterValueTypes.OPTIONAL_BLOCK_POSITION)
    }

    object ArmorStand {
        val PARAMETERS = Living.PARAMETERS.copy()

        /**
         * Bit mask Meaning
         * 0x01	    Small
         * 0x04	    Arms
         * 0x08	    No base plate
         * 0x10	    Marker
         */
        val FLAGS = PARAMETERS.create(ParameterValueTypes.BYTE)
        val HEAD_ROTATION = PARAMETERS.create(ParameterValueTypes.VECTOR_3F)
        val BODY_ROTATION = PARAMETERS.create(ParameterValueTypes.VECTOR_3F)
        val LEFT_ARM_ROTATION = PARAMETERS.create(ParameterValueTypes.VECTOR_3F)
        val RIGHT_ARM_ROTATION = PARAMETERS.create(ParameterValueTypes.VECTOR_3F)
        val LEFT_LEG_ROTATION = PARAMETERS.create(ParameterValueTypes.VECTOR_3F)
        val RIGHT_LEG_ROTATION = PARAMETERS.create(ParameterValueTypes.VECTOR_3F)
    }

    object Insentient {
        val PARAMETERS = Living.PARAMETERS.copy()

        /**
         * Bit mask Meaning
         * 0x01     NoAI
         * 0x02     Left handed
         */
        @JvmField
        val FLAGS = PARAMETERS.create(ParameterValueTypes.BYTE)
    }

    object Humanoid {
        val PARAMETERS = Living.PARAMETERS.copy()

        /**
         * Additional yellow hearts.
         */
        @JvmField
        val ADDITIONAL_HEARTS = PARAMETERS.create(ParameterValueTypes.FLOAT)

        /**
         * The score of the player. This is displayed on the respawn screen.
         */
        @JvmField
        val SCORE = PARAMETERS.create(ParameterValueTypes.INT)

        /**
         * The displayed skin parts.
         */
        @JvmField
        val SKIN_PARTS = PARAMETERS.create(ParameterValueTypes.BYTE)

        /**
         * The main hand. (0: Left, 1: Right)
         */
        @JvmField
        val MAIN_HAND = PARAMETERS.create(ParameterValueTypes.BYTE)

        /**
         * Th entity on the left shoulder.
         */
        val LEFT_SHOULDER_ENTITY: ParameterType<DataView?> = PARAMETERS.create(ParameterValueTypes.NBT_TAG)

        /**
         * Th entity on the right shoulder.
         */
        val RIGHT_SHOULDER_ENTITY: ParameterType<DataView?> = PARAMETERS.create(ParameterValueTypes.NBT_TAG)
    }

    object AbstractSlime {
        val PARAMETERS = Insentient.PARAMETERS.copy()

        /**
         * The size of the slime.
         */
        @JvmField
        val SIZE = PARAMETERS.create(ParameterValueTypes.INT)
    }

    object Slime {
        val PARAMETERS = AbstractSlime.PARAMETERS.copy()
    }

    object MagmaCube {
        val PARAMETERS = AbstractSlime.PARAMETERS.copy()
    }

    object Bat {
        val PARAMETERS = Insentient.PARAMETERS.copy()

        /**
         * Bit mask Meaning
         * 0x01	    Is hanging
         */
        @JvmField
        val FLAGS = PARAMETERS.create(ParameterValueTypes.BYTE)
    }

    object Ageable {
        val PARAMETERS = Insentient.PARAMETERS.copy()

        /**
         * Whether the entity is a baby.
         */
        @JvmField
        val IS_BABY = PARAMETERS.create(ParameterValueTypes.BOOLEAN)
    }

    object AbstractHorse {
        val PARAMETERS = Ageable.PARAMETERS.copy()

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
        val FLAGS = PARAMETERS.create(ParameterValueTypes.BYTE)

        /**
         * The owner of the horse.
         */
        val OWNER = PARAMETERS.create(ParameterValueTypes.OPTIONAL_UUID)
    }

    object Horse {
        val PARAMETERS = AbstractHorse.PARAMETERS.copy()
        val VARIANT = PARAMETERS.create(ParameterValueTypes.INT)
        val ARMOR = PARAMETERS.create(ParameterValueTypes.INT)
    }

    object ChestedHorse {
        val PARAMETERS = AbstractHorse.PARAMETERS.copy()
        val HAS_CHEST = PARAMETERS.create(ParameterValueTypes.BOOLEAN)
    }

    object ZombieHorse {
        val PARAMETERS = AbstractHorse.PARAMETERS.copy()
    }

    object SkeletonHorse {
        val PARAMETERS = AbstractHorse.PARAMETERS.copy()
    }

    object Donkey {
        val PARAMETERS = ChestedHorse.PARAMETERS.copy()
    }

    object Mule {
        val PARAMETERS = ChestedHorse.PARAMETERS.copy()
    }

    object Llama {
        val PARAMETERS = ChestedHorse.PARAMETERS.copy()
        val STRENGTH = PARAMETERS.create(ParameterValueTypes.INT)
        val CARPET_COLOR = PARAMETERS.create(ParameterValueTypes.INT)
        val VARIANT = PARAMETERS.create(ParameterValueTypes.INT)
    }

    object AbstractZombie {
        val PARAMETERS = Ageable.PARAMETERS.copy()
        @JvmField
        val UNUSED = PARAMETERS.create(ParameterValueTypes.INT)
        @JvmField
        val HANDS_UP = PARAMETERS.create(ParameterValueTypes.BOOLEAN)
    }

    object Zombie {
        val PARAMETERS = AbstractZombie.PARAMETERS.copy()
    }

    object ZombieVillager {
        val PARAMETERS = AbstractZombie.PARAMETERS.copy()

        val IS_CONVERTING = PARAMETERS.create(ParameterValueTypes.BOOLEAN)

        val VILLAGER_DATA = PARAMETERS.create(ParameterValueTypes.VILLAGER_DATA)
    }

    object Husk {
        val PARAMETERS = AbstractZombie.PARAMETERS.copy()
    }

    object AbstractSkeleton {
        val PARAMETERS = Insentient.PARAMETERS.copy()
        val IS_SWINGING_ARMS = PARAMETERS.create(ParameterValueTypes.BOOLEAN)
    }

    object Skeleton {
        val PARAMETERS = AbstractSkeleton.PARAMETERS.copy()
    }

    object WitherSkeleton {
        val PARAMETERS = AbstractSkeleton.PARAMETERS.copy()
    }

    object Stray {
        val PARAMETERS = AbstractSkeleton.PARAMETERS.copy()
    }

    object AbstractGuardian {
        val PARAMETERS = Insentient.PARAMETERS.copy()
        val IS_RETRACTING_SPIKES = PARAMETERS.create(ParameterValueTypes.BOOLEAN)
        val TARGET = PARAMETERS.create(ParameterValueTypes.INT)
    }

    object Guardian {
        val PARAMETERS = AbstractGuardian.PARAMETERS.copy()
    }

    object ElderGuardian {
        val PARAMETERS = AbstractGuardian.PARAMETERS.copy()
    }

    object Pig {
        val PARAMETERS = Ageable.PARAMETERS.copy()
        @JvmField
        val HAS_SADDLE = PARAMETERS.create(ParameterValueTypes.BOOLEAN)
    }

    object Rabbit {
        val PARAMETERS = Ageable.PARAMETERS.copy()
        @JvmField
        val VARIANT = PARAMETERS.create(ParameterValueTypes.INT)
    }

    object PolarBear {
        val PARAMETERS = Ageable.PARAMETERS.copy()
        val STANDING_UP = PARAMETERS.create(ParameterValueTypes.BOOLEAN)
    }

    object Sheep {
        val PARAMETERS = Ageable.PARAMETERS.copy()

        /**
         * Bit mask	Meaning
         * 0x0F	    Color
         * 0x10	    Is sheared
         */
        @JvmField
        val FLAGS = PARAMETERS.create(ParameterValueTypes.BYTE)
    }

    object TameableAnimal {
        val PARAMETERS = Ageable.PARAMETERS.copy()

        /**
         * Bit mask	Meaning
         * 0x01	    Is sitting
         * 0x02	    Is angry
         * 0x04	    Is tamed
         */
        val FLAGS = PARAMETERS.create(ParameterValueTypes.BYTE)
        val OWNER = PARAMETERS.create(ParameterValueTypes.OPTIONAL_UUID)
    }

    object Ocelot {
        val PARAMETERS = Ageable.PARAMETERS.copy()
        val TRUSTING = PARAMETERS.create(ParameterValueTypes.BOOLEAN)
    }

    object Cat {
        val PARAMETERS = TameableAnimal.PARAMETERS.copy()
        val VARIANT = PARAMETERS.create(ParameterValueTypes.INT)
        val UNKNOWN_1 = PARAMETERS.create(ParameterValueTypes.BOOLEAN)
        val UNKNOWN_2 = PARAMETERS.create(ParameterValueTypes.BOOLEAN)
        val COLLAR_COLOR = PARAMETERS.create(ParameterValueTypes.INT)
    }

    object Wolf {
        val PARAMETERS = TameableAnimal.PARAMETERS.copy()

        val IS_BEGGING = PARAMETERS.create(ParameterValueTypes.BOOLEAN)

        val COLLAR_COLOR = PARAMETERS.create(ParameterValueTypes.INT)
    }

    object Villager {
        val PARAMETERS = Ageable.PARAMETERS.copy()

        val HEAD_SHAKE_TIMER = PARAMETERS.create(ParameterValueTypes.INT)

        val VILLAGER_DATA = PARAMETERS.create(ParameterValueTypes.VILLAGER_DATA)
    }

    object IronGolem {
        val PARAMETERS = Insentient.PARAMETERS.copy()

        /**
         * Bit mask	Meaning
         * 0x01	    Is player-created
         */
        @JvmField
        val FLAGS = PARAMETERS.create(ParameterValueTypes.BYTE)
    }

    object Snowman {
        val PARAMETERS = Insentient.PARAMETERS.copy()

        /**
         * Bit mask	Meaning
         * 0x10	    No pumpkin hat
         */
        @JvmField
        val FLAGS = PARAMETERS.create(ParameterValueTypes.BYTE)
    }

    object Vex {
        val PARAMETERS = Insentient.PARAMETERS.copy()

        /**
         * Bit mask	Meaning
         * 0x01	    Attack mode
         */
        val FLAGS = PARAMETERS.create(ParameterValueTypes.BYTE)
    }

    object Item {
        val PARAMETERS = Base.PARAMETERS.copy()
        @JvmField
        val ITEM = PARAMETERS.create(ParameterValueTypes.ITEM_STACK)
    }

    object EnderDragon {
        val PARAMETERS = Insentient.PARAMETERS.copy()

        /**
         * The phase of the dragon.
         */
        val PHASE = PARAMETERS.create(ParameterValueTypes.INT)
    }

    object Fireworks {
        val PARAMETERS = Base.PARAMETERS.copy()
        @JvmField
        val ITEM = PARAMETERS.create(ParameterValueTypes.ITEM_STACK)
        @JvmField
        val ELYTRA_BOOST_PLAYER: ParameterType<Int?> = PARAMETERS.create(ParameterValueTypes.OPTIONAL_INT)
        val SHOT_AT_ANGLE = PARAMETERS.create(ParameterValueTypes.BOOLEAN)
    }
}