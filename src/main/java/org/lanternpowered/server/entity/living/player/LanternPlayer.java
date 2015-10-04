package org.lanternpowered.server.entity.living.player;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.lanternpowered.server.effect.LanternViewer;
import org.lanternpowered.server.entity.LanternEntityHuman;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutChatMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutParticleEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSendResourcePack;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSoundEffect;
import org.lanternpowered.server.permission.SubjectBase;
import org.spongepowered.api.GameProfile;
import org.spongepowered.api.data.manipulator.mutable.entity.BanData;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.tab.TabList;
import org.spongepowered.api.resourcepack.ResourcePack;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.context.Context;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.sink.MessageSink;
import org.spongepowered.api.text.sink.MessageSinks;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.util.command.CommandSource;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Lists;

public class LanternPlayer extends LanternEntityHuman implements Player, LanternViewer {

    // We cannot extend the subject base directly, so we have to forward the methods
    private final SubjectBase permissionSubject = new SubjectBase() {

        @Override
        public String getIdentifier() {
            return LanternPlayer.this.getIdentifier();
        }

        @Override
        public Optional<CommandSource> getCommandSource() {
            return Optional.of(LanternPlayer.this);
        }

        @Override
        protected String getSubjectCollectionIdentifier() {
            return PermissionService.SUBJECTS_USER;
        }

        @Override
        protected Tristate getPermissionDefault(String permission) {
            return Tristate.TRUE;
        }

    };

    private Session session = null;
    private MessageSink messageSink = MessageSinks.toAll();

    private GameProfile gameProfile;

    // The (client) locale of the player
    private Locale locale = Locale.ENGLISH;

    // The (client) render distance of the player
    // When specified -1, the render distance will match the server one
    private int renderDistance = -1;

    private boolean sleepingIgnored;

    /**
     * Gets the render distance of the player.
     * 
     * @return the render distance
     */
    public int getRenderDistance() {
        return this.renderDistance;
    }

    /**
     * Sets the render distance of the player.
     * 
     * @param renderDistance the render distance
     */
    public void setRenderDistance(int renderDistance) {
        this.renderDistance = renderDistance;
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
    public SubjectCollection getContainingCollection() {
        return this.permissionSubject.getContainingCollection();
    }

    @Override
    public SubjectData getSubjectData() {
        return this.permissionSubject.getSubjectData();
    }

    @Override
    public SubjectData getTransientSubjectData() {
        return this.permissionSubject.getTransientSubjectData();
    }

    @Override
    public boolean hasPermission(Set<Context> contexts, String permission) {
        return this.permissionSubject.hasPermission(contexts, permission);
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.permissionSubject.hasPermission(permission);
    }

    @Override
    public Tristate getPermissionValue(Set<Context> contexts, String permission) {
        return this.permissionSubject.getPermissionValue(contexts, permission);
    }

    @Override
    public boolean isChildOf(Subject parent) {
        return this.permissionSubject.isChildOf(parent);
    }

    @Override
    public boolean isChildOf(Set<Context> contexts, Subject parent) {
        return this.permissionSubject.isChildOf(contexts, parent);
    }

    @Override
    public List<Subject> getParents() {
        return this.permissionSubject.getParents();
    }

    @Override
    public List<Subject> getParents(Set<Context> contexts) {
        return this.permissionSubject.getParents(contexts);
    }

    @Override
    public Set<Context> getActiveContexts() {
        return this.permissionSubject.getActiveContexts();
    }

    @Override
    public GameProfile getProfile() {
        return this.gameProfile;
    }

    @Override
    public String getIdentifier() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void sendMessage(ChatType type, Iterable<Text> messages) {
        checkNotNull(messages, "messages");
        checkNotNull(type, "type");
        for (Text message : messages) {
            if (message != null) {
                this.session.send(new MessagePlayOutChatMessage(message, type));
            }
        }
    }

    @Override
    public void sendMessage(Text... messages) {
        this.sendMessage(Lists.newArrayList(checkNotNull(messages, "messages")));
    }

    @Override
    public void sendMessage(Iterable<Text> messages) {
        this.sendMessage(ChatTypes.CHAT, checkNotNull(messages, "messages"));
    }

    @Override
    public MessageSink getMessageSink() {
        return this.messageSink;
    }

    @Override
    public void setMessageSink(MessageSink sink) {
        this.messageSink = checkNotNull(sink, "sink");
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
        this.session.send(new MessagePlayOutSoundEffect(sound.getName(), position,
                (float) Math.max(minVolume, volume), (float) pitch));
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
        return this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = checkNotNull(locale, "locale");
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

    @Override
    public BanData getBanData() {
        return this.get(BanData.class).get();
    }
}
