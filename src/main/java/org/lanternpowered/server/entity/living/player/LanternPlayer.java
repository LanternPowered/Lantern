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
package org.lanternpowered.server.entity.living.player;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.lanternpowered.server.advancement.AdvancementTree;
import org.lanternpowered.server.advancement.AdvancementTrees;
import org.lanternpowered.server.advancement.AdvancementsProgress;
import org.lanternpowered.server.advancement.TestAdvancementTree;
import org.lanternpowered.server.boss.LanternBossBar;
import org.lanternpowered.server.data.ValueCollection;
import org.lanternpowered.server.data.element.ElementKeyRegistration;
import org.lanternpowered.server.data.io.store.item.WrittenBookItemTypeObjectSerializer;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.effect.AbstractViewer;
import org.lanternpowered.server.effect.sound.LanternSoundType;
import org.lanternpowered.server.entity.event.SpectateEntityEvent;
import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode;
import org.lanternpowered.server.entity.living.player.tab.GlobalTabList;
import org.lanternpowered.server.entity.living.player.tab.GlobalTabListEntry;
import org.lanternpowered.server.entity.living.player.tab.LanternTabList;
import org.lanternpowered.server.entity.living.player.tab.LanternTabListEntry;
import org.lanternpowered.server.entity.living.player.tab.LanternTabListEntryBuilder;
import org.lanternpowered.server.event.CauseStack;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.inventory.LanternContainer;
import org.lanternpowered.server.inventory.OpenableInventory;
import org.lanternpowered.server.inventory.PlayerContainerSession;
import org.lanternpowered.server.inventory.PlayerInventoryContainer;
import org.lanternpowered.server.inventory.entity.LanternPlayerInventory;
import org.lanternpowered.server.item.CooldownTracker;
import org.lanternpowered.server.network.NetworkSession;
import org.lanternpowered.server.network.entity.NetworkIdHolder;
import org.lanternpowered.server.network.objects.RawItemStack;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutBrand;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBlockChange;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutOpenBook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutParticleEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerJoinGame;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerPositionAndLook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerRespawn;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutRecord;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSelectAdvancementTree;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetReducedDebug;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetWindowSlot;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutUnlockRecipes;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.lanternpowered.server.scoreboard.LanternScoreboard;
import org.lanternpowered.server.text.chat.LanternChatType;
import org.lanternpowered.server.text.title.LanternTitles;
import org.lanternpowered.server.world.LanternWeatherUniverse;
import org.lanternpowered.server.world.LanternWorld;
import org.lanternpowered.server.world.LanternWorldBorder;
import org.lanternpowered.server.world.chunk.ChunkLoadingTicket;
import org.lanternpowered.server.world.difficulty.LanternDifficulty;
import org.lanternpowered.server.world.dimension.LanternDimensionType;
import org.lanternpowered.server.world.rules.RuleTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.data.type.SkinPart;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.sound.SoundCategory;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.record.RecordType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.entity.HarvestEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.event.world.ChangeWorldBorderEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.resourcepack.ResourcePack;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.BookView;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.chat.ChatVisibilities;
import org.spongepowered.api.text.chat.ChatVisibility;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.util.RelativePositions;
import org.spongepowered.api.world.ChunkTicketManager;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldBorder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
public class LanternPlayer extends AbstractUser implements Player, AbstractViewer, NetworkIdHolder {

    private final static AABB BOUNDING_BOX_BASE = new AABB(new Vector3d(-0.3, 0, -0.3), new Vector3d(0.3, 1.8, 0.3));

    private final NetworkSession session;

    private final LanternTabList tabList = new LanternTabList(this);

    // The entity id that will be used for the client
    private int networkEntityId = -1;

    private MessageChannel messageChannel = MessageChannel.TO_ALL;

    // The (client) locale of the player
    private Locale locale = Locale.ENGLISH;

    // The (client) render distance of the player
    // When specified -1, the render distance will match the server one
    private int viewDistance = -1;

    // The chat visibility
    private ChatVisibility chatVisibility = ChatVisibilities.FULL;

    // Whether the chat colors are enabled
    private boolean chatColorsEnabled;

    private LanternScoreboard scoreboard;

    // Whether you should ignore this player when checking for sleeping players to reset the time
    private boolean sleepingIgnored;

    // The chunks the client knows about
    private final Set<Vector2i> knownChunks = new HashSet<>();

    // The interaction handler
    private final PlayerInteractionHandler interactionHandler;

    // The chunk position since the last #pulseChunkChanges call
    @Nullable private Vector2i lastChunkPos = null;

    // The loading ticket that will force the chunks to be loaded
    @Nullable private ChunkTicketManager.PlayerEntityLoadingTicket loadingTicket;

    private final ResourcePackSendQueue resourcePackSendQueue = new ResourcePackSendQueue(this);

    /**
     * The inventory of this {@link Player}.
     */
    private final LanternPlayerInventory inventory;

    /**
     * The {@link LanternContainer} of the players inventory.
     */
    private final PlayerInventoryContainer inventoryContainer;

    /**
     * The container session of this {@link Player}.
     */
    private final PlayerContainerSession containerSession;

    /**
     * All the boss bars that are visible for this {@link Player}.
     */
    private final Set<LanternBossBar> bossBars = new HashSet<>();

    /**
     * The item cooldown tracker of this {@link Player}.
     */
    private final CooldownTracker cooldownTracker = new PlayerCooldownTracker(this);

    /**
     * The last time that the player was active.
     */
    private long lastActiveTime;

    /**
     * The entity that is being spectated by this player.
     */
    @Nullable private Entity spectatorEntity;

    // The world border the player is currently tracking, if null, it will track the
    // border of the world the player is located in
    @Nullable private LanternWorldBorder worldBorder;

    private final AdvancementsProgress advancementsProgress = new AdvancementsProgress();

    public LanternPlayer(LanternGameProfile gameProfile, NetworkSession session) {
        super((ProxyUser) Sponge.getServiceManager().provideUnchecked(UserStorageService.class).getOrCreate(gameProfile));
        this.interactionHandler = new PlayerInteractionHandler(this);
        this.inventory = new LanternPlayerInventory(null, null, this);
        this.inventoryContainer = new PlayerInventoryContainer(this.inventory);
        this.containerSession = new PlayerContainerSession(this);
        this.session = session;
        resetIdleTimeoutCounter();
        setBoundingBoxBase(BOUNDING_BOX_BASE);
        // Attach this player to the proxy user and load player data
        getProxyUser().setInternalUser(this);
    }

    public Set<LanternBossBar> getBossBars() {
        return this.bossBars;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public int getNetworkId() {
        return this.networkEntityId;
    }

    /**
     * Sets the network entity id.
     *
     * @param entityId The network entity id
     */
    public void setNetworkId(int entityId) {
        this.networkEntityId = entityId;
    }

    /**
     * Resets the timeout counter.
     */
    public void resetIdleTimeoutCounter() {
        this.lastActiveTime = System.currentTimeMillis();
    }

    @Override
    public void registerKeys() {
        super.registerKeys();
        final ValueCollection c = getValueCollection();
        ((ElementKeyRegistration<?, Optional<AdvancementTree>>) c.get(LanternKeys.OPEN_ADVANCEMENT_TREE).get())
                .addListener((oldElement, newElement) -> {
                    if (getWorld() != null) {
                        this.session.send(new MessagePlayOutSelectAdvancementTree(newElement.map(AdvancementTree::getInternalId).orElse(null)));
                    }
                });
    }

    @Override
    public void setWorld(@Nullable LanternWorld world) {
        final LanternWorld oldWorld = getWorld();
        if (world == oldWorld) {
            return;
        }
        setWorld(oldWorld, world);
    }

    private void setWorld(@Nullable LanternWorld oldWorld, @Nullable LanternWorld world) {
        if (oldWorld != world) {
            this.interactionHandler.reset();
        }
        super.setWorld(world);
        if (oldWorld != null && world != oldWorld) {
            if (this.loadingTicket != null) {
                this.loadingTicket.release();
                this.loadingTicket = null;
            }
            // Remove the player from all the observed chunks, there is no need
            // to send unload messages because we will respawn in a different world
            final ObservedChunkManager observedChunkManager = oldWorld.getObservedChunkManager();
            final Set<Vector2i> knownChunks = new HashSet<>(this.knownChunks);
            knownChunks.forEach(coords -> observedChunkManager.removeObserver(coords, this, false));
            this.knownChunks.clear();
            // Clear the last chunk pos
            this.lastChunkPos = null;
            // Remove the player from the world
            oldWorld.removePlayer(this);
            if (this.worldBorder == null) {
                oldWorld.getWorldBorder().removePlayer(this);
            }
        }
        if (world != null) {
            final LanternGameMode gameMode = (LanternGameMode) get(Keys.GAME_MODE).get();
            final LanternDimensionType dimensionType = (LanternDimensionType) world.getDimension().getType();
            final LanternDifficulty difficulty = (LanternDifficulty) world.getDifficulty();
            final boolean reducedDebug = world.getOrCreateRule(RuleTypes.REDUCED_DEBUG_INFO).getValue();
            final boolean lowHorizon = world.getProperties().getConfig().isLowHorizon();
            // The player has joined the server
            if (oldWorld == null) {
                this.session.getServer().addPlayer(this);
                this.session.send(new MessagePlayOutPlayerJoinGame(gameMode, dimensionType, difficulty, this.networkEntityId,
                        this.session.getServer().getMaxPlayers(), reducedDebug, false, lowHorizon));
                // Send the server brand
                this.session.send(new MessagePlayInOutBrand(Lantern.getImplementationPlugin().getName()));
                // Send the player list
                final List<LanternTabListEntry> tabListEntries = new ArrayList<>();
                final LanternTabListEntryBuilder thisBuilder = createTabListEntryBuilder(this);
                for (Player player : Sponge.getServer().getOnlinePlayers()) {
                    final LanternTabListEntryBuilder builder = player == this ? thisBuilder : createTabListEntryBuilder((LanternPlayer) player);
                    tabListEntries.add(builder.list(this.tabList).build());
                    if (player != this) {
                        player.getTabList().addEntry(thisBuilder.list(player.getTabList()).build());
                    }
                }
                this.tabList.init(tabListEntries);
                TestAdvancementTree.A.addRawTracker(this);
                TestAdvancementTree.B.addRawTracker(this);
                AdvancementTrees.INSTANCE.initialize(this);
                getAdvancementsProgress().get(TestAdvancementTree.DIG_DIRT)
                        .tryGet(TestAdvancementTree.DIG_DIRT_CRITERION).set(4);
                this.session.send(new MessagePlayOutSelectAdvancementTree(
                        get(LanternKeys.OPEN_ADVANCEMENT_TREE).get().map(AdvancementTree::getInternalId).orElse(null)));
                // TODO: Unlock all the recipes for now, mappings between the internal ids and
                // TODO: the readable ids still has to be made
                final int[] recipes = new int[435];
                for (int i = 0; i < recipes.length; i++) {
                    recipes[i] = i;
                }
                this.session.send(new MessagePlayOutUnlockRecipes.Add(
                        get(LanternKeys.RECIPE_BOOK_GUI_OPEN).get(),
                        get(LanternKeys.RECIPE_BOOK_FILTER_ACTIVE).get(),
                        new IntArrayList(recipes)));
            } else {
                if (oldWorld != null && oldWorld != world) {
                    LanternDimensionType oldDimensionType = (LanternDimensionType) oldWorld.getDimension().getType();
                    // The client only creates a new world instance on the client if a
                    // different dimension is used, that is why we will send two respawn
                    // messages to trick the client to do it anyway
                    // This is also needed to avoid weird client bugs
                    if (oldDimensionType == dimensionType) {
                        oldDimensionType = (LanternDimensionType) (dimensionType == DimensionTypes.OVERWORLD ?
                                DimensionTypes.NETHER : DimensionTypes.OVERWORLD);
                        this.session.send(new MessagePlayOutPlayerRespawn(gameMode, oldDimensionType, difficulty, lowHorizon));
                    }
                }
                // Send a respawn message
                this.session.send(new MessagePlayOutPlayerRespawn(gameMode, dimensionType, difficulty, lowHorizon));
                this.session.send(new MessagePlayOutSetReducedDebug(reducedDebug));
            }
            // Send the first chunks
            pulseChunkChanges();
            // Update the sky, this contains the darkness and rain levels
            world.getWeatherUniverse().ifPresent(u -> this.session.send(((LanternWeatherUniverse) u).createSkyUpdateMessage()));
            // Update the time
            this.session.send(world.getTimeUniverse().createUpdateTimeMessage());
            // Update the player inventory
            this.inventoryContainer.init();
            if (oldWorld != world) {
                if (this.worldBorder == null) {
                    world.getWorldBorder().addPlayer(this);
                }
                // Send the boss bars
                this.bossBars.forEach(bossBar -> bossBar.resendBossBar(this));
                // Set the scoreboard
                setScoreboard(world.getScoreboard());
                // Add the player to the world
                world.addPlayer(this);
            }
        } else {
            if (this.worldBorder != null) {
                this.worldBorder.removePlayer(this);
            }
            AdvancementTrees.INSTANCE.removeTracker(this);
            this.session.getServer().removePlayer(this);
            this.bossBars.forEach(bossBar -> bossBar.removeRawPlayer(this));
            this.tabList.clear();
            // Remove this player from the global tab list
            GlobalTabList.getInstance().get(getProfile()).ifPresent(GlobalTabListEntry::removeEntry);
        }
    }

    @Override
    protected void postHarvestEvent(CauseStack causeStack) {
        final boolean keepsInventory = getWorld().getOrCreateRule(RuleTypes.KEEP_INVENTORY).getValue();
        final boolean keepsExpLevels = getWorld().getOrCreateRule(RuleTypes.KEEP_EXPERIENCE_LEVELS).getValue();
        final int exp = collectExperience();
        // Humanoids get their own sub-interface for the event
        final HarvestEntityEvent.TargetPlayer harvestEvent = SpongeEventFactory.createHarvestEntityEventTargetPlayer(
                causeStack.getCurrentCause(), exp, exp, this, keepsInventory, keepsExpLevels, 0);
        Sponge.getEventManager().post(harvestEvent);
        if (harvestEvent.isCancelled()) {
            return;
        }
        // Finalize the harvest event
        finalizeHarvestEvent(causeStack, harvestEvent);
        if (!harvestEvent.keepsInventory()) {
            // TODO: Drop all the items
            getInventory().clear();
        }
        if (!harvestEvent.keepsLevel()) {
            offer(Keys.EXPERIENCE_LEVEL, harvestEvent.getLevel());
        }
    }

    @Override
    protected boolean pulseDeath(int deltaTicks) {
        // A player is never removed after a delay, it will exist until
        // the player respawns or disconnects.
        return isDead();
    }

    public void handleRespawn() {
        Transform<World> transform = getTransform();
        final LanternWorld world = (LanternWorld) transform.getExtent();
        if (isDead()) {
            // TODO: Get the proper spawn location
            final Transform<World> toTransform = new Transform<>(transform.getExtent(), new Vector3d(0, 100, 0));

            // Make the player less dead...
            setDead(false);
            // Reset player settings
            offer(Keys.HEALTH, get(Keys.MAX_HEALTH).get());
            offer(Keys.FOOD_LEVEL, get(LanternKeys.MAX_FOOD_LEVEL).get());
            offer(Keys.ABSORPTION, 0.0);
            offer(Keys.EXHAUSTION, DEFAULT_EXHAUSTION);
            offer(Keys.SATURATION, DEFAULT_SATURATION);
            offer(Keys.POTION_EFFECTS, new ArrayList<>());
            offer(LanternKeys.SCORE, 0);

            final CauseStack causeStack = CauseStack.current();
            try (CauseStack.Frame frame = causeStack.pushCauseFrame()) {
                frame.pushCause(this);
                frame.addContext(EventContextKeys.PLAYER, this);

                final RespawnPlayerEvent event = SpongeEventFactory.createRespawnPlayerEvent(causeStack.getCurrentCause(),
                        transform, toTransform, this, this, false, true);
                Sponge.getEventManager().post(event);

                // Get the to transform, this can be overridden in the event
                transform = event.getToTransform();
            }
        }
        setWorld(world, (LanternWorld) transform.getExtent());
        setPosition(transform.getPosition());
    }

    private static LanternTabListEntryBuilder createTabListEntryBuilder(LanternPlayer player) {
        return new LanternTabListEntryBuilder()
                .profile(player.getProfile())
                .displayName(Text.of(player.getName())) // TODO
                .gameMode(player.get(Keys.GAME_MODE).get())
                .latency(player.getConnection().getLatency());
    }

    private static final Set<RelativePositions> RELATIVE_ROTATION = Sets.immutableEnumSet(
            RelativePositions.PITCH, RelativePositions.YAW);
    private static final Set<RelativePositions> RELATIVE_POSITION = Sets.immutableEnumSet(
            RelativePositions.X, RelativePositions.Y, RelativePositions.Z);

    @Override
    public boolean setPositionAndWorld(World world, Vector3d position) {
        final LanternWorld oldWorld = this.getWorld();
        final boolean success = super.setPositionAndWorld(world, position);
        if (success && world == oldWorld) {
            this.session.send(new MessagePlayOutPlayerPositionAndLook(position.getX(), position.getY(), position.getZ(), 0, 0, RELATIVE_ROTATION, 0));
        }
        return success;
    }

    @Override
    public void setPosition(Vector3d position) {
        super.setPosition(position);
        final LanternWorld world = getWorld();
        if (world != null) {
            this.session.send(new MessagePlayOutPlayerPositionAndLook(position.getX(), position.getY(), position.getZ(), 0, 0, RELATIVE_ROTATION, 0));
        }
    }

    @Override
    public void setRotation(Vector3d rotation) {
        super.setRotation(rotation);
        final LanternWorld world = getWorld();
        if (world != null) {
            this.session.send(new MessagePlayOutPlayerPositionAndLook(0, 0, 0,
                    (float) rotation.getX(), (float) rotation.getY(), RELATIVE_POSITION, 0));
        }
    }

    @Override
    public void setHeadRotation(Vector3d rotation) {
        setRotation(rotation);
    }

    @Override
    public Vector3d getHeadRotation() {
        return super.getRotation();
    }

    @Override
    public void setRawRotation(Vector3d rotation) {
        super.setRawRotation(rotation);
    }

    @Override
    protected void setRawHeadRotation(Vector3d rotation) {
        super.setRawRotation(rotation);
    }

    @Override
    public boolean setLocationAndRotation(Location<World> location, Vector3d rotation) {
        final World oldWorld = getWorld();
        final boolean success = super.setLocationAndRotation(location, rotation);
        if (success) {
            final World world = location.getExtent();
            // Only send this if the world isn't changed, otherwise will the position be resend anyway
            if (oldWorld == world) {
                final Vector3d pos = location.getPosition();
                final MessagePlayOutPlayerPositionAndLook message = new MessagePlayOutPlayerPositionAndLook(pos.getX(), pos.getY(), pos.getZ(),
                        (float) rotation.getX(), (float) rotation.getY(), Collections.emptySet(), 0);
                this.session.send(message);
            }
        }
        return success;
    }

    @Override
    public boolean setLocationAndRotation(Location<World> location, Vector3d rotation, EnumSet<RelativePositions> relativePositions) {
        final World oldWorld = getWorld();
        final boolean success = super.setLocationAndRotation(location, rotation, relativePositions);
        if (success) {
            final World world = location.getExtent();
            // Only send this if the world isn't changed, otherwise will the position be resend anyway
            if (oldWorld == world) {
                final Vector3d pos = location.getPosition();
                final MessagePlayOutPlayerPositionAndLook message = new MessagePlayOutPlayerPositionAndLook(pos.getX(), pos.getY(), pos.getZ(),
                        (float) rotation.getX(), (float) rotation.getY(), Sets.immutableEnumSet(relativePositions), 0);
                this.session.send(message);
            }
        }
        return success;
    }

    @Override
    public void setRawPosition(Vector3d position) {
        super.setRawPosition(position);
    }

    @Override
    public void pulse(int deltaTicks) {
        // Check whether the player is still active
        int timeout = Lantern.getGame().getGlobalConfig().getPlayerIdleTimeout();
        if (timeout > 0 && System.currentTimeMillis() - this.lastActiveTime >= timeout * 60000) {
            this.session.disconnect(t("multiplayer.disconnect.idling"));
            return;
        }

        super.pulse(deltaTicks);

        // TODO: Maybe async?
        pulseChunkChanges();

        // Pulse the interaction handler
        this.interactionHandler.pulse();

        // Stream the inventory updates
        final LanternContainer container = this.containerSession.getOpenContainer();
        (container == null ? this.inventoryContainer : container).tryGetClientContainer(this).update();

        this.resourcePackSendQueue.pulse();

        if (get(LanternKeys.IS_ELYTRA_FLYING).get()) {
            if (get(Keys.IS_SNEAKING).get()) {
                offer(LanternKeys.IS_ELYTRA_FLYING, false);
                offer(LanternKeys.ELYTRA_SPEED_BOOST, false);
            } else {
                offer(LanternKeys.ELYTRA_SPEED_BOOST, get(Keys.IS_SPRINTING).get());
            }
        }
    }

    /**
     * Gets the {@link ChunkLoadingTicket} that should be used
     * for this player.
     *
     * @return the chunk loading ticket
     */
    public ChunkLoadingTicket getChunkLoadingTicket() {
        // Allocate a new loading ticket, this can be null after
        // joining the server or switching worlds
        if (this.loadingTicket == null || ((ChunkLoadingTicket) this.loadingTicket).isReleased()) {
            this.loadingTicket = this.getWorld().getChunkManager().createPlayerEntityTicket(
                    Lantern.getMinecraftPlugin(), getUniqueId()).get();
            this.loadingTicket.bindToEntity(this);
        }
        return (ChunkLoadingTicket) this.loadingTicket;
    }

    public void pulseChunkChanges() {
        final LanternWorld world = getWorld();
        if (world == null) {
            return;
        }

        ChunkLoadingTicket loadingTicket = this.getChunkLoadingTicket();
        Vector3d position = this.getPosition();

        double xPos = position.getX();
        double zPos = position.getZ();

        int centralX = ((int) xPos) >> 4;
        int centralZ = ((int) zPos) >> 4;

        // Fail fast if the player hasn't moved a chunk
        if (this.lastChunkPos != null && this.lastChunkPos.getX() == centralX &&
                this.lastChunkPos.getY() == centralZ) {
            return;
        }

        this.lastChunkPos = new Vector2i(centralX, centralZ);

        // Get the radius of visible chunks
        int radius = Math.min(world.getProperties().getConfig().getGeneration().getViewDistance(),
                this.viewDistance == -1 ? Integer.MAX_VALUE : this.viewDistance + 1);

        final Set<Vector2i> previousChunks = new HashSet<>(this.knownChunks);
        final List<Vector2i> newChunks = new ArrayList<>();

        for (int x = (centralX - radius); x <= (centralX + radius); x++) {
            for (int z = (centralZ - radius); z <= (centralZ + radius); z++) {
                final Vector2i coords = new Vector2i(x, z);
                if (!previousChunks.remove(coords)) {
                    newChunks.add(coords);
                }
            }
        }

        // Early end if there's no changes
        if (newChunks.size() == 0 && previousChunks.size() == 0) {
            return;
        }

        // Sort chunks by distance from player - closer chunks sent/forced first
        Collections.sort(newChunks, (a, b) -> {
            double dx = 16 * a.getX() + 8 - xPos;
            double dz = 16 * a.getY() + 8 - zPos;
            double da = dx * dx + dz * dz;
            dx = 16 * b.getX() + 8 - xPos;
            dz = 16 * b.getY() + 8 - zPos;
            double db = dx * dx + dz * dz;
            return Double.compare(da, db);
        });

        ObservedChunkManager observedChunkManager = world.getObservedChunkManager();

        // Force all the new chunks to be loaded and track the changes
        newChunks.forEach(coords -> {
            observedChunkManager.addObserver(coords, this);
            loadingTicket.forceChunk(coords);
        });
        // Unforce old chunks so they can unload and untrack the chunk
        previousChunks.forEach(coords -> {
            observedChunkManager.removeObserver(coords, this, true);
            loadingTicket.unforceChunk(coords);
        });

        this.knownChunks.removeAll(previousChunks);
        this.knownChunks.addAll(newChunks);
    }

    @Override
    public boolean isOnline() {
        return this.session.getChannel().isActive();
    }

    @Override
    public Optional<Player> getPlayer() {
        return Optional.of(this);
    }

    @Override
    public void sendMessage(ChatType type, Text message) {
        checkNotNull(message, "message");
        checkNotNull(type, "type");
        if (this.chatVisibility.isVisible(type)) {
            this.session.send(((LanternChatType) type).getMessageProvider().apply(message, this.locale));
        }
    }

    @Override
    public void sendMessage(Text message) {
        sendMessage(ChatTypes.CHAT, message);
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
        if (getPosition().distanceSquared(position) < radius * radius) {
            spawnParticles(particleEffect, position);
        }
    }

    @Override
    public void playSound(SoundType sound, SoundCategory category, Vector3d position, double volume, double pitch, double minVolume) {
        checkNotNull(sound, "sound");
        checkNotNull(position, "position");
        checkNotNull(category, "category");
        this.session.send(((LanternSoundType) sound).createMessage(position,
                category, (float) Math.max(minVolume, volume), (float) pitch));
    }

    @Override
    public void playRecord(Vector3i position, RecordType recordType) {
        playOrStopRecord(position, checkNotNull(recordType, "recordType"));
    }

    @Override
    public void stopRecord(Vector3i position) {
        playOrStopRecord(position, null);
    }

    private void playOrStopRecord(Vector3i position, @Nullable RecordType recordType) {
        checkNotNull(position, "position");
        getConnection().send(new MessagePlayOutRecord(position, recordType));
    }

    @Override
    public void sendTitle(Title title) {
        this.session.send(LanternTitles.getMessages(checkNotNull(title, "title")));
    }

    @Override
    public void sendBookView(BookView bookView) {
        checkNotNull(bookView, "bookView");

        final DataView dataView = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);
        WrittenBookItemTypeObjectSerializer.writeBookData(dataView, bookView, this.locale);

        // Written book internal id
        final RawItemStack rawItemStack = new RawItemStack(387, 0, 1, dataView);
        final int slot = this.inventory.getHotbar().getSelectedSlotIndex();
        this.session.send(new MessagePlayOutSetWindowSlot(-2, slot, rawItemStack));
        this.session.send(new MessagePlayOutOpenBook(HandTypes.MAIN_HAND));
        this.session.send(new MessagePlayOutSetWindowSlot(-2, slot, this.inventory.getHotbar().getSelectedSlot().peek().orElse(null)));
    }

    @Override
    public void sendBlockChange(Vector3i position, BlockState state) {
        checkNotNull(state, "state");
        checkNotNull(position, "position");
        this.session.send(new MessagePlayOutBlockChange(position, BlockRegistryModule.get().getStateInternalIdAndData(state)));
    }

    @Override
    public void sendBlockChange(int x, int y, int z, BlockState state) {
        this.sendBlockChange(new Vector3i(x, y, z), state);
    }

    @Override
    public void resetBlockChange(Vector3i position) {
        checkNotNull(position, "position");
        LanternWorld world = this.getWorld();
        if (world == null) {
            return;
        }
        this.session.send(new MessagePlayOutBlockChange(position, BlockRegistryModule.get().getStateInternalIdAndData(world.getBlock(position))));
    }

    @Override
    public void resetBlockChange(int x, int y, int z) {
        this.resetBlockChange(new Vector3i(x, y, z));
    }

    @Override
    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = checkNotNull(locale, "locale");
    }

    @Override
    public boolean isViewingInventory() {
        return false;
    }

    @Override
    public Optional<Container> getOpenInventory() {
        return Optional.ofNullable(this.containerSession.getOpenContainer());
    }

    @Override
    public Optional<Container> openInventory(Inventory inventory) {
        checkNotNull(inventory, "inventory");
        LanternContainer container;
        if (inventory instanceof PlayerInventory) {
            return Optional.empty();
        } else if (inventory instanceof OpenableInventory) {
            container = new LanternContainer(this.inventory, (OpenableInventory) inventory);
        } else {
            throw new UnsupportedOperationException("Unsupported inventory type: " + inventory);
        }
        if (this.containerSession.setOpenContainer(container)) {
            return Optional.of(container);
        }
        return Optional.empty();
    }

    @Override
    public boolean closeInventory() {
        return this.containerSession.setOpenContainer(null);
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
        this.chatVisibility = checkNotNull(chatVisibility, "chatVisibility");
    }

    @Override
    public boolean isChatColorsEnabled() {
        return this.chatColorsEnabled;
    }

    @Override
    public MessageChannelEvent.Chat simulateChat(Text message, Cause cause) {
        checkNotNull(message, "message");
        checkNotNull(cause, "cause");

        final Text nameText = get(Keys.DISPLAY_NAME).get();
        final MessageChannel channel = getMessageChannel();
        final MessageChannelEvent.Chat event = SpongeEventFactory.createMessageChannelEventChat(cause,
                channel, Optional.of(channel), new MessageEvent.MessageFormatter(nameText, message), message, false);
        if (!Sponge.getEventManager().post(event) && !event.isMessageCancelled()) {
            event.getChannel().ifPresent(c -> c.send(this, event.getMessage(), ChatTypes.CHAT));
        }

        return event;
    }

    public void setChatColorsEnabled(boolean enabled) {
        this.chatColorsEnabled = enabled;
    }

    @Override
    public Set<SkinPart> getDisplayedSkinParts() {
        return this.get(LanternKeys.DISPLAYED_SKIN_PARTS).get();
    }

    @Override
    public NetworkSession getConnection() {
        return this.session;
    }

    public ResourcePackSendQueue getResourcePackSendQueue() {
        return this.resourcePackSendQueue;
    }

    @Override
    public void sendResourcePack(ResourcePack resourcePack) {
        this.resourcePackSendQueue.offer(resourcePack);
    }

    @Override
    public LanternTabList getTabList() {
        return this.tabList;
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
        return this.scoreboard;
    }

    @Override
    public void setScoreboard(Scoreboard scoreboard) {
        checkNotNull(scoreboard, "scoreboard");
        if (this.scoreboard != null && scoreboard != this.scoreboard) {
            this.scoreboard.removePlayer(this);
        }
        this.scoreboard = (LanternScoreboard) scoreboard;
        this.scoreboard.addPlayer(this);
    }

    @Override
    public Text getTeamRepresentation() {
        return Text.of(getName());
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
    public boolean respawnPlayer() {
        return false;
    }

    @Override
    public Optional<Entity> getSpectatorTarget() {
        return Optional.ofNullable(this.spectatorEntity);
    }

    @Override
    public void setSpectatorTarget(@Nullable Entity entity) {
        this.spectatorEntity = entity;
        triggerEvent(new SpectateEntityEvent(entity));
    }

    @Override
    public Optional<WorldBorder> getWorldBorder() {
        return Optional.ofNullable(this.worldBorder);
    }

    @Override
    public void setWorldBorder(@Nullable WorldBorder border, Cause cause) {
        checkNotNull(cause, "cause");
        if (this.worldBorder == border) {
            return;
        }
        final ChangeWorldBorderEvent.TargetPlayer event = SpongeEventFactory.createChangeWorldBorderEventTargetPlayer(
                cause, Optional.ofNullable(border), Optional.ofNullable(this.worldBorder), this);
        Sponge.getEventManager().post(event);
        if (event.isCancelled()) {
            return;
        }
        if (this.worldBorder != null) {
            this.worldBorder.removePlayer(this);
        }
        final LanternWorldBorder worldBorder = (LanternWorldBorder) border;
        if (worldBorder != null) {
            if (this.worldBorder == null) {
                getWorld().getWorldBorder().removePlayer(this);
            }
            worldBorder.addPlayer(this);
        } else {
            getWorld().getWorldBorder().addPlayer(this);
        }
        this.worldBorder = worldBorder;
    }

    public PlayerInteractionHandler getInteractionHandler() {
        return this.interactionHandler;
    }

    @Override
    public LanternPlayerInventory getInventory() {
        return this.inventory;
    }

    /**
     * Gets the {@link PlayerContainerSession}.
     *
     * @return The container session
     */
    public PlayerContainerSession getContainerSession() {
        return this.containerSession;
    }

    public PlayerInventoryContainer getInventoryContainer() {
        return this.inventoryContainer;
    }

    public void handleOnGroundState(boolean state) {
        setOnGround(state);
        if (state) {
            offer(LanternKeys.IS_ELYTRA_FLYING, false);
        }
    }

    public void handleStartElytraFlying() {
        // Check for the elytra item
        if (getInventory().getEquipment().getSlot(EquipmentTypes.CHESTPLATE)
                .get().peek().map(ItemStack::getType).orElse(null) != ItemTypes.ELYTRA) {
            return;
        }
        offer(LanternKeys.IS_ELYTRA_FLYING, true);
    }

    public CooldownTracker getCooldownTracker() {
        return this.cooldownTracker;
    }

    public AdvancementsProgress getAdvancementsProgress() {
        return this.advancementsProgress;
    }
}
