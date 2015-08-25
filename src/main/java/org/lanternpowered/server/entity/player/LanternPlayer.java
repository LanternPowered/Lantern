package org.lanternpowered.server.entity.player;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.lanternpowered.server.entity.LanternEntityLiving;
import org.lanternpowered.server.network.session.Session;
import org.spongepowered.api.GameProfile;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.data.manipulator.mutable.entity.AchievementData;
import org.spongepowered.api.data.manipulator.mutable.entity.BanData;
import org.spongepowered.api.data.manipulator.mutable.entity.FoodData;
import org.spongepowered.api.data.manipulator.mutable.entity.GameModeData;
import org.spongepowered.api.data.manipulator.mutable.entity.JoinData;
import org.spongepowered.api.data.manipulator.mutable.entity.StatisticData;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.entity.player.tab.TabList;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.network.PlayerConnection;
import org.spongepowered.api.resourcepack.ResourcePack;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.context.Context;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.sink.MessageSink;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.util.command.CommandSource;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Optional;

public class LanternPlayer extends LanternEntityLiving implements Player {

    private final Session session = null;

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
    public Optional<ItemStack> getHelmet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setHelmet(ItemStack helmet) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Optional<ItemStack> getChestplate() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setChestplate(ItemStack chestplate) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Optional<ItemStack> getLeggings() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setLeggings(ItemStack leggings) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Optional<ItemStack> getBoots() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setBoots(ItemStack boots) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Optional<ItemStack> getItemInHand() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setItemInHand(ItemStack itemInHand) {
        // TODO Auto-generated method stub
        
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
    public GameProfile getProfile() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isOnline() {
        return this.session.isActive();
    }

    @Override
    public Optional<Player> getPlayer() {
        return Optional.<Player>of(this);
    }

    @Override
    public String getIdentifier() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<CommandSource> getCommandSource() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SubjectCollection getContainingCollection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SubjectData getSubjectData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SubjectData getTransientSubjectData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasPermission(Set<Context> contexts, String permission) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean hasPermission(String permission) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Tristate getPermissionValue(Set<Context> contexts, String permission) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isChildOf(Subject parent) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isChildOf(Set<Context> contexts, Subject parent) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<Subject> getParents() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Subject> getParents(Set<Context> contexts) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Context> getActiveContexts() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void sendMessage(Text... messages) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void sendMessage(Iterable<Text> messages) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void spawnParticles(ParticleEffect particleEffect, Vector3d position) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void spawnParticles(ParticleEffect particleEffect, Vector3d position, int radius) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void playSound(SoundType sound, Vector3d position, double volume) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void playSound(SoundType sound, Vector3d position, double volume, double pitch) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void playSound(SoundType sound, Vector3d position, double volume, double pitch, double minVolume) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void sendMessage(ChatType type, String... message) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void sendMessage(ChatType type, Text... messages) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void sendMessage(ChatType type, Iterable<Text> messages) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void sendTitle(Title title) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void resetTitle() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void clearTitle() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Locale getLocale() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PlayerConnection getConnection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void sendResourcePack(ResourcePack pack) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public TabList getTabList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void kick() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void kick(Text reason) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Scoreboard getScoreboard() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setScoreboard(Scoreboard scoreboard) {
        // TODO Auto-generated method stub
        
    }

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
    public MessageSink getMessageSink() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setMessageSink(MessageSink sink) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isSleepingIgnored() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setSleepingIgnored(boolean sleepingIgnored) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public FoodData getFoodData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AchievementData getAchievementData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatisticData getStatisticData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BanData getBanData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JoinData getJoinData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DisplayNameData getDisplayNameData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GameModeData getGameModeData() {
        // TODO Auto-generated method stub
        return null;
    }

}
