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
package org.lanternpowered.server.game.registry.type.scoreboard;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.lanternpowered.server.scoreboard.LanternCollisionRule;
import org.spongepowered.api.registry.CatalogRegistryModule;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

// TODO: Use the api class once available
public final class CollisionRuleRegistryModule implements CatalogRegistryModule<LanternCollisionRule> {

    private final Map<String, LanternCollisionRule> collisionRules = Maps.newHashMap();

    @Override
    public void registerDefaults() {
        Map<String, LanternCollisionRule> types = Maps.newHashMap();
        types.put("always", new LanternCollisionRule("always"));
        types.put("push_own_team", new LanternCollisionRule("pushOwnTeam"));
        types.put("push_other_teams", new LanternCollisionRule("pushOtherTeams"));
        types.put("never", new LanternCollisionRule("never"));
        types.entrySet().forEach(entry -> {
            this.collisionRules.put(entry.getValue().getId(), entry.getValue());
            this.collisionRules.put(entry.getKey(), entry.getValue());
        });
    }

    @Override
    public Optional<LanternCollisionRule> getById(String id) {
        return Optional.ofNullable(this.collisionRules.get(checkNotNull(id).toLowerCase()));
    }

    @Override
    public Collection<LanternCollisionRule> getAll() {
        return ImmutableSet.copyOf(this.collisionRules.values());
    }

}
