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
package org.lanternpowered.server.game.registry.type.statistic;

import static com.google.common.base.Preconditions.checkArgument;
import static org.lanternpowered.server.text.translation.TranslationHelper.tr;

import com.google.common.base.Throwables;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.statistic.achievement.AchievementBuilder;
import org.lanternpowered.server.statistic.achievement.LanternAchievement;
import org.lanternpowered.server.statistic.achievement.LanternAchievementBuilder;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.statistic.achievement.Achievement;
import org.spongepowered.api.statistic.achievement.Achievements;

import java.lang.reflect.Method;

/**
 * {@link Achievement}s are being replaced by advancements in 1.12,
 * just leaving this until sponge decides to remove the api.
 */
@Deprecated
@RegistrationDependency(StatisticRegistryModule.class)
public final class AchievementRegistryModule extends AdditionalPluginCatalogRegistryModule<Achievement> {

    private static final AchievementRegistryModule INSTANCE = new AchievementRegistryModule();

    public static AchievementRegistryModule get() {
        return INSTANCE;
    }

    private static final Method ADD_ACHIEVEMENT_CHILD;

    static {
        try {
            ADD_ACHIEVEMENT_CHILD = LanternAchievement.class.getDeclaredMethod("addChild", LanternAchievement.class);
            ADD_ACHIEVEMENT_CHILD.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw Throwables.propagate(e);
        }
    }

    private AchievementRegistryModule() {
        super(Achievements.class);
    }

    @Override
    protected void register(Achievement catalogType, boolean disallowInbuiltPluginIds) {
        internalRegister(catalogType, disallowInbuiltPluginIds);
        StatisticRegistryModule.get().internalRegister(catalogType, disallowInbuiltPluginIds);
    }

    void internalRegister(Achievement catalogType, boolean disallowInbuiltPluginIds) {
        super.register(catalogType, disallowInbuiltPluginIds);
        catalogType.getParent().ifPresent(parent -> {
            checkArgument(getById(parent.getId()).orElse(null) == parent, "The parent must be registered.");
            try {
                ADD_ACHIEVEMENT_CHILD.invoke(parent, catalogType);
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }
        });
    }

    @Override
    public void registerDefaults() {
        final Achievement openInventory = vanillaBuilder("openInventory")
                .build("minecraft", "open_inventory");
        register(openInventory);

        final Achievement mineWood = vanillaBuilder("mineWood")
                .parent(openInventory)
                .build("minecraft", "mine_wood");
        register(mineWood);

        final Achievement buildWorkbench = vanillaBuilder("buildWorkBench")
                .parent(mineWood)
                .build("minecraft", "build_work_bench");
        register(buildWorkbench);

        final Achievement buildPickaxe = vanillaBuilder("buildPickaxe")
                .parent(buildWorkbench)
                .build("minecraft", "build_pickaxe");
        register(buildPickaxe);

        final Achievement buildFurnace = vanillaBuilder("buildFurnace")
                .parent(buildPickaxe)
                .build("minecraft", "build_furnace");
        register(buildFurnace);

        final Achievement acquireIron = vanillaBuilder("acquireIron")
                .parent(buildFurnace)
                .build("minecraft", "acquire_iron");
        register(acquireIron);

        final Achievement buildHoe = vanillaBuilder("buildHoe")
                .parent(buildWorkbench)
                .build("minecraft", "build_hoe");
        register(buildHoe);

        final Achievement makeBread = vanillaBuilder("makeBread")
                .parent(buildHoe)
                .build("minecraft", "make_bread");
        register(makeBread);

        final Achievement bakeCake = vanillaBuilder("bakeCake")
                .parent(buildHoe)
                .build("minecraft", "bake_cake");
        register(bakeCake);

        final Achievement buildBetterPickaxe = vanillaBuilder("buildBetterPickaxe")
                .parent(buildHoe)
                .build("minecraft", "build_better_pickaxe");
        register(buildBetterPickaxe);

        final Achievement cookFish = vanillaBuilder("cookFish")
                .parent(buildFurnace)
                .build("minecraft", "cook_fish");
        register(cookFish);

        final Achievement onARail = vanillaBuilder("onARail")
                .parent(acquireIron)
                .build("minecraft", "on_a_rail");
        register(onARail);

        final Achievement buildSword = vanillaBuilder("buildSword")
                .parent(buildWorkbench)
                .build("minecraft", "build_sword");
        register(buildSword);

        final Achievement killEnemy = vanillaBuilder("killEnemy")
                .parent(buildSword)
                .build("minecraft", "kill_enemy");
        register(killEnemy);

        final Achievement killCow = vanillaBuilder("killCow")
                .parent(buildSword)
                .build("minecraft", "kill_cow");
        register(killCow);

        final Achievement flyPig = vanillaBuilder("flyPig")
                .parent(killCow)
                .build("minecraft", "fly_pig");
        register(flyPig);

        final Achievement snipeSkeleton = vanillaBuilder("snipeSkeleton")
                .parent(killEnemy)
                .build("minecraft", "snipe_skeleton");
        register(snipeSkeleton);

        final Achievement diamonds = vanillaBuilder("diamonds")
                .parent(acquireIron)
                .build("minecraft", "diamonds");
        register(diamonds);

        final Achievement diamondsToYou = vanillaBuilder("diamondsToYou")
                .parent(diamonds)
                .build("minecraft", "diamonds_to_you");
        register(diamondsToYou);

        final Achievement portal = vanillaBuilder("portal")
                .parent(diamonds)
                .build("minecraft", "portal");
        register(portal);

        final Achievement ghast = vanillaBuilder("ghast")
                .parent(portal)
                .build("minecraft", "ghast");
        register(ghast);

        final Achievement blazeRod = vanillaBuilder("blazeRod")
                .parent(portal)
                .build("minecraft", "blaze_rod");
        register(blazeRod);

        final Achievement potion = vanillaBuilder("potion")
                .parent(blazeRod)
                .build("minecraft", "potion");
        register(potion);

        final Achievement endPortal = vanillaBuilder("theEnd")
                .parent(blazeRod)
                .build("minecraft", "the_end");
        register(endPortal);

        final Achievement theEnd = vanillaBuilder("theEnd2")
                .parent(endPortal)
                .build("minecraft", "the_end2");
        register(theEnd);

        final Achievement enchantments = vanillaBuilder("enchantments")
                .parent(diamonds)
                .build("minecraft", "enchantments");
        register(enchantments);

        final Achievement overkill = vanillaBuilder("overkill")
                .parent(enchantments)
                .build("minecraft", "overkill");
        register(overkill);

        final Achievement bookcase = vanillaBuilder("bookcase")
                .parent(enchantments)
                .build("minecraft", "bookcase");
        register(bookcase);

        final Achievement breedCow = vanillaBuilder("breedCow")
                .parent(killCow)
                .build("minecraft", "breed_cow");
        register(breedCow);

        final Achievement spawnWither = vanillaBuilder("spawnWither")
                .parent(theEnd)
                .build("minecraft", "spawn_wither");
        register(spawnWither);

        final Achievement killWither = vanillaBuilder("killWither")
                .parent(spawnWither)
                .build("minecraft", "kill_wither");
        register(killWither);

        final Achievement fullBeacon = vanillaBuilder("fullBeacon")
                .parent(killWither)
                .build("minecraft", "full_beacon");
        register(fullBeacon);

        final Achievement exploreAllBiomes = vanillaBuilder("exploreAllBiomes")
                .parent(endPortal)
                .build("minecraft", "explore_all_biomes");
        register(exploreAllBiomes);

        final Achievement overpowered = vanillaBuilder("overpowered")
                .parent(buildBetterPickaxe)
                .build("minecraft", "overpowered");
        register(overpowered);
    }

    private AchievementBuilder vanillaBuilder(String unlocalizedName) {
        final String internalId = String.format("achievement.%s", unlocalizedName);
        return new LanternAchievementBuilder()
                .internalId(internalId)
                .translation(tr(internalId))
                .description(tr("%s.desc", internalId))
                .targetValue(1);
    }
}
