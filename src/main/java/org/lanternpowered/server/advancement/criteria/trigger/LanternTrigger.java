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

import com.google.common.base.MoreObjects;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;
import org.lanternpowered.server.catalog.PluginCatalogType;
import org.lanternpowered.server.event.CauseStack;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.advancement.criteria.trigger.FilteredTriggerConfiguration;
import org.spongepowered.api.advancement.criteria.trigger.Trigger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.advancement.CriterionEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
public class LanternTrigger<C extends FilteredTriggerConfiguration> extends PluginCatalogType.Base implements Trigger<C> {

    private final Class<C> configType;
    private final Function<JsonObject, C> configConstructor;
    @Nullable private final Consumer<CriterionEvent.Trigger<C>> eventHandler;
    private final TypeToken<C> configTypeToken;

    LanternTrigger(LanternTriggerBuilder<C> builder) {
        super(CauseStack.current().first(PluginContainer.class).get().getId(), builder.id,
                builder.name == null ? builder.id : builder.name);
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

    @Override
    public void trigger(Player player) {
        final Cause cause = CauseStack.current().getCurrentCause();

        // SpongeEventFactory.createCriterionEventTrigger()
    }

    public Function<JsonObject, C> getConfigConstructor() {
        return this.configConstructor;
    }

    @Nullable
    public Consumer<CriterionEvent.Trigger<C>> getEventHandler() {
        return this.eventHandler;
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("configType", this.configType.getName());
    }
}
