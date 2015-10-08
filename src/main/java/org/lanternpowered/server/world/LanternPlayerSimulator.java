package org.lanternpowered.server.world;

import javax.annotation.Nullable;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.PlayerSimulator;

import com.flowpowered.math.vector.Vector3i;

public class LanternPlayerSimulator implements PlayerSimulator {

    @Override
    public void interactBlock(Vector3i position, Direction side) {
        this.interactBlock(position.getX(), position.getY(), position.getZ(), side);
    }

    @Override
    public void interactBlock(int x, int y, int z, Direction side) {
        this.interactBlockWith(x, y, z, null, side);
    }

    @Override
    public void interactBlockWith(Vector3i position, @Nullable ItemStack itemStack, Direction side) {
        this.interactBlockWith(position.getX(), position.getY(), position.getZ(), itemStack, side);
    }

    @Override
    public void interactBlockWith(int x, int y, int z, @Nullable ItemStack itemStack, Direction side) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean digBlock(Vector3i position) {
        return this.digBlock(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public boolean digBlock(int x, int y, int z) {
        return this.digBlockWith(x, y, z, null);
    }

    @Override
    public boolean digBlockWith(Vector3i position, @Nullable ItemStack itemStack) {
        return this.digBlockWith(position.getX(), position.getY(), position.getZ(), itemStack);
    }

    @Override
    public boolean digBlockWith(int x, int y, int z, @Nullable ItemStack itemStack) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getBlockDigTimeWith(Vector3i position, @Nullable ItemStack itemStack) {
        return this.getBlockDigTimeWith(position.getX(), position.getY(), position.getZ(), itemStack);
    }

    @Override
    public int getBlockDigTimeWith(int x, int y, int z, @Nullable ItemStack itemStack) {
        // TODO Auto-generated method stub
        return 0;
    }
}
