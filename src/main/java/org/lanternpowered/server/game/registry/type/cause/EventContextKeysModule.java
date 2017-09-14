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
package org.lanternpowered.server.game.registry.type.cause;

import com.google.common.reflect.TypeToken;
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.event.LanternEventContextKey;
import org.lanternpowered.server.event.LanternEventContextKeys;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;
import org.spongepowered.api.event.cause.EventContextKey;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.dismount.DismountType;
import org.spongepowered.api.event.cause.entity.spawn.SpawnType;
import org.spongepowered.api.event.cause.entity.teleport.TeleportType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public final class EventContextKeysModule extends AdditionalPluginCatalogRegistryModule<EventContextKey> {

    public EventContextKeysModule() {
        super(EventContextKeys.class, LanternEventContextKeys.class, ContextKeys.class);
    }

    @Override
    public void registerDefaults() {
        // Sponge
        register(new LanternEventContextKey<>("sponge", "creator", "Creator", User.class));
        register(new LanternEventContextKey<>("sponge", "damage_type", "Damage Type", DamageType.class));
        register(new LanternEventContextKey<>("sponge", "dismount_type", "Dimension Type", DismountType.class));
        register(new LanternEventContextKey<>("sponge", "igniter", "Igniter", User.class));
        register(new LanternEventContextKey<>("sponge", "last_damage_source", "Last Damage Source", DamageSource.class));
        register(new LanternEventContextKey<>("sponge", "liquid_mix", "Liquid Mix", World.class));
        register(new LanternEventContextKey<>("sponge", "notifier", "Notifier", User.class));
        register(new LanternEventContextKey<>("sponge", "owner", "Owner", User.class));
        register(new LanternEventContextKey<>("sponge", "player", "Player", Player.class));
        register(new LanternEventContextKey<>("sponge", "player_simulated", "Game Profile", GameProfile.class));
        register(new LanternEventContextKey<>("sponge", "projectile_source", "Projectile Source", ProjectileSource.class));
        register(new LanternEventContextKey<>("sponge", "service_manager", "Service Manager", ServiceManager.class));
        register(new LanternEventContextKey<>("sponge", "spawn_type", "Spawn Type", SpawnType.class));
        register(new LanternEventContextKey<>("sponge", "teleport_type", "Teleport Type", TeleportType.class));
        register(new LanternEventContextKey<>("sponge", "thrower", "Thrower", User.class));
        register(new LanternEventContextKey<>("sponge", "weapon", "Weapon", ItemStackSnapshot.class));
        register(new LanternEventContextKey<>("sponge", "fake_player", "Fake Player", Player.class));
        register(new LanternEventContextKey<>("sponge", "player_break", "Player Break", World.class));
        register(new LanternEventContextKey<>("sponge", "player_place", "Player Place", World.class));
        register(new LanternEventContextKey<>("sponge", "fire_spread", "Fire Spread", World.class));
        register(new LanternEventContextKey<>("sponge", "leaves_decay", "Leaves Decay", World.class));
        register(new LanternEventContextKey<>("sponge", "piston_retract", "Piston Retract", World.class));
        register(new LanternEventContextKey<>("sponge", "piston_extend", "Piston Extend", World.class));
        register(new LanternEventContextKey<>("sponge", "block_hit", "Block Hit", BlockSnapshot.class));
        register(new LanternEventContextKey<>("sponge", "entity_hit", "Entity Hit", BlockSnapshot.class));
        register(new LanternEventContextKey<>("sponge", "used_item", "Used Item", ItemStackSnapshot.class));
        register(new LanternEventContextKey<>("sponge", "plugin", "Plugin", PluginContainer.class));

        // Lantern

        /// Behavior context keys
        register(new LanternEventContextKey<>("lantern", "used_item_stack", "Used Item Stack", ItemStack.class));
        register(new LanternEventContextKey<>("lantern", "used_block_state", "Used Block State", BlockState.class));
        register(new LanternEventContextKey<>("lantern", "interaction_location", "Interaction Location", new TypeToken<Location<World>>() {}));
        register(new LanternEventContextKey<>("lantern", "interaction_face", "Interaction Face", Direction.class));
        register(new LanternEventContextKey<>("lantern", "interaction_hand", "Interaction Hand", HandType.class));
        register(new LanternEventContextKey<>("lantern", "block_location", "Block Location", new TypeToken<Location<World>>() {}));
        register(new LanternEventContextKey<>("lantern", "block_type", "Block Type", BlockType.class));
        register(new LanternEventContextKey<>("lantern", "block_snapshot", "Block Snapshot", BlockSnapshot.class));
        register(new LanternEventContextKey<>("lantern", "item_type", "Item Type", ItemType.class));
        register(new LanternEventContextKey<>("lantern", "used_slot", "Used Slot", Slot.class));

        /// Event context keys
        register(new LanternEventContextKey<>("lantern", "original_item_stack", "Original Item Stack", ItemStack.class));
        register(new LanternEventContextKey<>("lantern", "rest_item_stack", "Rest Item Stack", ItemStack.class));
    }
}
