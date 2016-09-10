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
package org.lanternpowered.server.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.value.immutable.ImmutableBoundedValue;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.UUID;

@NonnullByDefault
public class LanternEntityLiving extends LanternEntity implements Living {

    private Vector3d headRotation = Vector3d.ZERO;

    public LanternEntityLiving(UUID uniqueId) {
        super(uniqueId);
    }

    public LanternEntityLiving() {
        super();
    }

    @Override
    public void registerKeys() {
        super.registerKeys();
        this.registerKey(Keys.REMAINING_AIR, 200).applyAttachedValueProcessor(builder -> builder
                .offerHandler((key, container, elementHolder, element) -> {
                    int maxAir = container.getElementHolder(Keys.MAX_AIR).get();
                    if (element > maxAir) {
                        return DataTransactionResult.errorResult(buildRemainingAirValue(elementHolder.get(), maxAir));
                    } else {
                        int oldAir = elementHolder.set(element);
                        return DataTransactionResult.successReplaceResult(
                                buildRemainingAirValue(element, maxAir),
                                buildRemainingAirValue(oldAir, maxAir));
                    }
                })
                .failAlwaysRemoveHandler());
        this.registerKey(Keys.MAX_AIR, 200).applyAttachedValueProcessor(builder -> builder
                .valueBuilder((key, container, element) -> Sponge.getRegistry().getValueFactory().createBoundedValueBuilder(Keys.MAX_AIR)
                        .actualValue(element)
                        .minimum(0)
                        .maximum(Integer.MAX_VALUE)
                        .build())
                .failAlwaysRemoveHandler());
        this.registerKey(Keys.HEALTH, 20.0).applyAttachedValueProcessor(builder -> builder
                .offerHandler((key, container, elementHolder, element) -> {
                    double maxHealth = container.getElementHolder(Keys.MAX_HEALTH).get();
                    if (element > maxHealth) {
                       return DataTransactionResult.errorResult(buildHealthValue(elementHolder.get(), maxHealth));
                    } else {
                        double oldHealth = elementHolder.set(element);
                        return DataTransactionResult.successReplaceResult(
                                buildHealthValue(element, maxHealth),
                                buildHealthValue(oldHealth, maxHealth));
                    }
                })
                .failAlwaysRemoveHandler());
        this.registerKey(Keys.MAX_HEALTH, 20.0).applyAttachedValueProcessor(builder -> builder
                .valueBuilder((key, container, element) -> Sponge.getRegistry().getValueFactory().createBoundedValueBuilder(Keys.MAX_HEALTH)
                        .actualValue(element)
                        .minimum(1.0)
                        .maximum((double) Float.MAX_VALUE)
                        .build())
                .failAlwaysRemoveHandler());
    }

    private static ImmutableBoundedValue<Integer> buildRemainingAirValue(int air, int maxAir) {
        return Sponge.getRegistry().getValueFactory().createBoundedValueBuilder(Keys.REMAINING_AIR)
                .actualValue(air)
                .minimum(0)
                .maximum(maxAir)
                .build().asImmutable();
    }

    private static ImmutableBoundedValue<Double> buildHealthValue(double health, double maxHealth) {
        return Sponge.getRegistry().getValueFactory().createBoundedValueBuilder(Keys.HEALTH)
                .actualValue(health)
                .minimum(0.0)
                .maximum(maxHealth)
                .build().asImmutable();
    }

    protected void setRawHeadRotation(Vector3d rotation) {
        this.headRotation = checkNotNull(rotation, "rotation");
    }

    @Override
    public Vector3d getHeadRotation() {
        return this.headRotation;
    }

    @Override
    public void setHeadRotation(Vector3d rotation) {
        this.setRawHeadRotation(rotation);
    }

    @Override
    public Text getTeamRepresentation() {
        return Text.of(this.getUniqueId().toString());
    }
}
