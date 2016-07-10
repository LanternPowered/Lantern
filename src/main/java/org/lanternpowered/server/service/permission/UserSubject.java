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
package org.lanternpowered.server.service.permission;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.config.user.OpsEntry;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.lanternpowered.server.service.permission.base.LanternSubject;
import org.lanternpowered.server.service.permission.base.SingleParentMemorySubjectData;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.MemorySubjectData;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.util.Tristate;

import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * An implementation of vanilla minecraft's 4 op groups.
 */
public class UserSubject extends LanternSubject {

    private final GameProfile player;
    private final MemorySubjectData data;
    private final UserCollection collection;

    public UserSubject(final GameProfile player, final UserCollection users) {
        this.player = checkNotNull(player, "player");
        this.data = new SingleParentMemorySubjectData(checkNotNull(users, "userCollection").getService()) {

            @Override
            public Subject getParent() {
                int opLevel = getOpLevel();
                return opLevel == 0 ? null : users.getService().getGroupForOpLevel(opLevel);
            }

            @Override
            public boolean setParent(@Nullable Subject parent) {
                int opLevel;
                if (parent == null) {
                    opLevel = 0;
                } else {
                    if (!(parent instanceof OpLevelCollection.OpLevelSubject)) {
                        return false;
                    }
                    opLevel = ((OpLevelCollection.OpLevelSubject) parent).getOpLevel();
                }
                if (opLevel > 0) {
                    Lantern.getGame().getOpsConfig().addEntry(new OpsEntry(((LanternGameProfile) player).withoutProperties(), opLevel));
                } else {
                    Lantern.getGame().getOpsConfig().removeEntry(player.getUniqueId());
                }
                return true;
            }

        };
        this.collection = users;
    }

    @Override
    public String getIdentifier() {
        return this.player.getUniqueId().toString();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Optional<CommandSource> getCommandSource() {
        return (Optional) Lantern.getGame().getServer().getPlayer(this.player.getUniqueId());
    }

    int getOpLevel() {
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
    public Tristate getPermissionValue(Set<Context> contexts, String permission) {
        Tristate ret = super.getPermissionValue(contexts, permission);
        if (ret == Tristate.UNDEFINED) {
            ret = this.getDataPermissionValue(this.collection.getDefaults().getSubjectData(), permission);
        }
        if (ret == Tristate.UNDEFINED) {
            ret = this.getDataPermissionValue(this.collection.getService().getDefaults().getSubjectData(), permission);
        }
        if (ret == Tristate.UNDEFINED && this.getOpLevel() >= Lantern.getGame().getGlobalConfig().getDefaultOpPermissionLevel()) {
            ret = Tristate.TRUE;
        }
        return ret;
    }

    @Override
    public Optional<String> getOption(Set<Context> contexts, String option) {
        Optional<String> ret = super.getOption(contexts, option);
        if (!ret.isPresent()) {
            ret = this.getDataOptionValue(this.collection.getDefaults().getSubjectData(), option);
        }
        if (!ret.isPresent()) {
            ret = this.getDataOptionValue(this.collection.getService().getDefaults().getSubjectData(), option);
        }
        return ret;
    }

}
