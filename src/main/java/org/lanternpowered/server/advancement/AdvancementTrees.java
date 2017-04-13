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
package org.lanternpowered.server.advancement;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutAdvancements;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class AdvancementTrees {

    public static final AdvancementTrees INSTANCE = new AdvancementTrees();

    private final static int UPDATE_DELAY = 10;

    private final List<AdvancementTree> advancementTrees = new ArrayList<>();

    void add(AdvancementTree tree) {
        this.advancementTrees.add(checkNotNull(tree, "tree"));
    }

    private int counter = UPDATE_DELAY;

    public void removeTracker(Player player) {
        for (AdvancementTree advancementTree : this.advancementTrees) {
            advancementTree.removeRawTracker(player);
        }
    }

    public void pulse() {
        if (this.counter-- > 0) {
            return;
        }
        this.counter = UPDATE_DELAY;
        final List<LanternPlayer> trackers = new ArrayList<>();
        for (AdvancementTree advancementTree : this.advancementTrees) {
            final List<LanternPlayer> trackers1 = advancementTree.getUpdateTrackers();
            trackers.addAll(trackers1);
            final int state = advancementTree.isRefreshRequired() ? AdvancementTree.REFRESH : AdvancementTree.UPDATE;
            final AdvancementTree.GlobalAdvancementsData globalAdvancementsData = advancementTree.createGlobalData(Locale.ENGLISH, state);
            for (LanternPlayer tracker : trackers1) {
                final MessagePlayOutAdvancements message = advancementTree.createAdvancementsMessage(
                        globalAdvancementsData, tracker.getAdvancementsProgress(), state);
                if (message != null) {
                    tracker.getConnection().send(message);
                }
            }
            advancementTree.clearDirty();
        }
        trackers.forEach(player -> player.getAdvancementsProgress().clearDirty());
    }
}
