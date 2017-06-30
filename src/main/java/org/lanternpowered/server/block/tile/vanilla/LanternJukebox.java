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
package org.lanternpowered.server.block.tile.vanilla;

import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector3d;
import org.lanternpowered.server.block.tile.LanternTileEntity;
import org.lanternpowered.server.block.trait.LanternBooleanTraits;
import org.lanternpowered.server.data.type.record.RecordType;
import org.lanternpowered.server.item.property.RecordProperty;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutRecord;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Jukebox;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

import javax.annotation.Nullable;

public final class LanternJukebox extends LanternTileEntity implements Jukebox {

    @Nullable private ItemStack record;
    private boolean playing;

    @Override
    public void playRecord() {
        if (this.record == null) {
            return;
        }
        this.playing = true;
        final Location<World> location = getLocation();
        final RecordProperty property = this.record.getProperty(RecordProperty.class).orElse(null);
        final RecordType recordType = property == null ? null : property.getValue();
        if (recordType != null) {
            ((LanternWorld) location.getExtent()).broadcast(
                    () -> new MessagePlayOutRecord(location.getBlockPosition(), recordType));
        }
    }

    /**
     * Whether currently a record is being played.
     *
     * @return Is playing
     */
    public boolean isPlaying() {
        return this.playing;
    }

    /**
     * Gets the raw record {@link ItemStack}, if present.
     *
     * @return The record item
     */
    public Optional<ItemStack> getRecordItem() {
        return Optional.ofNullable(this.record);
    }

    @Override
    public void stopRecord() {
        if (!this.playing) {
            return;
        }
        this.playing = false;
        final Location<World> location = getLocation();
        ((LanternWorld) location.getExtent()).broadcast(
                () -> new MessagePlayOutRecord(location.getBlockPosition(), null));
    }

    @Override
    public void ejectRecord() {
        ejectRecordItem().ifPresent(entity -> entity.getWorld().spawnEntity(entity,
                Cause.builder().owner(this).build()));
    }

    private void updateBlockState() {
        final Location<World> location = getLocation();
        final BlockState block = location.getBlock();
        location.setBlock(block
                .withTrait(LanternBooleanTraits.HAS_RECORD, this.record != null)
                .orElse(block), Cause.source(this).build());
    }

    /**
     * Resets the record {@link ItemStackSnapshot} and
     * returns it. If present.
     *
     * @return The record item
     */
    public Optional<Entity> ejectRecordItem() {
        if (this.record == null) {
            return Optional.empty();
        }
        stopRecord();
        final Location<World> location = getLocation();
        final Vector3d entityPosition = location.getBlockPosition().toDouble().add(0.5, 0.9, 0.5);
        final Entity item = location.getExtent().createEntity(EntityTypes.ITEM, entityPosition);
        item.offer(Keys.VELOCITY, new Vector3d(0, 0.1, 0));
        item.offer(Keys.REPRESENTED_ITEM, this.record.createSnapshot());
        this.record = null;
        updateBlockState();
        return Optional.of(item);
    }

    @Override
    public void insertRecord(ItemStack record) {
        checkNotNull(record, "record");
        ejectRecord();
        this.record = record.copy();
        updateBlockState();
    }

    @Override
    public BlockState getBlock() {
        final BlockState block = getLocation().getBlock();
        return block.getType() == BlockTypes.JUKEBOX ? block : BlockTypes.JUKEBOX.getDefaultState();
    }
}
