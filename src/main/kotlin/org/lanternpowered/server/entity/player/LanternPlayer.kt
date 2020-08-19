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
package org.lanternpowered.server.entity.player

import net.kyori.adventure.sound.SoundStop
import org.lanternpowered.api.Server
import org.lanternpowered.api.audience.MessageType
import org.lanternpowered.api.boss.BossBar
import org.lanternpowered.api.cause.Cause
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.cause.CauseStackManager
import org.lanternpowered.api.effect.sound.SoundEffect
import org.lanternpowered.api.entity.player.Player
import org.lanternpowered.api.entity.player.chat.ChatVisibilities
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.event.LanternEventFactory
import org.lanternpowered.api.item.inventory.itemStackOf
import org.lanternpowered.api.key.asNamespacedKey
import org.lanternpowered.api.scoreboard.Scoreboard
import org.lanternpowered.api.service.serviceOf
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.book.Book
import org.lanternpowered.api.text.textOf
import org.lanternpowered.api.text.title.Title
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.api.world.chunk.ChunkLoadingTicket
import org.lanternpowered.api.world.chunk.ChunkPosition
import org.lanternpowered.api.world.getGameRule
import org.lanternpowered.server.advancement.LanternPlayerAdvancements
import org.lanternpowered.server.config.ViewDistance
import org.lanternpowered.server.data.key.LanternKeys
import org.lanternpowered.server.effect.entity.EntityEffectTypes
import org.lanternpowered.server.effect.entity.sound.DefaultLivingFallSoundEffect
import org.lanternpowered.server.effect.entity.sound.DefaultLivingSoundEffect
import org.lanternpowered.server.effect.entity.sound.player.PlayerHurtSoundEffect
import org.lanternpowered.server.effect.sound.getEntityPacketBuilder
import org.lanternpowered.server.effect.sound.getPacketBuilder
import org.lanternpowered.server.entity.EntityBodyPosition
import org.lanternpowered.server.entity.LanternLiving
import org.lanternpowered.server.entity.event.SpectateEntityEvent
import org.lanternpowered.server.entity.player.tab.LanternTabList
import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.inventory.AbstractChildrenInventory
import org.lanternpowered.server.inventory.AbstractContainer
import org.lanternpowered.server.inventory.IContainerProvidedInventory
import org.lanternpowered.server.inventory.LanternItemStackSnapshot
import org.lanternpowered.server.inventory.PlayerInventoryContainer
import org.lanternpowered.server.inventory.PlayerInventoryContainerSession
import org.lanternpowered.server.inventory.PlayerTopBottomContainer
import org.lanternpowered.server.inventory.vanilla.PlayerInventoryShiftClickBehavior
import org.lanternpowered.server.inventory.vanilla.VanillaInventoryArchetypes
import org.lanternpowered.server.item.LanternCooldownTracker
import org.lanternpowered.server.network.NetworkSession
import org.lanternpowered.server.network.entity.EntityProtocolManager
import org.lanternpowered.server.network.entity.NetworkIdHolder
import org.lanternpowered.server.network.vanilla.packet.type.play.BlockChangePacket
import org.lanternpowered.server.network.vanilla.packet.type.play.ChatMessagePacket
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenBookPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenSignPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.ParticleEffectPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.SetActiveAdvancementTreePacket
import org.lanternpowered.server.network.vanilla.packet.type.play.SetMusicDiscPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.SetWindowSlotPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.StopSoundsPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.TitlePacket
import org.lanternpowered.server.network.vanilla.packet.type.play.UpdateViewPositionPacket
import org.lanternpowered.server.registry.type.block.BlockStateRegistry
import org.lanternpowered.server.scoreboard.LanternScoreboard
import org.lanternpowered.server.text.title.toPackets
import org.lanternpowered.server.user.LanternUser
import org.lanternpowered.server.world.LanternWorldBorder
import org.lanternpowered.server.world.LanternWorldNew
import org.spongepowered.api.advancement.Advancement
import org.spongepowered.api.advancement.AdvancementProgress
import org.spongepowered.api.advancement.AdvancementTree
import org.spongepowered.api.block.BlockState
import org.spongepowered.api.block.entity.Sign
import org.spongepowered.api.data.Keys
import org.spongepowered.api.data.type.HandTypes
import org.spongepowered.api.data.type.SkinPart
import org.spongepowered.api.effect.particle.ParticleEffect
import org.spongepowered.api.effect.sound.SoundTypes
import org.spongepowered.api.effect.sound.music.MusicDisc
import org.spongepowered.api.entity.living.player.CooldownTracker
import org.spongepowered.api.entity.living.player.PlayerChatRouter
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.entity.living.player.chat.ChatVisibility
import org.spongepowered.api.entity.living.player.tab.TabList
import org.spongepowered.api.event.message.PlayerChatEvent
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.Container
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.item.inventory.type.ViewableInventory
import org.spongepowered.api.profile.GameProfile
import org.spongepowered.api.resourcepack.ResourcePack
import org.spongepowered.api.service.ban.BanService
import org.spongepowered.api.util.AABB
import org.spongepowered.api.util.ban.Ban
import org.spongepowered.api.world.WorldBorder
import org.spongepowered.api.world.gamerule.GameRules
import org.spongepowered.math.vector.Vector2i
import org.spongepowered.math.vector.Vector3d
import org.spongepowered.math.vector.Vector3i
import java.util.Optional
import java.util.function.Supplier
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.minutes

class LanternPlayer(
        profile: GameProfile, private val connection: NetworkSession
) : AbstractPlayer(profile), Player, NetworkIdHolder {

    companion object {

        val DEFAULT_CHAT_ROUTER = PlayerChatRouter { player, message ->
            val displayName = player.require(Keys.DISPLAY_NAME)
            Server.sendMessage(translatableTextOf("chat.type.text", displayName, message))
        }

        val DEFAULT_EFFECT_COLLECTION = LanternLiving.DEFAULT_EFFECT_COLLECTION.toBuilder()
                // Override the fall sound
                .replaceOrAdd(EntityEffectTypes.FALL, DefaultLivingFallSoundEffect::class.java,
                        DefaultLivingFallSoundEffect(
                                SoundTypes.ENTITY_PLAYER_SMALL_FALL,
                                SoundTypes.ENTITY_PLAYER_BIG_FALL))
                // Override the hurt sound
                .replaceOrAdd(EntityEffectTypes.HURT, DefaultLivingSoundEffect::class.java,
                        PlayerHurtSoundEffect(EntityBodyPosition.HEAD))
                // Override the death sound
                .replaceOrAdd(EntityEffectTypes.DEATH, DefaultLivingSoundEffect::class.java,
                        DefaultLivingSoundEffect(EntityBodyPosition.HEAD, SoundTypes.ENTITY_PLAYER_DEATH))
                .build()

        private val BOUNDING_BOX_EXTENT = AABB(Vector3d(-0.3, 0.0, -0.3), Vector3d(0.3, 1.8, 0.3))
    }

    private val interactionHandler = PlayerInteractionHandler(this)
    private val _user: LanternUser by lazy { this.server.userManager.getOrCreate(this.profile) as LanternUser }

    init {
        this.initInventoryContainer()
        this.effectCollection = DEFAULT_EFFECT_COLLECTION.copy()
        this.boundingBoxExtent = BOUNDING_BOX_EXTENT

        keyRegistry {
            register(Keys.IS_SLEEPING_IGNORED, false)

            registerProvider(Keys.ACTIVE_ITEM) {
                setFastAnd { item ->
                    if (item.isEmpty) {
                        this.interactionHandler.cancelActiveItem()
                        true
                    } else {
                        // You cannot change the active item, only cancel it
                        false
                    }
                }
                get { this.interactionHandler.activeItem }
            }

            get(LanternKeys.OPEN_ADVANCEMENT_TREE)?.addChangeListener { newTree ->
                if (this.nullableWorld == null)
                    return@addChangeListener
                this.connection.send(SetActiveAdvancementTreePacket(newTree?.key))
            }

            register(Keys.SPECTATOR_TARGET).addChangeListener { newEntity ->
                this.triggerEvent(SpectateEntityEvent(newEntity))
            }
        }

        this._user.load(this)
    }

    /**
     * Releases the player instance, is called after
     * the player disconnected to cleanup remaining references.
     */
    fun release() {
        this._user.reset()
        // Destroy the player entity
        this.unload(UnloadState.REMOVED)
        // Detach the player from the world
        // TODO this.setWorld(null)
        // Release the players entity id
        EntityProtocolManager.releaseEntityId(this.networkId)
    }

    override fun getName(): String = this.profile.name.get()
    override fun getConnection(): NetworkSession = this.connection
    override fun getWorld(): LanternWorldNew = super<AbstractPlayer>.getWorld()
    override fun getUser(): User = this._user

    override fun kick() = this.connection.close()
    override fun kick(reason: Text) = this.connection.close(reason)

    override fun handleDeath(causeStack: CauseStack) {
        // Call the harvest event
        val keepsInventory: Boolean = this.world.getGameRule(GameRules.KEEP_INVENTORY)
        val exp = if (keepsInventory) 0 else min(100, this.get(Keys.EXPERIENCE_LEVEL).orElse(0) * 7)
        // Humanoids get their own sub-interface for the event
        val harvestEvent = LanternEventFactory.createHarvestEntityEventTargetPlayer(
                causeStack.currentCause, exp, exp, this, keepsInventory, keepsInventory, 0)
        EventManager.post(harvestEvent)
        if (!harvestEvent.isCancelled) {
            val drops = mutableListOf<ItemStackSnapshot>()
            if (!harvestEvent.keepsInventory()) {
                // Make a copy of all the items in the players inventory, and put them in the drops
                for (slot in this.inventory.slots()) {
                    val stack = slot.peek()
                    if (!stack.isEmpty)
                        drops.add(LanternItemStackSnapshot.wrap(stack))
                }
            }
            if (!harvestEvent.keepsLevel())
                this.offer(Keys.EXPERIENCE_LEVEL, harvestEvent.level)
            // Finalize the harvest event
            this.finalizeHarvestEvent(causeStack, harvestEvent, drops)
        }

        // Ban the player if the world is hardcode
        if (this.world.properties.isHardcore) {
            val banService = serviceOf<BanService>() ?: return
            // Add a permanent ban
            banService.addBan(Ban.of(this.profile, translatableTextOf("gameMode.hardcore.banMessage")))
            // Bye, bye!
            this.kick(translatableTextOf("deathScreen.title.hardcore"))
        }
    }

    override fun updateDeath(deltaTime: Duration): Boolean {
        // A player is never removed after a delay, it will exist until
        // the player respawns or disconnects.
        return this.isDead
    }

    override fun update(deltaTime: Duration) {
        if (this.checkIdle())
            return

        super.update(deltaTime)

        this.updateChunkChanges()
    }

    // region Idle

    private var lastActiveTime = -1L

    private fun checkIdle(): Boolean {
        val idleTime = this.lastActiveTime
        if (idleTime == -1L) {
            this.lastActiveTime = System.currentTimeMillis()
        } else if (System.currentTimeMillis() - idleTime > this.server.config.server.playerIdleTimeout.minutes.inMilliseconds) {
            this.kick(translatableTextOf("multiplayer.disconnect.idling"))
            return true
        }
        return false
    }

    /**
     * Resets the time this player has been idle for, if the time reaches
     * the timeout limit the player will be kicked.
     */
    fun resetIdleTime() {
        this.lastActiveTime = System.currentTimeMillis()
    }

    // endregion

    // region Sign

    private var _openedSignPosition: Vector3i? = null

    /**
     * The opened sign position, this doesn't mean that it's actually
     * open but the last known position it was open. As long this isn't null,
     * this player can edit the sign at the returned position.
     */
    val openedSignPosition: Vector3i?
        get() = this._openedSignPosition

    /**
     * Resets the sign editing session, this player will have to attempt
     * to interact again with the sign to allow to edit it.
     */
    fun resetOpenedSignPosition() {
        this._openedSignPosition = null
    }

    /**
     * Attempts to open the sign at the given position and returns
     * whether it was successful.
     *
     * @param position The position
     * @return Whether opening the sign was successful
     */
    fun openSignAt(position: Vector3i): Boolean {
        if (this.world.getBlockEntity(position).orNull() !is Sign)
            return false
        this.connection.send(OpenSignPacket(position))
        this._openedSignPosition = position
        return true
    }

    // endregion

    // region View Distance

    private var viewDistance = ViewDistance.DEFAULT

    override fun getViewDistance(): Int = this.viewDistance

    override fun setViewDistance(distance: Int) {
        check(distance >= 1) { "The view distance must be at least 1" }
        this.viewDistance = distance
    }

    // endregion

    private val resourcePacketSendQueue = ResourcePackSendQueue(this)

    override fun sendResourcePack(pack: ResourcePack) = this.resourcePacketSendQueue.offer(pack)

    override fun getDisplayedSkinParts(): Set<SkinPart> = this.require(LanternKeys.DISPLAYED_SKIN_PARTS)

    private val tabList = LanternTabList(this)

    override fun getTabList(): TabList = this.tabList

    private val cooldownTracker = LanternCooldownTracker(this)

    override fun getCooldownTracker(): CooldownTracker = this.cooldownTracker

    override fun respawnPlayer(): Boolean {
        TODO("Not yet implemented")
    }

    // region World Border

    /**
     * The world border this player is currently tracking, if null, the player
     * will track the one of the current world.
     */
    private var worldBorder: LanternWorldBorder? = null

    override fun getWorldBorder(): Optional<WorldBorder> = this.worldBorder.asOptional()

    override fun setWorldBorder(border: WorldBorder?) {
        val oldBorder = this.worldBorder
        if (border == oldBorder)
            return
        val cause = CauseStackManager.currentCause
        val event = LanternEventFactory.createChangeWorldBorderEventTargetPlayer(
                cause, oldBorder, border, this)
        EventManager.post(event)
        if (event.isCancelled)
            return
        oldBorder?.removePlayer(this)
        if (border != null) {
            if (oldBorder == null)
                this.world.border.removePlayer(this)
            border as LanternWorldBorder
            border.addPlayer(this)
        } else {
            this.world.border.addPlayer(this)
        }
        this.worldBorder = border as? LanternWorldBorder
    }

    // endregion

    // region Scoreboard

    private var scoreboard: LanternScoreboard = this.connection.server.scoreboard

    override fun getScoreboard(): Scoreboard = this.scoreboard

    override fun setScoreboard(scoreboard: Scoreboard) {
        if (scoreboard !== this.scoreboard) {
            this.scoreboard.removePlayer(this)
        }
        this.scoreboard = scoreboard as LanternScoreboard
        this.scoreboard.addPlayer(this)
    }

    // endregion

    // region Audience

    private var bossBarManager = PlayerBossBarManager(this)

    override fun showBossBar(bar: BossBar) = this.bossBarManager.show(bar)
    override fun hideBossBar(bar: BossBar) = this.bossBarManager.hide(bar)

    fun clearBossBars() = this.bossBarManager.clear()

    override fun showTitle(title: Title) {
        this.connection.send(title.toPackets())
    }

    override fun resetTitle() {
        this.connection.send(TitlePacket.Reset)
    }

    override fun clearTitle() {
        this.connection.send(TitlePacket.Clear)
    }

    override fun sendActionBar(message: Text) {
        this.connection.send(TitlePacket.SetActionbarTitle(message))
    }

    override fun playMusicDisc(position: Vector3i, musicDisc: MusicDisc) {
        this.connection.send(SetMusicDiscPacket(position, musicDisc))
    }

    override fun playMusicDisc(position: Vector3i, musicDiscType: Supplier<out MusicDisc>) = this.playMusicDisc(position, musicDiscType.get())

    override fun stopMusicDisc(position: Vector3i) {
        this.connection.send(SetMusicDiscPacket(position, null))
    }

    override fun stopSound(stop: SoundStop) {
        val sound = stop.sound()?.asNamespacedKey()
        val category = stop.source()
        this.connection.send(StopSoundsPacket(sound, category))
    }

    override fun playSound(sound: SoundEffect) {
        this.connection.send(sound.getEntityPacketBuilder()(this))
    }

    override fun playSound(sound: SoundEffect, x: Double, y: Double, z: Double) = this.playSound(sound, Vector3d(x, y, z))

    override fun playSound(sound: SoundEffect, position: Vector3d) {
        this.connection.send(sound.getPacketBuilder()(position))
    }

    override fun spawnParticles(particleEffect: ParticleEffect, position: Vector3d, radius: Int) {
        if (this.position.distanceSquared(position) < radius * radius)
            this.spawnParticles(particleEffect, position)
    }

    override fun spawnParticles(particleEffect: ParticleEffect, position: Vector3d) {
        this.connection.send(ParticleEffectPacket(position, particleEffect))
    }

    override fun sendBlockChange(position: Vector3i, state: BlockState) {
        val internalId = BlockStateRegistry.getId(state)
        this.connection.send(BlockChangePacket(position, internalId))
    }

    override fun sendBlockChange(x: Int, y: Int, z: Int, state: BlockState) = this.sendBlockChange(Vector3i(x, y, z), state)

    override fun resetBlockChange(position: Vector3i) {
        val world = this.nullableWorld ?: return
        this.sendBlockChange(position, world.getBlock(position))
    }

    override fun resetBlockChange(x: Int, y: Int, z: Int) = this.resetBlockChange(Vector3i(x, y, z))

    override fun openBook(book: Book) {
        this.resetOpenedSignPosition()

        val itemStack = itemStackOf(ItemTypes.WRITTEN_BOOK) {
            add(Keys.AUTHOR, book.author())
            add(Keys.DISPLAY_NAME, book.title())
            add(Keys.PAGES, book.pages())
        }

        val slot = this.inventory.hotbar.selectedSlotIndex
        this.connection.send(SetWindowSlotPacket(-2, slot, itemStack))
        this.connection.send(OpenBookPacket(HandTypes.MAIN_HAND.get()))
        this.connection.send(SetWindowSlotPacket(-2, slot, this.inventory.hotbar.selectedSlot.peek()))
    }

    // endregion

    // region Chat

    private var chatRouter = DEFAULT_CHAT_ROUTER
    private var chatVisibility = ChatVisibilities.FULL.get()
    private var chatColorsEnabled = true

    override fun getChatVisibility(): ChatVisibility = this.chatVisibility

    fun setChatVisibility(chatVisibility: ChatVisibility) {
        this.chatVisibility = chatVisibility
    }

    override fun isChatColorsEnabled(): Boolean = this.chatColorsEnabled

    fun setChatColorsEnabled(enabled: Boolean) {
        this.chatColorsEnabled = enabled
    }

    override fun sendMessage(message: Text) = this.sendMessage(message, MessageType.CHAT)

    override fun sendMessage(message: Text, type: MessageType) {
        if (!this.chatVisibility.isVisible(type))
            return
        this.connection.send(ChatMessagePacket(message, type))
    }

    override fun getChatRouter(): PlayerChatRouter = this.chatRouter

    override fun setChatRouter(router: PlayerChatRouter) {
        this.chatRouter = router
    }

    override fun simulateChat(message: Text, cause: Cause): PlayerChatEvent {
        val router = this.chatRouter
        val event = LanternEventFactory.createPlayerChatEvent(cause, router, router, message, message)
        EventManager.post(event)
        if (event.isCancelled)
            return event
        event.chatRouter.orNull()?.chat(this, event.message)
        return event
    }

    // endregion

    // region Advancements

    val advancementsProgress = LanternPlayerAdvancements(this)

    override fun getUnlockedAdvancementTrees(): Collection<AdvancementTree> = this.advancementsProgress.unlockedAdvancementTrees
    override fun getProgress(advancement: Advancement): AdvancementProgress = this.advancementsProgress.get(advancement)

    // endregion

    // region Inventory

    private lateinit var inventoryContainer: PlayerInventoryContainer
    val inventoryContainerSession = PlayerInventoryContainerSession(this)

    private fun initInventoryContainer() {
        this.inventoryContainer = PlayerInventoryContainer(this.inventory,
                AbstractChildrenInventory.viewBuilder()
                        .title(this.require(Keys.DISPLAY_NAME))
                        .inventory(VanillaInventoryArchetypes.CRAFTING.builder()
                                .build(Lantern.getMinecraftPlugin()))
                        .inventory(this.inventory.armor)
                        .inventory(this.inventory.offhand)
                        .shiftClickBehavior(PlayerInventoryShiftClickBehavior.INSTANCE)
                        .build(Lantern.getMinecraftPlugin()))
    }

    override fun getEnderChestInventory(): Inventory = this.enderChestInventory

    override fun getOpenInventory(): Optional<Container> = this.inventoryContainerSession.openContainer.asOptional()

    override fun openInventory(inventory: Inventory): Optional<Container> =
            this.openInventory(inventory, inventory.get(Keys.DISPLAY_NAME).orElseGet { textOf("Inventory") })

    override fun openInventory(inventory: Inventory, displayName: Text): Optional<Container> {
        // The inventory must be viewable
        if (inventory !is ViewableInventory)
            return Optional.empty()
        val container: AbstractContainer
        if (inventory is IContainerProvidedInventory) {
            container = inventory.createContainer(this)
        } else {
            container = PlayerTopBottomContainer.construct(this.inventory, inventory as AbstractChildrenInventory)
            container.setName(displayName)
        }
        return if (this.inventoryContainerSession.setOpenContainer(container)) container.asOptional() else emptyOptional()
    }

    override fun closeInventory(): Boolean {
        return this.inventoryContainerSession.setOpenContainer(null)
    }

    // endregion

    private var networkId = -1

    override fun getNetworkId(): Int = this.networkId

    fun setNetworkId(id: Int) {
        this.networkId = id
    }

    // region Chunk Loading

    private val loadingTicket: ChunkLoadingTicket? = null
    private var lastChunkPos: Vector2i? = null
    private val knownChunks = mutableSetOf<Vector2i>()

    private fun updateChunkChanges() {
        var loadingTicket = this.loadingTicket
        if (loadingTicket == null) {
            loadingTicket = this.world.chunkManager.createTicket()
        }

        val position = this.position
        val xPos = position.x
        val zPos = position.z
        val centralX = xPos.toInt() shr 4
        val centralZ = zPos.toInt() shr 4

        // Fail fast if the player hasn't moved a chunk
        val lastChunkPos = this.lastChunkPos
        if (lastChunkPos != null && lastChunkPos.x == centralX && lastChunkPos.y == centralZ) {
            return
        }
        this.lastChunkPos = Vector2i(centralX, centralZ)
        this.connection.send(UpdateViewPositionPacket(centralX, centralZ))
        val previousChunks = this.knownChunks.toMutableSet()
        val newChunks = mutableListOf<Vector2i>()
        val viewDistance = this.world.properties.viewDistance
        for (x in centralX - viewDistance..centralX + viewDistance) {
            for (z in centralZ - viewDistance..centralZ + viewDistance) {
                val coords = Vector2i(x, z)
                if (!previousChunks.remove(coords))
                    newChunks.add(coords)
            }
        }

        // Early end if there's no changes
        if (newChunks.size == 0 && previousChunks.size == 0)
            return

        // Sort chunks by distance from player - closer chunks are sent/forced first
        newChunks.sortedWith(Comparator { a: Vector2i, b: Vector2i ->
            var dx = 16 * a.x + 8 - xPos
            var dz = 16 * a.y + 8 - zPos
            val da = dx * dx + dz * dz
            dx = 16 * b.x + 8 - xPos
            dz = 16 * b.y + 8 - zPos
            val db = dx * dx + dz * dz
            da.compareTo(db)
        })

        val observedChunkManager = this.world.observedChunkManager
        // Force all the new chunks to be loaded and track the changes
        newChunks.forEach { coords: Vector2i ->
            observedChunkManager.addObserver(coords, this)
            loadingTicket.acquire(ChunkPosition(coords.x, coords.y))
        }

        // Unforce old chunks so they can unload and untrack the chunk
        previousChunks.forEach { coords: Vector2i ->
            observedChunkManager.removeObserver(coords, this, true)
            loadingTicket.release(ChunkPosition(coords.x, coords.y))
        }

        this.knownChunks.removeAll(previousChunks)
        this.knownChunks.addAll(newChunks)
    }

    // endregion
}
