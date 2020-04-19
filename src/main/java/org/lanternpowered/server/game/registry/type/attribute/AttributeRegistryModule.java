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
package org.lanternpowered.server.game.registry.type.attribute;

import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.attribute.LanternAttribute;
import org.lanternpowered.server.attribute.LanternAttributeBuilder;
import org.lanternpowered.server.attribute.LanternAttributes;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.text.Text;

import java.util.function.Predicate;

@RegistrationDependency(AttributeTargetRegistryModule.class)
public final class AttributeRegistryModule extends AdditionalPluginCatalogRegistryModule<LanternAttribute> {

    public AttributeRegistryModule() {
        super(LanternAttributes.class);
    }

    @Override
    public void registerDefaults() {
        //final Map<String, LanternAttribute> mappings = new HashMap<>();
        CauseStack.current().pushCause(Lantern.getMinecraftPlugin());
        /*
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
                "zombie.spawnReinforcements", 0.0D, 0.0D, 1.0D, AttributeTargets.ZOMBIE));*/
        CauseStack.current().popCause();
    }

    private LanternAttribute defaultAttribute(String id, double def, double min, double max, Predicate<DataHolder> targets) {
        return new LanternAttributeBuilder().id(id).defaultValue(def).maximum(max).minimum(min).targets(targets)
                .name(Text.of(Lantern.getGame().getRegistry().getTranslationManager().get("attribute.name." + id)))
                .build();
    }
}
