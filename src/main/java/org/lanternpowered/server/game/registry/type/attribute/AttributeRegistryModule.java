/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
package org.lanternpowered.server.game.registry.type.attribute;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.lanternpowered.server.attribute.AttributeTargets;
import org.lanternpowered.server.attribute.LanternAttribute;
import org.lanternpowered.server.attribute.LanternAttributeBuilder;
import org.lanternpowered.server.attribute.LanternAttributes;
import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.text.Text;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@RegistrationDependency(AttributeTargetRegistryModule.class)
public final class AttributeRegistryModule implements AdditionalCatalogRegistryModule<LanternAttribute> {

    @RegisterCatalog(LanternAttributes.class) private final Map<String, LanternAttribute> attributes = Maps.newHashMap();

    @Override
    public void registerAdditionalCatalog(LanternAttribute attribute) {
        checkNotNull(attribute, "attribute");
        final String id = attribute.getId().toLowerCase();
        checkArgument(this.attributes.containsValue(attribute), "The attribute is already registered.");
        checkArgument(this.attributes.containsKey(id), "The id (" + id + ") is already used.");
        this.attributes.put(id, checkNotNull(attribute, "attribute"));
    }

    @Override
    public void registerDefaults() {
        Map<String, LanternAttribute> mappings = Maps.newHashMap();
        mappings.put("generic_armor", this.defaultAttribute(
                "generic.armor", 0.0, 0.0, Double.MAX_VALUE, AttributeTargets.GENERIC));
        mappings.put("generic_max_health", this.defaultAttribute(
                "generic.maxHealth", 20.0, 0.0, Double.MAX_VALUE, AttributeTargets.GENERIC));
        mappings.put("generic_follow_range", this.defaultAttribute(
                "generic.followRange", 32.0D, 0.0D, 2048.0D, AttributeTargets.GENERIC));
        mappings.put("generic_attack_damage", this.defaultAttribute(
                "generic.attackDamage", 2.0D, 0.0D, Double.MAX_VALUE, AttributeTargets.GENERIC));
        mappings.put("generic_attack_speed", this.defaultAttribute(
                "generic.attackSpeed", 4.0, 0.0, 1024.0D, AttributeTargets.GENERIC));
        mappings.put("generic_knockback_resistance", this.defaultAttribute(
                "generic.knockbackResistance", 0.0D, 0.0D, 1.0D, AttributeTargets.GENERIC));
        mappings.put("generic_movement_speed", this.defaultAttribute(
                "generic.movementSpeed", 0.7D, 0.0D, Double.MAX_VALUE, AttributeTargets.GENERIC));
        mappings.put("horse_jump_strength", this.defaultAttribute(
                "horse.jumpStrength", 0.7D, 0.0D, 2.0D, AttributeTargets.HORSE));
        mappings.put("zombie_spawn_reinforcements", this.defaultAttribute(
                "zombie.spawnReinforcements", 0.0D, 0.0D, 1.0D, AttributeTargets.ZOMBIE));
        mappings.forEach((key, value) -> {
            this.attributes.put(key, value);
            this.attributes.put(value.getId(), value);
        });
    }

    private LanternAttribute defaultAttribute(String id, double def, double min, double max, Predicate<DataHolder> targets) {
        return new LanternAttributeBuilder().id(id).defaultValue(def).maximum(max).minimum(min).targets(targets)
                .name(Text.of(LanternGame.get().getRegistry().getTranslationManager().get("attribute.name." + id)))
                .build();
    }

    @Override
    public Optional<LanternAttribute> getById(String id) {
        if (checkNotNull(id, "identifier").indexOf(':') == -1) {
            id = "minecraft:" + id;
        }
        return Optional.ofNullable(this.attributes.get(id.toLowerCase()));
    }

    @Override
    public Collection<LanternAttribute> getAll() {
        return ImmutableList.copyOf(this.attributes.values());
    }

}
