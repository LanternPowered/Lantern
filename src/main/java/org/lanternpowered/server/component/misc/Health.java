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
package org.lanternpowered.server.component.misc;

import com.flowpowered.math.GenericMath;
import com.google.common.collect.Lists;
import org.lanternpowered.server.attribute.LanternAttributes;
import org.lanternpowered.server.component.Component;
import org.lanternpowered.server.component.ComponentHolder;
import org.lanternpowered.server.inject.Inject;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.HealEntityEvent;

public final class Health implements Component {

    @Inject private ComponentHolder holder;
    @Inject private Attributes attributes;

    private double maxHealth = 1.0;
    private double health = this.maxHealth;
    private double absorptionHealth = 0.0;

    /**
     * Gets the maximum amount of health.
     * 
     * @return the max health
     */
    public double getMaxHealth() {
        if (this.attributes != null) {
            return this.attributes.getValue(LanternAttributes.MAX_HEALTH);
        } else {
            return this.maxHealth;
        }
    }

    /**
     * Sets the maximum amount of health.
     * 
     * @param maxHealth the max health
     */
    public void setMaxHealth(double maxHealth) {
        if (this.attributes != null) {
            // TODO
        } else {
            this.maxHealth = maxHealth;
        }
    }

    /**
     * Gets the health.
     * 
     * @return the health
     */
    public double getHealth() {
        return this.health;
    }

    /**
     * Sets the health.
     * 
     * @param health the health
     */
    public void setHealth(double health) {
        this.health = GenericMath.clamp(health, 0.0, this.getMaxHealth());
    }

    public double getAbsorptionHealth() {
        return this.absorptionHealth;
    }

    public void setAbsorptionHealth(double absorptionHealth) {
        this.absorptionHealth = Math.max(0.0, absorptionHealth);
    }

    /**
     * Heals the component with the specified amount of
     * health and a specific cause.
     * 
     * @param health the health
     * @param cause the cause
     */
    public void heal(double health, Cause cause) {
        if (this.holder instanceof Entity) {
            // TODO: Health modifiers, etc.
            HealEntityEvent event = SpongeEventFactory.createHealEntityEvent(
                    cause, Lists.newArrayList(), (Entity) this.holder, health);
            if (event.isCancelled()) {
                return;
            }
            health = event.getFinalHealAmount();
        }
        if (health > 0) {
            this.setHealth(this.getHealth() + health);
        }
    }

    /**
     * Damages the component with the specified amount of
     * damage and a specific cause.
     * 
     * @param damage the damage
     * @param damageSource the source of damage
     * @param cause the cause
     * @return whether it was successful
     */
    public boolean damage(double damage, DamageSource damageSource, Cause cause) {
        if (this.holder instanceof Entity) {
            // TODO: Damage modifiers, etc.
            DamageEntityEvent event = SpongeEventFactory.createDamageEntityEvent(
                    cause, Lists.newArrayList(), (Entity) this.holder, damage);
            // TODO: Not cancellable?
            damage = event.getFinalDamage();
        }
        if (damage > 0) {
            double health = this.getHealth() - damage;
            this.setHealth(health);
            if (health <= 0.0) {
                // TODO: Notify stuff
            }
            return true;
        }
        return false;
    }
}
