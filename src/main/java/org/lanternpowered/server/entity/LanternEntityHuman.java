package org.lanternpowered.server.entity;

import org.spongepowered.api.data.manipulator.mutable.entity.FoodData;
import org.spongepowered.api.entity.living.Human;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Optional;

public class LanternEntityHuman extends LanternEntityLiving implements Human {

    @Override
    public <T extends Projectile> Optional<T> launchProjectile(Class<T> projectileClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends Projectile> Optional<T> launchProjectile(Class<T> projectileClass, Vector3d velocity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<ItemStack> getHelmet() {
        return this.getEquipped(EquipmentTypes.HEADWEAR);
    }

    @Override
    public void setHelmet(ItemStack helmet) {
        this.equip(EquipmentTypes.HEADWEAR, helmet);
    }

    @Override
    public Optional<ItemStack> getChestplate() {
        return this.getEquipped(EquipmentTypes.CHESTPLATE);
    }

    @Override
    public void setChestplate(ItemStack chestplate) {
        this.equip(EquipmentTypes.CHESTPLATE, chestplate);
    }

    @Override
    public Optional<ItemStack> getLeggings() {
        return this.getEquipped(EquipmentTypes.LEGGINGS);
    }

    @Override
    public void setLeggings(ItemStack leggings) {
        this.equip(EquipmentTypes.LEGGINGS, leggings);
    }

    @Override
    public Optional<ItemStack> getBoots() {
        return this.getEquipped(EquipmentTypes.BOOTS);
    }

    @Override
    public void setBoots(ItemStack boots) {
        this.equip(EquipmentTypes.BOOTS, boots);
    }

    @Override
    public Optional<ItemStack> getItemInHand() {
        return this.getEquipped(EquipmentTypes.EQUIPPED);
    }

    @Override
    public void setItemInHand(ItemStack itemInHand) {
        this.equip(EquipmentTypes.EQUIPPED, itemInHand);
    }

    @Override
    public boolean canEquip(EquipmentType type) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean canEquip(EquipmentType type, ItemStack equipment) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Optional<ItemStack> getEquipped(EquipmentType type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean equip(EquipmentType type, ItemStack equipment) {
        if (!this.canEquip(type, equipment)) {
            return false;
        }
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public CarriedInventory<? extends Carrier> getInventory() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isViewingInventory() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Optional<Inventory> getOpenInventory() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void openInventory(Inventory inventory) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void closeInventory() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public FoodData getFoodData() {
        return this.get(FoodData.class).get();
    }

}
