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
import org.lanternpowered.server.statistic.LanternStatistic;
import org.lanternpowered.server.statistic.achievement.LanternAchievement;
import org.lanternpowered.server.statistic.achievement.LanternAchievementBuilder;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.statistic.achievement.Achievement;
import org.spongepowered.api.statistic.achievement.Achievements;

import java.lang.reflect.Method;

@RegistrationDependency(StatisticRegistryModule.class)
public final class AchievementRegistryModule extends AdditionalPluginCatalogRegistryModule<Achievement> {

    private static final AchievementRegistryModule INSTANCE = new AchievementRegistryModule();

    public static AchievementRegistryModule get() {
        return INSTANCE;
    }

    private static final Method ADD_ACHIEVEMENT_CHILD;
    private static final Method ADD_STATISTIC_CHILD;

    static {
        try {
            ADD_ACHIEVEMENT_CHILD = LanternAchievement.class.getDeclaredMethod("addChild", LanternAchievement.class);
            ADD_ACHIEVEMENT_CHILD.setAccessible(true);

            ADD_STATISTIC_CHILD = LanternStatistic.class.getDeclaredMethod("addUpdateAchievement", LanternAchievement.class);
            ADD_STATISTIC_CHILD.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw Throwables.propagate(e);
        }
    }

    private AchievementRegistryModule() {
        super(Achievements.class);
    }

    @Override
    protected void register(Achievement catalogType, boolean disallowInbuiltPluginIds) {
        super.register(catalogType, disallowInbuiltPluginIds);

        catalogType.getParent().ifPresent(parent -> {
            checkArgument(getById(parent.getId()).orElse(null) == parent, "The parent must be registered.");
            try {
                ADD_ACHIEVEMENT_CHILD.invoke(parent, catalogType);
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }
        });
        catalogType.getSourceStatistic().ifPresent(statistic -> {
            try {
                ADD_STATISTIC_CHILD.invoke(statistic, catalogType);
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }
        });
    }

    @Override
    public void registerDefaults() {
        final LanternAchievementBuilder builder = new LanternAchievementBuilder();

        final Achievement openInventory = builder.reset().name("minecraft:open_inventory")
                .translation(tr("achievement.openInventory"))
                .description(tr("achievement.openInventory.desc"))
                .build();
        register(openInventory);

        final Achievement mineWood = builder.reset().name("minecraft:mine_wood")
                .translation(tr("achievement.mineWood"))
                .description(tr("achievement.mineWood.desc"))
                .parent(openInventory)
                .build();
        register(mineWood);

        final Achievement buildWorkbench = builder.reset().name("minecraft:build_workbench")
                .translation(tr("achievement.buildWorkBench"))
                .description(tr("achievement.buildWorkBench.desc"))
                .parent(mineWood)
                .build();
        register(buildWorkbench);

        final Achievement buildPickaxe = builder.reset().name("minecraft:build_pickaxe")
                .translation(tr("achievement.buildPickaxe"))
                .description(tr("achievement.buildPickaxe.desc"))
                .parent(buildWorkbench)
                .build();
        register(buildPickaxe);

        final Achievement buildFurnace = builder.reset().name("minecraft:build_furnace")
                .translation(tr("achievement.buildFurnace"))
                .description(tr("achievement.buildFurnace.desc"))
                .parent(buildPickaxe)
                .build();
        register(buildFurnace);

        final Achievement acquireIron = builder.reset().name("minecraft:acquire_iron")
                .translation(tr("achievement.acquireIron"))
                .description(tr("achievement.acquireIron.desc"))
                .parent(buildFurnace)
                .build();
        register(acquireIron);

        final Achievement buildHoe = builder.reset().name("minecraft:build_hoe")
                .translation(tr("achievement.buildHoe"))
                .description(tr("achievement.buildHoe.desc"))
                .parent(buildWorkbench)
                .build();
        register(buildHoe);

        final Achievement makeBread = builder.reset().name("minecraft:make_bread")
                .translation(tr("achievement.makeBread"))
                .description(tr("achievement.makeBread.desc"))
                .parent(buildHoe)
                .build();
        register(makeBread);

        final Achievement bakeCake = builder.reset().name("minecraft:bake_cake")
                .translation(tr("achievement.bakeCake"))
                .description(tr("achievement.bakeCake.desc"))
                .parent(buildHoe)
                .build();
        register(bakeCake);

        final Achievement buildBetterPickaxe = builder.reset().name("minecraft:build_better_pickaxe")
                .translation(tr("achievement.buildBetterPickaxe"))
                .description(tr("achievement.buildBetterPickaxe.desc"))
                .parent(buildHoe)
                .build();
        register(buildBetterPickaxe);

        final Achievement cookFish = builder.reset().name("minecraft:cook_fish")
                .translation(tr("achievement.cookFish"))
                .description(tr("achievement.cookFish.desc"))
                .parent(buildFurnace)
                .build();
        register(cookFish);

        final Achievement onARail = builder.reset().name("minecraft:on_a_rail")
                .translation(tr("achievement.onARail"))
                .description(tr("achievement.onARail.desc"))
                .parent(acquireIron)
                .build();
        register(onARail);

        final Achievement buildSword = builder.reset().name("minecraft:build_sword")
                .translation(tr("achievement.buildSword"))
                .description(tr("achievement.buildSword.desc"))
                .parent(buildWorkbench)
                .build();
        register(buildSword);

        final Achievement killEnemy = builder.reset().name("minecraft:kill_enemy")
                .translation(tr("achievement.killEnemy"))
                .description(tr("achievement.killEnemy.desc"))
                .parent(buildSword)
                .build();
        register(killEnemy);

        final Achievement killCow = builder.reset().name("minecraft:kill_cow")
                .translation(tr("achievement.killCow"))
                .description(tr("achievement.killCow.desc"))
                .parent(buildSword)
                .build();
        register(killCow);

        final Achievement flyPig = builder.reset().name("minecraft:fly_pig")
                .translation(tr("achievement.flyPig"))
                .description(tr("achievement.flyPig.desc"))
                .parent(killCow)
                .build();
        register(flyPig);

        final Achievement snipeSkeleton = builder.reset().name("minecraft:snipe_skeleton")
                .translation(tr("achievement.snipeSkeleton"))
                .description(tr("achievement.snipeSkeleton.desc"))
                .parent(killEnemy)
                .build();
        register(snipeSkeleton);

        final Achievement getDiamonds = builder.reset().name("minecraft:get_diamonds")
                .translation(tr("achievement.diamonds"))
                .description(tr("achievement.diamonds.desc"))
                .parent(acquireIron)
                .build();
        register(getDiamonds);

        final Achievement diamondsToYou = builder.reset().name("minecraft:diamonds_to_you")
                .translation(tr("achievement.diamondsToYou"))
                .description(tr("achievement.diamondsToYou.desc"))
                .parent(getDiamonds)
                .build();
        register(diamondsToYou);

        final Achievement netherPortal = builder.reset().name("minecraft:nether_portal")
                .translation(tr("achievement.portal"))
                .description(tr("achievement.portal.desc"))
                .parent(getDiamonds)
                .build();
        register(netherPortal);

        final Achievement ghastReturn = builder.reset().name("minecraft:ghast_return")
                .translation(tr("achievement.ghast"))
                .description(tr("achievement.ghast.desc"))
                .parent(netherPortal)
                .build();
        register(ghastReturn);

        final Achievement getBlazeRod = builder.reset().name("minecraft:get_blaze_rod")
                .translation(tr("achievement.blazeRod"))
                .description(tr("achievement.blazeRod.desc"))
                .parent(netherPortal)
                .build();
        register(getBlazeRod);

        final Achievement brewPotion = builder.reset().name("minecraft:brew_potion")
                .translation(tr("achievement.potion"))
                .description(tr("achievement.potion.desc"))
                .parent(getBlazeRod)
                .build();
        register(brewPotion);

        final Achievement endPortal = builder.reset().name("minecraft:end_portal")
                .translation(tr("achievement.theEnd"))
                .description(tr("achievement.theEnd.desc"))
                .parent(getBlazeRod)
                .build();
        register(endPortal);

        final Achievement theEnd = builder.reset().name("minecraft:the_end")
                .translation(tr("achievement.theEnd2"))
                .description(tr("achievement.theEnd2.desc"))
                .parent(endPortal)
                .build();
        register(theEnd);

        final Achievement enchantments = builder.reset().name("minecraft:enchantments")
                .translation(tr("achievement.enchantments"))
                .description(tr("achievement.enchantments.desc"))
                .parent(getDiamonds)
                .build();
        register(enchantments);

        final Achievement overkill = builder.reset().name("minecraft:overkill")
                .translation(tr("achievement.overkill"))
                .description(tr("achievement.overkill.desc"))
                .parent(enchantments)
                .build();
        register(overkill);

        final Achievement bookcase = builder.reset().name("minecraft:bookcase")
                .translation(tr("achievement.bookcase"))
                .description(tr("achievement.bookcase.desc"))
                .parent(enchantments)
                .build();
        register(bookcase);

        final Achievement breedCow = builder.reset().name("minecraft:breed_cow")
                .translation(tr("achievement.breedCow"))
                .description(tr("achievement.breedCow.desc"))
                .parent(killCow)
                .build();
        register(breedCow);

        final Achievement spawnWither = builder.reset().name("minecraft:spawn_wither")
                .translation(tr("achievement.spawnWither"))
                .description(tr("achievement.spawnWither.desc"))
                .parent(theEnd)
                .build();
        register(spawnWither);

        final Achievement killWither = builder.reset().name("minecraft:kill_wither")
                .translation(tr("achievement.killWither"))
                .description(tr("achievement.killWither.desc"))
                .parent(spawnWither)
                .build();
        register(killWither);

        final Achievement fullBeacon = builder.reset().name("minecraft:full_beacon")
                .translation(tr("achievement.fullBeacon"))
                .description(tr("achievement.fullBeacon.desc"))
                .parent(killWither)
                .build();
        register(fullBeacon);

        final Achievement exploreAllBiomes = builder.reset().name("minecraft:explore_all_biomes")
                .translation(tr("achievement.exploreAllBiomes"))
                .description(tr("achievement.exploreAllBiomes.desc"))
                .parent(endPortal)
                .build();
        register(exploreAllBiomes);

        final Achievement overpowered = builder.reset().name("minecraft:overpowered")
                .translation(tr("achievement.overpowered"))
                .description(tr("achievement.overpowered.desc"))
                .parent(buildBetterPickaxe)
                .build();
        register(overpowered);
    }
}
