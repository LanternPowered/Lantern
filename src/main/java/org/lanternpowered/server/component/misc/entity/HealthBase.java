/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
package org.lanternpowered.server.component.misc.entity;

import org.lanternpowered.server.component.ComponentHolder;
import org.lanternpowered.server.component.misc.Health;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.inject.Inject;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.HealEntityEvent;

import com.flowpowered.math.GenericMath;
import com.google.common.collect.Lists;

public abstract class HealthBase implements Health {

    @Inject private ComponentHolder holder;

    private double health;
    private double absorptionHealth;

    @Override
    public double getHealth() {
        return this.health;
    }

    @Override
    public void setHealth(double health) {
        this.health = GenericMath.clamp(health, 0.0, this.getMaxHealth());
    }

    @Override
    public double getAbsorptionHealth() {
        return this.absorptionHealth;
    }

    @Override
    public void setAbsorptionHealth(double absorptionHealth) {
        this.absorptionHealth = Math.max(0.0, absorptionHealth);
    }

    @Override
    public void heal(double health, Cause cause) {
        if (this.holder instanceof Entity) {
            // TODO: Health modifiers, etc.
            HealEntityEvent event = SpongeEventFactory.createHealEntityEvent(LanternGame.get(),
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

    @Override
    public boolean damage(double damage, Cause cause) {
        if (this.holder instanceof Entity) {
            // TODO: Damage modifiers, etc.
            DamageEntityEvent event = SpongeEventFactory.createDamageEntityEvent(LanternGame.get(),
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
