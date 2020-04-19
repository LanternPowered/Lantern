/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.service.permission;

import org.lanternpowered.server.config.user.OpsEntry;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.lanternpowered.server.service.permission.base.LanternSubject;
import org.lanternpowered.server.service.permission.base.SingleParentMemorySubjectData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.MemorySubjectData;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import java.util.Optional;
import java.util.Set;

/**
 * An implementation of vanilla minecraft's 4 op groups.
 */
final class UserSubject extends LanternSubject {

    private final GameProfile player;
    private final MemorySubjectData data;
    private final UserCollection collection;

    UserSubject(GameProfile player, UserCollection users) {
        this.player = player;
        this.collection = users;
        this.data = new SingleParentMemorySubjectData(this) {

            @Override
            public SubjectReference getParent() {
                int opLevel = getOpLevel();
                return opLevel == 0 ? null : users.getService().getGroupForOpLevel(opLevel).asSubjectReference();
            }

            @Override
            public void setParent(SubjectReference parent) {
                int opLevel;
                if (parent == null) {
                    opLevel = 0;
                } else {
                    final Subject subject = parent.resolve().join();
                    if (!(subject instanceof OpLevelCollection.OpLevelSubject)) {
                        return;
                    }
                    opLevel = ((OpLevelCollection.OpLevelSubject) subject).getOpLevel();
                }
                if (opLevel > 0) {
                    Lantern.getGame().getOpsConfig().addEntry(new OpsEntry(((LanternGameProfile) player).withoutProperties(), opLevel));
                } else {
                    Lantern.getGame().getOpsConfig().removeEntry(player.getUniqueId());
                }
            }
        };
    }

    @Override
    public String getIdentifier() {
        return this.player.getUniqueId().toString();
    }

    @Override
    public Optional<String> getFriendlyIdentifier() {
        return this.player.getName();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<CommandSource> getCommandSource() {
        if (Sponge.isServerAvailable()) {
            return (Optional) Lantern.getServer().getPlayer(this.player.getUniqueId());
        }
        return Optional.empty();
    }

    public int getOpLevel() {
        return Lantern.getGame().getOpsConfig().getEntryByUUID(this.player.getUniqueId()).map(OpsEntry::getOpLevel).orElse(0);
    }

    @Override
    public SubjectCollection getContainingCollection() {
        return this.collection;
    }

    @Override
    public MemorySubjectData getSubjectData() {
        return this.data;
    }

    @Override
    public PermissionService getService() {
        return this.collection.getService();
    }

    @Override
    public Tristate getPermissionValue(Set<Context> contexts, String permission) {
        Tristate ret = super.getPermissionValue(contexts, permission);
        if (ret == Tristate.UNDEFINED) {
            ret = getDataPermissionValue(this.collection.getDefaults().getSubjectData(), permission);
        }
        if (ret == Tristate.UNDEFINED) {
            ret = getDataPermissionValue(this.collection.getService().getDefaults().getSubjectData(), permission);
        }
        if (ret == Tristate.UNDEFINED && getOpLevel() >= Lantern.getGame().getGlobalConfig().getDefaultOpPermissionLevel()) {
            ret = Tristate.TRUE;
        }
        return ret;
    }

    @Override
    public Optional<String> getOption(Set<Context> contexts, String option) {
        Optional<String> ret = super.getOption(contexts, option);
        if (!ret.isPresent()) {
            ret = getDataOptionValue(this.collection.getDefaults().getSubjectData(), option);
        }
        if (!ret.isPresent()) {
            ret = getDataOptionValue(this.collection.getService().getDefaults().getSubjectData(), option);
        }
        return ret;
    }
}
