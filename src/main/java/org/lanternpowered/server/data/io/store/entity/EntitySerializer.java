/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.data.io.store.entity;

import org.lanternpowered.server.data.io.store.IdentifiableObjectStore;
import org.lanternpowered.server.data.io.store.ObjectSerializer;
import org.lanternpowered.server.data.io.store.ObjectStore;
import org.lanternpowered.server.data.io.store.ObjectStoreRegistry;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.entity.LanternEntityType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EntitySerializer implements ObjectSerializer<LanternEntity> {

    private static final DataQuery ID = DataQuery.of("id");

    @Override
    public LanternEntity deserialize(DataView dataView) throws InvalidDataException {
        final String id = fixEntityId(dataView, dataView.getString(ID).get());
        dataView.remove(ID);

        final LanternEntityType entityType = (LanternEntityType) Sponge.getRegistry().getType(EntityType.class, id).orElseThrow(
                () -> new InvalidDataException("Unknown entity id: " + id));
        //noinspection unchecked
        final ObjectStore<LanternEntity> store = (ObjectStore) ObjectStoreRegistry.get().get(entityType.getEntityClass()).get();
        final UUID uniqueId;
        if (store instanceof IdentifiableObjectStore) {
            uniqueId = ((IdentifiableObjectStore) store).deserializeUniqueId(dataView);
        } else {
            uniqueId = UUID.randomUUID();
        }
        //noinspection unchecked
        final LanternEntity entity = (LanternEntity) entityType.getEntityConstructor().apply(uniqueId);
        store.deserialize(entity, dataView);
        return entity;
    }

    @Override
    public DataView serialize(LanternEntity object) {
        final DataView dataView = new MemoryDataContainer(DataView.SafetyMode.NO_DATA_CLONED);
        dataView.set(ID, object.getType().getId());
        //noinspection unchecked
        final ObjectStore<LanternEntity> store = (ObjectStore) ObjectStoreRegistry.get().get(object.getClass()).get();
        store.serialize(object, dataView);
        if (store instanceof IdentifiableObjectStore) {
            ((IdentifiableObjectStore) store).serializeUniqueId(dataView, object.getUniqueId());
        }
        return dataView;
    }

    private static final String[] HORSE_ENTITY_IDS =
            { "minecraft:horse", "minecraft:donkey", "minecraft:mule", "minecraft:zombie_horse", "minecraft:skeleton_horse" };
    private static final DataQuery HORSE_TYPE = DataQuery.of("Type");

    private static final String[] SKELETON_ENTITY_IDS =
            { "minecraft:skeleton", "minecraft:wither_skeleton", "minecraft:stray" };
    private static final DataQuery SKELETON_TYPE = DataQuery.of("SkeletonType");

    private static final String[] MINECART_ENTITY_IDS =
            { "minecraft:minecart", "minecraft:chest_minecart", "minecraft:furnace_minecart", "minecraft:tnt_minecart",
                    "minecraft:spawner_minecart", "minecraft:hopper_minecart", "minecraft:commandblock_minecart" };
    private static final DataQuery MINECART_TYPE = DataQuery.of("Type");

    private static final DataQuery ZOMBIE_TYPE = DataQuery.of("ZombieType");

    private static final Map<String, String> OLD_TO_NEW_ID_MAPPINGS = new HashMap<>();

    private static void put(String newId, String oldId) {
        OLD_TO_NEW_ID_MAPPINGS.put(oldId, newId);
    }

    static {
        put("minecraft:area_effect_cloud", "AreaEffectCloud");
        put("minecraft:armor_stand", "ArmorStand");
        put("minecraft:arrow", "Arrow");
        put("minecraft:bat", "Bat");
        put("minecraft:blaze", "Blaze");
        put("minecraft:boat", "Boat");
        put("minecraft:cave_spider", "CaveSpider");
        put("minecraft:chest_minecart", "MinecartChest");
        put("minecraft:chicken", "Chicken");
        put("minecraft:commandblock_minecart", "MinecartCommandBlock");
        put("minecraft:cow", "Cow");
        put("minecraft:creeper", "Creeper");
        put("minecraft:donkey", "Donkey");
        put("minecraft:dragon_fireball", "DragonFireball");
        put("minecraft:egg", "ThrownEgg");
        put("minecraft:elder_guardian", "ElderGuardian");
        put("minecraft:ender_crystal", "EnderCrystal");
        put("minecraft:ender_dragon", "EnderDragon");
        put("minecraft:ender_pearl", "ThrownEnderpearl");
        put("minecraft:enderman", "Enderman");
        put("minecraft:endermite", "Endermite");
        put("minecraft:eye_of_ender_signal", "EyeOfEnderSignal");
        put("minecraft:falling_block", "FallingSand");
        put("minecraft:fireball", "Fireball");
        put("minecraft:fireworks_rocket", "FireworksRocketEntity");
        put("minecraft:furnace_minecart", "MinecartFurnace");
        put("minecraft:ghast", "Ghast");
        put("minecraft:giant", "Giant");
        put("minecraft:guardian", "Guardian");
        put("minecraft:hopper_minecart", "MinecartHopper");
        put("minecraft:horse", "Horse");
        put("minecraft:husk", "Husk");
        put("minecraft:item", "Item");
        put("minecraft:item_frame", "ItemFrame");
        put("minecraft:leash_knot", "LeashKnot");
        put("minecraft:magma_cube", "LavaSlime");
        put("minecraft:minecart", "MinecartRideable");
        put("minecraft:mooshroom", "MushroomCow");
        put("minecraft:mule", "Mule");
        put("minecraft:ocelot", "Ozelot");
        put("minecraft:painting", "Painting");
        put("minecraft:pig", "Pig");
        put("minecraft:polar_bear", "PolarBear");
        put("minecraft:potion", "ThrownPotion");
        put("minecraft:rabbit", "Rabbit");
        put("minecraft:sheep", "Sheep");
        put("minecraft:shulker", "Shulker");
        put("minecraft:shulker_bullet", "ShulkerBullet");
        put("minecraft:silverfish", "Silverfish");
        put("minecraft:skeleton", "Skeleton");
        put("minecraft:skeleton_horse", "SkeletonHorse");
        put("minecraft:slime", "Slime");
        put("minecraft:small_fireball", "SmallFireball");
        put("minecraft:snowball", "Snowball");
        put("minecraft:snowman", "SnowMan");
        put("minecraft:spawner_minecart", "MinecartSpawner");
        put("minecraft:spectral_arrow", "SpectralArrow");
        put("minecraft:spider", "Spider");
        put("minecraft:squid", "Squid");
        put("minecraft:stray", "Stray");
        put("minecraft:tnt", "PrimedTnt");
        put("minecraft:tnt_minecart", "MinecartTNT");
        put("minecraft:villager", "Villager");
        put("minecraft:villager_golem", "VillagerGolem");
        put("minecraft:witch", "Witch");
        put("minecraft:wither", "WitherBoss");
        put("minecraft:wither_skeleton", "WitherSkeleton");
        put("minecraft:wither_skull", "WitherSkull");
        put("minecraft:wolf", "Wolf");
        put("minecraft:xp_bottle", "ThrownExpBottle");
        put("minecraft:xp_orb", "XPOrb");
        put("minecraft:zombie", "Zombie");
        put("minecraft:zombie_horse", "ZombieHorse");
        put("minecraft:zombie_pigman", "PigZombie");
        put("minecraft:zombie_villager", "ZombieVillager");
    }

    private static String fixEntityId(DataView dataView, String id) {
        // Separate the horse entities
        if (id.equals("EntityHorse")) {
            final int type = dataView.getInt(HORSE_TYPE).get();
            dataView.remove(HORSE_TYPE);
            return HORSE_ENTITY_IDS[type < 0 ? 0 : type >= HORSE_ENTITY_IDS.length ? 0 : type];
        // Separate the skeleton entities
        } else if (id.equals("Skeleton")) {
            final int type = dataView.getInt(SKELETON_TYPE).orElse(0);
            dataView.remove(SKELETON_TYPE);
            return SKELETON_ENTITY_IDS[type < 0 ? 0 : type >= SKELETON_ENTITY_IDS.length ? 0 : type];
        // Separate the minecart entities
        } else if (id.equals("Minecart")) {
            final int type = dataView.getInt(MINECART_TYPE).get();
            dataView.remove(MINECART_TYPE);
            return MINECART_ENTITY_IDS[type < 0 ? 0 : type >= MINECART_ENTITY_IDS.length ? 0 : type];
        } else if (id.equals("Zombie")) {
            final int type = dataView.getInt(ZOMBIE_TYPE).get();
            // Profession
            if (type > 0 && type <= 5) {
                dataView.set(ZombieVillagerStore.PROFESSION, type - 1);
                return "minecraft:zombie_villager";
            } else if (type == 6) {
                return "minecraft:husk";
            } else {
                return "minecraft:zombie";
            }
        }
        final String id1 = OLD_TO_NEW_ID_MAPPINGS.get(id);
        return id1 == null ? id : id1;
    }
}
