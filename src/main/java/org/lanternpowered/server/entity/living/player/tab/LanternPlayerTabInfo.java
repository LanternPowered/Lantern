package org.lanternpowered.server.entity.living.player.tab;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import org.lanternpowered.server.game.LanternGameProfile;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabListEntries.Entry;
import org.lanternpowered.server.util.Sets2;
import org.spongepowered.api.GameProfile;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.tab.PlayerTabInfo;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;

public final class LanternPlayerTabInfo implements PlayerTabInfo {

    // All the tab lists this tab info is attached to
    final Set<LanternTabList> tabLists = Sets2.newWeakHashSet();

    // The changes of the tab info since the last tick
    static class UpdateEntry {

        final LanternPlayerTabInfo tabInfo;

        boolean gameProfileOrName;
        boolean gameMode;
        boolean displayName;
        boolean connectionTime;
        boolean remove;

        List<Entry> entryCache;

        public UpdateEntry(LanternPlayerTabInfo tabInfo) {
            this.tabInfo = tabInfo;
        }
    }

    @Nullable UpdateEntry updateEntry;

    // The unique id of the player this info is attached to
    final UUID uniqueId;

    @Nullable Text displayName;
    LanternGameProfile gameProfile;
    GameMode gameMode;
    String name;

    int connectionTime;

    public LanternPlayerTabInfo(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    UpdateEntry updateEntry() {
        if (this.updateEntry == null) {
            this.updateEntry = new UpdateEntry(this);
            for (LanternTabList tabList : this.tabLists) {
                tabList.updateEntries.add(this.updateEntry);
            }
        }
        return this.updateEntry;
    }

    @Override
    public int getConnectionTime() {
        return this.connectionTime;
    }

    @Override
    public void setConnectionTime(int milliseconds) {
        this.connectionTime = milliseconds;
        this.updateEntry().gameProfileOrName = true;
    }

    @Override
    public GameMode getGameMode() {
        return this.gameMode;
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        this.gameMode = checkNotNull(gameMode, "gameMode");
        this.updateEntry().gameMode = true;
    }

    @Override
    public Text getDisplayName() {
        if (this.displayName == null) {
            return Texts.of(this.name);
        }
        return this.displayName;
    }

    @Override
    public void setDisplayName(Text displayName) {
        this.displayName = displayName;
        this.updateEntry().displayName = true;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = checkNotNull(name, "name");
        this.updateEntry().gameProfileOrName = true;
    }

    @Override
    public GameProfile getProfile() {
        return this.gameProfile;
    }

    @Override
    public void setProfile(GameProfile profile) {
        this.gameProfile = (LanternGameProfile) checkNotNull(profile, "profile");
        this.updateEntry().gameProfileOrName = true;
    }
}
