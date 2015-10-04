package org.lanternpowered.server.world;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.PlayerSimulator;

import com.flowpowered.math.vector.Vector3i;

public class LanternPlayerSimulator implements PlayerSimulator {

    @Override
    public void interactBlock(Vector3i position, Direction side) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void interactBlock(int x, int y, int z, Direction side) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void interactBlockWith(Vector3i position, ItemStack itemStack, Direction side) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void interactBlockWith(int x, int y, int z, ItemStack itemStack, Direction side) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean digBlock(Vector3i position) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean digBlock(int x, int y, int z) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean digBlockWith(Vector3i position, ItemStack itemStack) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean digBlockWith(int x, int y, int z, ItemStack itemStack) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getBlockDigTimeWith(Vector3i position, ItemStack itemStack) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getBlockDigTimeWith(int x, int y, int z, ItemStack itemStack) {
        // TODO Auto-generated method stub
        return 0;
    }
}
