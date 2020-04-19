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
package org.lanternpowered.server.game.registry.type.world;

import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.world.rules.RuleTypes;
import org.spongepowered.api.registry.RegistryModule;
import org.spongepowered.api.world.gamerule.DefaultGameRules;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Set;

public final class DefaultGameRulesRegistryModule implements RegistryModule {

    private Set<String> rules;

    @Override
    public void registerDefaults() {
       final ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        for (Field field : DefaultGameRules.class.getFields()) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType() == String.class) {
                try {
                    builder.add((String) field.get(null));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        this.rules = builder.build();
        RuleTypes.init();
    }

    public Collection<String> get() {
        return this.rules;
    }

}
