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
import org.lanternpowered.server.statistic.LanternStatisticBuilder;
import org.lanternpowered.server.statistic.achievement.LanternAchievement;
import org.lanternpowered.server.statistic.achievement.LanternAchievementBuilder;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.statistic.Statistic;
import org.spongepowered.api.statistic.StatisticGroups;
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
        final Achievement openInventory = vanillaBuilder("minecraft:open_inventory", "openInventory")
                .build();
        register(openInventory);

        final Achievement mineWood = vanillaBuilder("minecraft:mine_wood", "mineWood")
                .parent(openInventory)
                .build();
        register(mineWood);

        final Achievement buildWorkbench = vanillaBuilder("minecraft:build_workbench", "buildWorkBench")
                .parent(mineWood)
                .build();
        register(buildWorkbench);

        final Achievement buildPickaxe = vanillaBuilder("minecraft:build_pickaxe", "buildPickaxe")
                .parent(buildWorkbench)
                .build();
        register(buildPickaxe);

        final Achievement buildFurnace = vanillaBuilder("minecraft:build_furnace", "buildFurnace")
                .parent(buildPickaxe)
                .build();
        register(buildFurnace);

        final Achievement acquireIron = vanillaBuilder("minecraft:acquire_iron", "acquireIron")
                .parent(buildFurnace)
                .build();
        register(acquireIron);

        final Achievement buildHoe = vanillaBuilder("minecraft:build_hoe", "buildHoe")
                .parent(buildWorkbench)
                .build();
        register(buildHoe);

        final Achievement makeBread = vanillaBuilder("minecraft:make_bread", "makeBread")
                .parent(buildHoe)
                .build();
        register(makeBread);

        final Achievement bakeCake = vanillaBuilder("minecraft:bake_cake", "bakeCake")
                .parent(buildHoe)
                .build();
        register(bakeCake);

        final Achievement buildBetterPickaxe = vanillaBuilder("minecraft:build_better_pickaxe", "buildBetterPickaxe")
                .parent(buildHoe)
                .build();
        register(buildBetterPickaxe);

        final Achievement cookFish = vanillaBuilder("minecraft:cook_fish", "cookFish")
                .parent(buildFurnace)
                .build();
        register(cookFish);

        final Achievement onARail = vanillaBuilder("minecraft:on_a_rail", "onARail")
                .parent(acquireIron)
                .build();
        register(onARail);

        final Achievement buildSword = vanillaBuilder("minecraft:build_sword", "buildSword")
                .parent(buildWorkbench)
                .build();
        register(buildSword);

        final Achievement killEnemy = vanillaBuilder("minecraft:kill_enemy", "killEnemy")
                .parent(buildSword)
                .build();
        register(killEnemy);

        final Achievement killCow = vanillaBuilder("minecraft:kill_cow", "killCow")
                .parent(buildSword)
                .build();
        register(killCow);

        final Achievement flyPig = vanillaBuilder("minecraft:fly_pig", "flyPig")
                .parent(killCow)
                .build();
        register(flyPig);

        final Achievement snipeSkeleton = vanillaBuilder("minecraft:snipe_skeleton", "snipeSkeleton")
                .parent(killEnemy)
                .build();
        register(snipeSkeleton);

        final Achievement getDiamonds = vanillaBuilder("minecraft:get_diamonds", "diamonds")
                .parent(acquireIron)
                .build();
        register(getDiamonds);

        final Achievement diamondsToYou = vanillaBuilder("minecraft:diamonds_to_you", "diamondsToYou")
                .parent(getDiamonds)
                .build();
        register(diamondsToYou);

        final Achievement netherPortal = vanillaBuilder("minecraft:nether_portal", "portal")
                .parent(getDiamonds)
                .build();
        register(netherPortal);

        final Achievement ghastReturn = vanillaBuilder("minecraft:ghast_return", "ghast")
                .parent(netherPortal)
                .build();
        register(ghastReturn);

        final Achievement getBlazeRod = vanillaBuilder("minecraft:get_blaze_rod", "blazeRod")
                .parent(netherPortal)
                .build();
        register(getBlazeRod);

        final Achievement brewPotion = vanillaBuilder("minecraft:brew_potion", "potion")
                .parent(getBlazeRod)
                .build();
        register(brewPotion);

        final Achievement endPortal = vanillaBuilder("minecraft:end_portal", "theEnd")
                .parent(getBlazeRod)
                .build();
        register(endPortal);

        final Achievement theEnd = vanillaBuilder("minecraft:the_end", "theEnd2")
                .parent(endPortal)
                .build();
        register(theEnd);

        final Achievement enchantments = vanillaBuilder("minecraft:enchantments", "enchantments")
                .parent(getDiamonds)
                .build();
        register(enchantments);

        final Achievement overkill = vanillaBuilder("minecraft:overkill", "overkill")
                .parent(enchantments)
                .build();
        register(overkill);

        final Achievement bookcase = vanillaBuilder("minecraft:bookcase", "bookcase")
                .parent(enchantments)
                .build();
        register(bookcase);

        final Achievement breedCow = vanillaBuilder("minecraft:breed_cow", "breedCow")
                .parent(killCow)
                .build();
        register(breedCow);

        final Achievement spawnWither = vanillaBuilder("minecraft:spawn_wither", "spawnWither")
                .parent(theEnd)
                .build();
        register(spawnWither);

        final Achievement killWither = vanillaBuilder("minecraft:kill_wither", "killWither")
                .parent(spawnWither)
                .build();
        register(killWither);

        final Achievement fullBeacon = vanillaBuilder("minecraft:full_beacon", "fullBeacon")
                .parent(killWither)
                .build();
        register(fullBeacon);

        final Achievement exploreAllBiomes = vanillaBuilder("minecraft:explore_all_biomes", "exploreAllBiomes")
                .parent(endPortal)
                .build();
        register(exploreAllBiomes);

        final Achievement overpowered = vanillaBuilder("minecraft:overpowered", "overpowered")
                .parent(buildBetterPickaxe)
                .build();
        register(overpowered);
    }

    private LanternAchievementBuilder vanillaBuilder(String identifier, String unlocalizedName) {
        final String internalId = String.format("achievement.%s", unlocalizedName);
        final Statistic statistic = ((LanternStatisticBuilder) new LanternStatisticBuilder()
                .internalId(internalId)
                .name(identifier)
                .group(StatisticGroups.HIDDEN)
                .translation(tr(internalId)))
                .build();
        StatisticRegistryModule.get().register(statistic);
        return new LanternAchievementBuilder()
                .internalId(internalId)
                .name(identifier)
                .translation(tr(internalId))
                .description(tr("%s.desc", internalId))
                .sourceStatistic(statistic)
                .targetValue(1);
    }
}
