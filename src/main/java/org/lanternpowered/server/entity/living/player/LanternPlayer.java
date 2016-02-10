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
package org.lanternpowered.server.entity.living.player;

import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Sets;
import org.lanternpowered.server.command.AbstractCommandSource;
import org.lanternpowered.server.effect.AbstractViewer;
import org.lanternpowered.server.effect.sound.LanternSoundType;
import org.lanternpowered.server.effect.sound.SoundCategory;
import org.lanternpowered.server.entity.LanternEntityHumanoid;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.objects.LocalizedText;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutChatMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutNamedSoundEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutParticleEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSendResourcePack;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSoundEffect;
import org.lanternpowered.server.permission.AbstractSubject;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.lanternpowered.server.text.title.LanternTitles;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.type.SkinPart;
import org.spongepowered.api.data.type.SkinParts;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.tab.TabList;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.resourcepack.ResourcePack;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.chat.ChatVisibilities;
import org.spongepowered.api.text.chat.ChatVisibility;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.Locale;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

@NonnullByDefault
public class LanternPlayer extends LanternEntityHumanoid implements AbstractSubject, Player, AbstractViewer, AbstractCommandSource {

    private final LanternUser user;
    private final LanternGameProfile gameProfile;
    private final Session session;

    private MessageChannel messageChannel = MessageChannel.TO_ALL;

    // The (client) locale of the player
    private Locale locale = Locale.ENGLISH;

    // The (client) render distance of the player
    // When specified -1, the render distance will match the server one
    private int viewDistance = -1;

    // The chat visibility
    private ChatVisibility chatVisibility = ChatVisibilities.FULL;

    // The main hand of the player
    private PlayerHand mainHand = PlayerHand.RIGHT;

    // Whether the chat colors are enabled
    private boolean chatColorsEnabled;

    // The visible skin parts
    private Set<SkinPart> skinParts = Sets.newHashSet(SkinParts.CAPE, SkinParts.HAT, SkinParts.JACKET, SkinParts.LEFT_SLEEVE,
            SkinParts.LEFT_PANTS_LEG, SkinParts.RIGHT_SLEEVE, SkinParts.RIGHT_PANTS_LEG);

    // Whether you should ignore this player when checking for sleeping players to reset the time
    private boolean sleepingIgnored;

    public LanternPlayer(LanternGameProfile gameProfile, Session session) {
        this.session = session;
        this.gameProfile = gameProfile;
        // Get or create the user object
        this.user = (LanternUser) LanternGame.get().getServiceManager().provideUnchecked(UserStorageService.class)
                .getOrCreate(gameProfile);
        this.user.setPlayer(this);
        // We don't register a callback because only the
        // user will hold the internal subject instance
    }

    public User getUserObject() {
        return this.user;
    }

    @Override
    public void setInternalSubject(Subject subject) {
        // We don't have to set the internal subject in the player instance
        // because it's already set in the user
    }

    @Override
    public Subject getInternalSubject() {
        return this.user.getInternalSubject();
    }

    @Override
    public String getSubjectCollectionIdentifier() {
        return this.user.getSubjectCollectionIdentifier();
    }

    @Override
    public Tristate getPermissionDefault(String permission) {
        return this.user.getPermissionDefault(permission);
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
    public Optional<CommandSource> getCommandSource() {
        return Optional.of(this);
    }

    @Override
    public GameProfile getProfile() {
        return this.gameProfile;
    }

    @Override
    public String getIdentifier() {
        return this.getUniqueId().toString();
    }

    @Override
    public void sendMessage(ChatType type, Text message) {
        checkNotNull(message, "message");
        checkNotNull(type, "type");
        this.session.send(new MessagePlayOutChatMessage(new LocalizedText(message, this.locale), type));
    }

    @Override
    public void sendMessage(Text message) {
        this.sendMessage(ChatTypes.CHAT, message);
    }

    @Override
    public MessageChannel getMessageChannel() {
        return this.messageChannel;
    }

    @Override
    public void setMessageChannel(MessageChannel channel) {
        this.messageChannel = checkNotNull(channel, "channel");
    }

    @Override
    public void spawnParticles(ParticleEffect particleEffect, Vector3d position) {
        this.session.send(new MessagePlayOutParticleEffect(checkNotNull(position, "position"),
                checkNotNull(particleEffect, "particleEffect")));
    }

    @Override
    public void spawnParticles(ParticleEffect particleEffect, Vector3d position, int radius) {
        checkNotNull(position, "position");
        checkNotNull(particleEffect, "particleEffect");
        if (this.getLocation().getPosition().distanceSquared(position) < radius * radius) {
            this.spawnParticles(particleEffect, position);
        }
    }

    @Override
    public void playSound(SoundType sound, Vector3d position, double volume, double pitch, double minVolume) {
        checkNotNull(sound, "sound");
        checkNotNull(position, "position");
        Message message;
        final OptionalInt eventId = ((LanternSoundType) sound).getEventId();
        if (eventId.isPresent()) {
            message = new MessagePlayOutSoundEffect(eventId.getAsInt(), position,
                    SoundCategory.MASTER, (float) Math.max(minVolume, volume), (float) pitch);
        } else {
            message = new MessagePlayOutNamedSoundEffect(sound.getName(), position,
                    SoundCategory.MASTER, (float) Math.max(minVolume, volume), (float) pitch);
        }
        this.session.send(message);
    }

    @Override
    public void sendTitle(Title title) {
        this.session.sendAll(LanternTitles.getMessages(checkNotNull(title, "title")));
    }

    @Override
    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = checkNotNull(locale, "locale");
    }

    @Override
    public int getViewDistance() {
        return this.viewDistance;
    }

    public void setViewDistance(int viewDistance) {
        this.viewDistance = viewDistance;
    }

    @Override
    public ChatVisibility getChatVisibility() {
        return this.chatVisibility;
    }

    public void setChatVisibility(ChatVisibility chatVisibility) {
        this.chatVisibility = chatVisibility;
    }

    @Override
    public boolean isChatColorsEnabled() {
        return this.chatColorsEnabled;
    }

    public void setChatColorsEnabled(boolean enabled) {
        this.chatColorsEnabled = enabled;
    }

    @Override
    public Set<SkinPart> getDisplayedSkinParts() {
        return this.skinParts;
    }

    public void setSkinParts(Set<SkinPart> skinParts) {
        this.skinParts = skinParts;
    }

    public PlayerHand getMainHand() {
        return this.mainHand;
    }

    public void setMainHand(PlayerHand mainHand) {
        this.mainHand = mainHand;
    }

    @Override
    public Session getConnection() {
        return this.session;
    }

    @Override
    public void sendResourcePack(ResourcePack resourcePack) {
        checkNotNull(resourcePack, "resourcePack");
        String hash = resourcePack.getHash().orElse(resourcePack.getId());
        String location = resourcePack.getUri().toString();
        this.session.send(new MessagePlayOutSendResourcePack(location, hash));
    }

    @Override
    public TabList getTabList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void kick() {
        this.session.disconnect();
    }

    @Override
    public void kick(Text reason) {
        this.session.disconnect(reason);
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
    public boolean isSleepingIgnored() {
        return this.sleepingIgnored;
    }

    @Override
    public void setSleepingIgnored(boolean sleepingIgnored) {
        this.sleepingIgnored = sleepingIgnored;
    }

}
