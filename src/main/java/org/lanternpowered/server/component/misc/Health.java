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
package org.lanternpowered.server.component.misc;

import org.lanternpowered.server.component.Component;
import org.spongepowered.api.event.cause.Cause;

public interface Health extends Component {

    /**
     * Damages the component with the specified amount of
     * damage and a specific cause.
     * 
     * @param damage the damage
     * @param cause the cause
     */
    void damage(double damage, Cause cause);

    /**
     * Heals the component with the specified amount of
     * health and a specific cause.
     * 
     * @param health the health
     * @param cause the cause
     */
    void heal(double health, Cause cause);

    /**
     * Gets the health.
     * 
     * @return the health
     */
    double getHealth();

    /**
     * Sets the health.
     * 
     * @param health the health
     */
    void setHealth(double health);

    /**
     * Gets the maximum amount of health.
     * 
     * @return the max health
     */
    double getMaxHealth();

    /**
     * Sets the maximum amount of health.
     * 
     * @param maxHealth the max health
     */
    void setMaxHealth(double maxHealth);
}
