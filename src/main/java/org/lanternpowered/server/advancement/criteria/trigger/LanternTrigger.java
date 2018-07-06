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
package org.lanternpowered.server.advancement.criteria.trigger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;
import org.lanternpowered.api.catalog.CatalogKeys;
import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.advancement.LanternPlayerAdvancements;
import org.lanternpowered.server.advancement.criteria.progress.AbstractCriterionProgress;
import org.lanternpowered.server.catalog.DefaultCatalogType;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.util.ToStringHelper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.criteria.AdvancementCriterion;
import org.spongepowered.api.advancement.criteria.ScoreCriterionProgress;
import org.spongepowered.api.advancement.criteria.trigger.FilteredTrigger;
import org.spongepowered.api.advancement.criteria.trigger.FilteredTriggerConfiguration;
import org.spongepowered.api.advancement.criteria.trigger.Trigger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.advancement.CriterionEvent;
import org.spongepowered.api.event.cause.Cause;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
public class LanternTrigger<C extends FilteredTriggerConfiguration> extends DefaultCatalogType implements Trigger<C> {

    private final Class<C> configType;
    private final Function<JsonObject, C> configConstructor;
    @Nullable private final Consumer<CriterionEvent.Trigger<C>> eventHandler;
    private final TypeToken<C> configTypeToken;

    // All the criteria progress that could be triggered by this trigger
    private final Multimap<LanternPlayerAdvancements, AbstractCriterionProgress> progress = HashMultimap.create();

    LanternTrigger(LanternTriggerBuilder<C> builder) {
        super(CatalogKeys.activePlugin(builder.id, builder.name == null ? builder.id : builder.name));
        this.configTypeToken = TypeToken.of(builder.configType);
        this.configType = builder.configType;
        this.configConstructor = builder.constructor;
        this.eventHandler = builder.eventHandler;
    }

    @Override
    public Class<C> getConfigurationType() {
        return this.configType;
    }

    @Override
    public void trigger() {
        trigger(Sponge.getServer().getOnlinePlayers());
    }

    @Override
    public void trigger(Iterable<Player> players) {
        players.forEach(this::trigger);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void trigger(Player player) {
        final LanternPlayerAdvancements playerAdvancements = ((LanternPlayer) player).getAdvancementsProgress();
        final Collection<AbstractCriterionProgress> collection = this.progress.get(playerAdvancements);
        if (!collection.isEmpty()) {
            final Cause cause = CauseStack.current().getCurrentCause();
            for (AbstractCriterionProgress progress : new ArrayList<>(this.progress.get(playerAdvancements))) {
                final Advancement advancement = progress.getAdvancementProgress().getAdvancement();
                final AdvancementCriterion criterion = progress.getCriterion();
                final FilteredTrigger filteredTrigger = criterion.getTrigger().get();

                final CriterionEvent.Trigger event = SpongeEventFactory.createCriterionEventTrigger(cause, advancement, criterion,
                        this.configTypeToken, player, filteredTrigger, this.eventHandler != null);
                if (this.eventHandler != null) {
                    this.eventHandler.accept(event);
                }
                Sponge.getEventManager().post(event);

                if (event.getResult()) {
                    if (progress instanceof ScoreCriterionProgress) {
                        ((ScoreCriterionProgress) progress).add(1);
                    } else {
                        progress.grant();
                    }
                }
            }
        }
    }

    public void add(LanternPlayerAdvancements playerAdvancements, AbstractCriterionProgress criterionProgress) {
        this.progress.put(playerAdvancements, criterionProgress);
    }

    public void remove(LanternPlayerAdvancements playerAdvancements, AbstractCriterionProgress criterionProgress) {
        this.progress.remove(playerAdvancements, criterionProgress);
    }

    public Function<JsonObject, C> getConfigConstructor() {
        return this.configConstructor;
    }

    @Nullable
    public Consumer<CriterionEvent.Trigger<C>> getEventHandler() {
        return this.eventHandler;
    }

    @Override
    public ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("configType", this.configType.getName());
    }
}
