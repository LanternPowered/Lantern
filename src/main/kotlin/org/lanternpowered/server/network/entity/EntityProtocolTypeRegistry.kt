package org.lanternpowered.server.network.entity

import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.api.registry.CatalogTypeRegistryBuilder
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.entity.player.LanternPlayer
import org.lanternpowered.server.network.entity.vanilla.ArmorStandEntityProtocol
import org.lanternpowered.server.network.entity.vanilla.BatEntityProtocol
import org.lanternpowered.server.network.entity.vanilla.ChickenEntityProtocol
import org.lanternpowered.server.network.entity.vanilla.EnderDragonEntityProtocol
import org.lanternpowered.server.network.entity.vanilla.EndermiteEntityProtocol
import org.lanternpowered.server.network.entity.vanilla.ExperienceOrbEntityProtocol
import org.lanternpowered.server.network.entity.vanilla.GiantEntityProtocol
import org.lanternpowered.server.network.entity.vanilla.HumanEntityProtocol
import org.lanternpowered.server.network.entity.vanilla.HuskEntityProtocol
import org.lanternpowered.server.network.entity.vanilla.IronGolemEntityProcotol
import org.lanternpowered.server.network.entity.vanilla.ItemEntityProtocol
import org.lanternpowered.server.network.entity.vanilla.LightningEntityProtocol
import org.lanternpowered.server.network.entity.vanilla.MagmaCubeEntityProtocol
import org.lanternpowered.server.network.entity.vanilla.PaintingEntityProtocol
import org.lanternpowered.server.network.entity.vanilla.PigEntityProtocol
import org.lanternpowered.server.network.entity.vanilla.PlayerEntityProtocol
import org.lanternpowered.server.network.entity.vanilla.RabbitEntityProtocol
import org.lanternpowered.server.network.entity.vanilla.SheepEntityProtocol
import org.lanternpowered.server.network.entity.vanilla.SilverfishEntityProtocol
import org.lanternpowered.server.network.entity.vanilla.SlimeEntityProtocol
import org.lanternpowered.server.network.entity.vanilla.SnowmanEntityProtocol
import org.lanternpowered.server.network.entity.vanilla.VillagerEntityProtocol
import org.lanternpowered.server.network.entity.vanilla.ZombieEntityProtocol
import org.lanternpowered.server.network.entity.vanilla.ZombieVillagerEntityProtocol

val EntityProtocolTypeRegistry = catalogTypeRegistry<EntityProtocolType<*>> {
    fun CatalogTypeRegistryBuilder<EntityProtocolType<*>>.register(
            id: String, supplier: (LanternEntity) -> AbstractEntityProtocol<LanternEntity>
    ) = this.register(entityProtocolTypeOf(minecraftKey(id), LanternEntity::class, supplier))

    register(entityProtocolTypeOf(minecraftKey("player"), LanternPlayer::class, ::PlayerEntityProtocol))

    register("armor_stand", ::ArmorStandEntityProtocol)
    register("bat", ::BatEntityProtocol)
    register("chicken", ::ChickenEntityProtocol)
    register("ender_dragon", ::EnderDragonEntityProtocol)
    register("endermite", ::EndermiteEntityProtocol)
    register("experience_orb", ::ExperienceOrbEntityProtocol)
    register("giant", ::GiantEntityProtocol)
    register("human", ::HumanEntityProtocol)
    register("husk", ::HuskEntityProtocol)
    register("iron_golem", ::IronGolemEntityProcotol)
    register("item", ::ItemEntityProtocol)
    register("lightning", ::LightningEntityProtocol)
    register("magma_cube", ::MagmaCubeEntityProtocol)
    register("painting", ::PaintingEntityProtocol)
    register("pig", ::PigEntityProtocol)
    register("rabbit", ::RabbitEntityProtocol)
    register("sheep", ::SheepEntityProtocol)
    register("silverfish", ::SilverfishEntityProtocol)
    register("slime", ::SlimeEntityProtocol)
    register("snowman", ::SnowmanEntityProtocol)
    register("villager", ::VillagerEntityProtocol)
    register("zombie", ::ZombieEntityProtocol)
    register("zombie_villager", ::ZombieVillagerEntityProtocol)
}
