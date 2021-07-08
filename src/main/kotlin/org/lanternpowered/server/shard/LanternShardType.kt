package org.lanternpowered.server.shard

import org.lanternpowered.api.shard.Shard
import org.lanternpowered.api.shard.ShardType

class LanternShardType<T : Shard<T>>(override val type: Class<T>) : ShardType<T>
