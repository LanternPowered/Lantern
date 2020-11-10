package org.lanternpowered.server.network.value

import org.spongepowered.api.data.type.ProfessionType
import org.spongepowered.api.data.type.VillagerType

data class VillagerData(
        val type: VillagerType,
        val profession: ProfessionType,
        val level: Int
)
