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
package org.lanternpowered.server.inventory.vanilla.block;

import org.lanternpowered.server.block.tile.ITileEntityInventory;
import org.lanternpowered.server.inventory.AbstractOrderedInventory;
import org.lanternpowered.server.inventory.CarrierReference;
import org.lanternpowered.server.inventory.client.ClientContainer;
import org.lanternpowered.server.inventory.client.ContainerProperties;
import org.lanternpowered.server.inventory.type.slot.LanternFuelSlot;
import org.lanternpowered.server.inventory.type.slot.LanternInputSlot;
import org.lanternpowered.server.inventory.type.slot.LanternOutputSlot;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.query.QueryOperation;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

import java.util.Optional;

public class FurnaceInventory extends AbstractOrderedInventory implements ITileEntityInventory {

    private static final class Holder {

        private static final QueryOperation<?> INPUT_SLOT_OPERATION =
                QueryOperationTypes.INVENTORY_TYPE.of(LanternInputSlot.class);
        private static final QueryOperation<?> FUEL_SLOT_OPERATION =
                QueryOperationTypes.INVENTORY_TYPE.of(LanternFuelSlot.class);
        private static final QueryOperation<?> OUTPUT_SLOT_OPERATION =
                QueryOperationTypes.INVENTORY_TYPE.of(LanternOutputSlot.class);
    }

    private final CarrierReference<Carrier> carrierReference = CarrierReference.of(Carrier.class);

    private LanternInputSlot inputSlot;
    private LanternFuelSlot fuelSlot;
    private LanternOutputSlot outputSlot;

    private boolean useCachedProgress;

    private double smeltProgress;
    private double fuelProgress;

    public LanternInputSlot getInputSlot() {
        return this.inputSlot;
    }

    public LanternFuelSlot getFuelSlot() {
        return this.fuelSlot;
    }

    public LanternOutputSlot getOutputSlot() {
        return this.outputSlot;
    }

    public void enableCachedProgress() {
        this.useCachedProgress = true;
    }

    public void resetCachedProgress() {
        this.smeltProgress = -1;
        this.fuelProgress = -1;
    }

    @Override
    protected void init() {
        super.init();

        resetCachedProgress();
        this.inputSlot = query(Holder.INPUT_SLOT_OPERATION).first();
        this.fuelSlot = query(Holder.FUEL_SLOT_OPERATION).first();
        this.outputSlot = query(Holder.OUTPUT_SLOT_OPERATION).first();
    }

    @Override
    protected void setCarrier(Carrier carrier) {
        super.setCarrier(carrier);
        this.carrierReference.set(carrier);
    }

    @Override
    public Optional<TileEntityCarrier> getCarrier() {
        return this.carrierReference.as(TileEntityCarrier.class);
    }

    @Override
    protected void initClientContainer(ClientContainer clientContainer) {
        super.initClientContainer(clientContainer);
        // Provide the smelting progress
        clientContainer.bindPropertySupplier(ContainerProperties.SMELT_PROGRESS, () -> {
            final Optional<DataHolder> dataHolder = this.carrierReference.as(DataHolder.class);
            if (!dataHolder.isPresent()) {
                return 0.0;
            }
            double smeltProgress = this.smeltProgress;
            if (!this.useCachedProgress || smeltProgress < 0) {
                smeltProgress = this.smeltProgress =
                        dataHolder.get().get(Keys.PASSED_COOK_TIME).get().doubleValue() /
                                dataHolder.get().get(Keys.MAX_COOK_TIME).get().doubleValue();
            }
            return smeltProgress;
        });
        // Provide the fuel progress
        clientContainer.bindPropertySupplier(ContainerProperties.FUEL_PROGRESS, () -> {
            final Optional<DataHolder> dataHolder = this.carrierReference.as(DataHolder.class);
            if (!dataHolder.isPresent()) {
                return 0.0;
            }
            double fuelProgress = this.fuelProgress;
            if (!this.useCachedProgress || fuelProgress < 0) {
                fuelProgress = this.fuelProgress = 1.0 -
                        dataHolder.get().get(Keys.PASSED_BURN_TIME).get().doubleValue() /
                                dataHolder.get().get(Keys.MAX_BURN_TIME).get();
            }
            return fuelProgress;
        });
    }
}
