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
package org.lanternpowered.test;

import com.flowpowered.math.vector.Vector2d;
import com.google.inject.Inject;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.slf4j.Logger;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.AdvancementTree;
import org.spongepowered.api.advancement.AdvancementTypes;
import org.spongepowered.api.advancement.DisplayInfo;
import org.spongepowered.api.advancement.TreeLayoutElement;
import org.spongepowered.api.advancement.criteria.AdvancementCriterion;
import org.spongepowered.api.advancement.criteria.ScoreAdvancementCriterion;
import org.spongepowered.api.advancement.criteria.trigger.FilteredTrigger;
import org.spongepowered.api.advancement.criteria.trigger.FilteredTriggerConfiguration;
import org.spongepowered.api.advancement.criteria.trigger.Trigger;
import org.spongepowered.api.block.tileentity.carrier.Furnace;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.advancement.AdvancementTreeEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nullable;

@Plugin(id = "advancement_test", name = "Advancement Test")
public class AdvancementTest {

    @Inject private Logger logger;

    private AdvancementTree advancementTree;
    private Advancement rootAdvancement;

    private ScoreAdvancementCriterion loginAFewTimesCriterion;
    private Advancement loginAFewTimesAdvancement;

    private Advancement firstTimeAdvancement;
    private Advancement secondTimeAdvancement;
    private Advancement testAdvancement;

    private Advancement cookDirtAdvancement;
    @Nullable private Advancement suicidalAdvancement;

    @Inject private PluginContainer pluginContainer;

    private Trigger<MyTriggerConfig> trigger;

    @ConfigSerializable
    public static class MyTriggerConfig implements FilteredTriggerConfiguration {

        @Setting("chance")
        private float chance = 0.5f;
    }

    @Listener
    public void onRegisterTriggers(GameRegistryEvent.Register<Trigger> event) {
        this.trigger = Trigger.builder()
                .typeSerializableConfig(MyTriggerConfig.class)
                .listener(triggerEvent -> {
                    final float value = ThreadLocalRandom.current().nextFloat();
                    final float chance = triggerEvent.getTrigger().getConfiguration().chance;
                    triggerEvent.setResult(value < chance);
                })
                .id("my_trigger")
                .build();
        event.register(this.trigger);
    }

    @Listener
    public void onRegisterAdvancementTrees(GameRegistryEvent.Register<AdvancementTree> event) {
        // Create the advancement tree
        this.advancementTree = AdvancementTree.builder()
                .rootAdvancement(this.rootAdvancement)
                .background("minecraft:textures/blocks/dirt.png")
                .id("dirt")
                .build();
        event.register(this.advancementTree);
    }

    @Listener
    public void onRegisterAdvancements(GameRegistryEvent.Register<Advancement> event) {
        // Create the root advancement
        this.rootAdvancement = Advancement.builder()
                .displayInfo(DisplayInfo.builder()
                        .icon(ItemTypes.DIRT)
                        .title(Text.of("Random advancements!"))
                        .description(Text.of("Some random and useless advancements."))
                        .build())
                .criterion(AdvancementCriterion.EMPTY)
                .id("random_root")
                .build();
        event.register(this.rootAdvancement);

        this.testAdvancement = Advancement.builder()
                .parent(this.rootAdvancement)
                .displayInfo(DisplayInfo.builder()
                        .icon(ItemTypes.BARRIER)
                        .title(Text.of("Test?"))
                        .build())
                .criterion(AdvancementCriterion.DUMMY)
                .id("random_test")
                .build();
        event.register(this.testAdvancement);

        this.firstTimeAdvancement = Advancement.builder()
                .parent(this.rootAdvancement)
                .displayInfo(DisplayInfo.builder()
                        .icon(ItemTypes.APPLE)
                        .title(Text.of("First time?"))
                        .description(Text.of("Login for the first time"))
                        .build())
                .criterion(AdvancementCriterion.DUMMY)
                .id("random_first_login")
                .build();
        event.register(this.firstTimeAdvancement);
        this.secondTimeAdvancement = Advancement.builder()
                .parent(this.firstTimeAdvancement)
                .displayInfo(DisplayInfo.builder()
                        .icon(ItemTypes.GOLDEN_APPLE)
                        .title(Text.of("Welcome back?"))
                        .description(Text.of("Login for the second time"))
                        .build())
                .criterion(AdvancementCriterion.DUMMY)
                .id("random_second_login")
                .build();
        event.register(this.secondTimeAdvancement);

        // Create the break dirt advancement and criterion
        this.loginAFewTimesCriterion = ScoreAdvancementCriterion.builder()
                .goal(10)
                .name("times")
                .build();
        this.loginAFewTimesAdvancement = Advancement.builder()
                .parent(this.secondTimeAdvancement)
                .displayInfo(DisplayInfo.builder()
                        .icon(ItemTypes.NETHER_STAR)
                        .title(Text.of("Login a few times"))
                        .announceToChat(true)
                        .build())
                .criterion(this.loginAFewTimesCriterion)
                .id("random_login_a_few_times")
                .build();
        event.register(this.loginAFewTimesAdvancement);

        // Create the cook dirt advancement
        this.cookDirtAdvancement = Advancement.builder()
                .parent(this.rootAdvancement)
                .criterion(AdvancementCriterion.DUMMY)
                .displayInfo(DisplayInfo.builder()
                        .icon(ItemTypes.FURNACE)
                        .title(Text.of("Dirty cook"))
                        .announceToChat(true)
                        .description(Text.of("Try to cook dirt"))
                        .type(AdvancementTypes.CHALLENGE)
                        .build())
                .id("dirt_cooker")
                .build();
        event.register(this.cookDirtAdvancement);

        event.register(Advancement.builder()
                .parent(this.secondTimeAdvancement)
                .criterion(ScoreAdvancementCriterion.builder()
                        .goal(5)
                        .name("times")
                        .trigger(FilteredTrigger.builder()
                                .type(this.trigger)
                                .config(new MyTriggerConfig())
                                .build())
                        .build())
                .displayInfo(DisplayInfo.builder()
                        .icon(ItemTypes.END_CRYSTAL)
                        .announceToChat(true)
                        .title(Text.of("Hope for the best"))
                        .description(Text.of("Login a few times, hope for the best"))
                        .type(AdvancementTypes.GOAL)
                        .build())
                .id("random_login")
                .build());

        this.suicidalAdvancement = null;
        event.getRegistryModule().get(CatalogKey.of("minecraft", "adventure_root")).ifPresent(parent -> {
            // Create the suicidal advancement
            this.suicidalAdvancement = Advancement.builder()
                    .parent(parent)
                    .criterion(AdvancementCriterion.DUMMY)
                    .displayInfo(DisplayInfo.builder()
                            .icon(ItemTypes.TNT)
                            .title(Text.of("Suicidal?"))
                            .description(Text.of("Put TNT in a burning furnace"))
                            .type(AdvancementTypes.CHALLENGE)
                            .hidden(true)
                            .build())
                    .id("suicidal")
                    .build();
            event.register(this.suicidalAdvancement);
        });
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        final Player player = event.getTargetEntity();
        // Do this here for now, no block break event
        player.getProgress(this.loginAFewTimesAdvancement).get(this.loginAFewTimesCriterion).get().add(1);
        this.trigger.trigger(player);

        player.getProgress(this.testAdvancement).grant();
        if (player.getProgress(this.firstTimeAdvancement).achieved()) {
            player.getProgress(this.secondTimeAdvancement).grant();
        } else {
            player.getProgress(this.firstTimeAdvancement).grant();
        }
    }

    @Listener
    public void onGenerateTreeLayout(AdvancementTreeEvent.GenerateLayout event) {
        if (event.getTree() != this.advancementTree) {
            return;
        }
        this.logger.info("Updating advancement tree layout...");
        // Make the tree start at y 0, for every level within the tree
        // The min y position mapped by the used x positions
        // For example:
        //    |- y
        // x -|- z
        //    |- w
        // to
        // x -|- y
        //    |- z
        //    |- w
        final Map<Double, Double> values = new HashMap<>();
        for (TreeLayoutElement element : event.getLayout().getElements()) {
            final Vector2d pos = element.getPosition();
            if (!values.containsKey(pos.getX()) || pos.getY() < values.get(pos.getX())) {
                values.put(pos.getX(), pos.getY());
            }
        }
        for (TreeLayoutElement element : event.getLayout().getElements()) {
            final Vector2d pos = element.getPosition();
            element.setPosition(pos.getX(), pos.getY() - values.get(pos.getX()));
        }
        /*
        // Rotate the advancement tree
        // The lines are currently drawn wrongly, that might be something
        // for later as it involves "tricking" the client
        double maxY = 0;
        for (TreeLayoutElement element : event.getLayout().getElements()) {
            maxY = Math.max(maxY, element.getPosition().getY());
        }
        for (TreeLayoutElement element : event.getLayout().getElements()) {
            final Vector2d pos = element.getPosition();
            element.setPosition(maxY - pos.getY(), pos.getX());
        }
        */
    }

    @SuppressWarnings("ConstantConditions")
    @Listener
    public void onChangeInventory(ChangeInventoryEvent event, @First Player player,
            @Getter("getTargetInventory") CarriedInventory<?> container) {
        if (!container.getName().get().equals("Furnace")) {
            return;
        }
        final Carrier carrier = container.getCarrier().orElse(null);
        if (!(carrier instanceof Furnace)) {
            return;
        }
        final Furnace furnace = (Furnace) carrier;
        final int passed = furnace.passedBurnTime().get();
        final int max = furnace.maxBurnTime().get();
        if (max <= 0 || passed >= max) {
            return;
        }
        for (SlotTransaction transaction : event.getTransactions()) {
            if (container.getProperty(transaction.getSlot(), SlotIndex.class).get().getValue() == 0) {
                if (transaction.getFinal().getType() == ItemTypes.DIRT) {
                    player.getProgress(this.cookDirtAdvancement).grant();
                } else if (this.suicidalAdvancement != null && (transaction.getFinal().getType() == ItemTypes.TNT ||
                        transaction.getFinal().getType() == ItemTypes.TNT_MINECART)) {
                    player.getProgress(this.suicidalAdvancement).grant();
                    /*
                    final Explosion explosion = Explosion.builder()
                            .location(furnace.getLocation())
                            .shouldBreakBlocks(true)
                            .canCauseFire(true)
                            .shouldDamageEntities(true)
                            .radius(7)
                            .build();
                    explosion.getWorld().triggerExplosion(explosion);
                    */
                }
            }
        }
    }
}
