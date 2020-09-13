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
package org.lanternpowered.server.service.pagination

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.common.collect.MapMaker
import net.kyori.adventure.text.TextComponent
import org.lanternpowered.api.audience.Audience
import org.lanternpowered.api.entity.player.Player
import org.lanternpowered.api.text.textOf
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.api.util.optional.asOptional
import org.spongepowered.api.command.Command
import org.spongepowered.api.command.CommandExecutor
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.parameter.ArgumentReader
import org.spongepowered.api.command.parameter.CommandContext
import org.spongepowered.api.command.parameter.Parameter
import org.spongepowered.api.command.parameter.managed.ValueParameter
import org.spongepowered.api.service.pagination.PaginationList
import org.spongepowered.api.service.pagination.PaginationService
import org.spongepowered.api.util.Nameable
import java.util.Optional
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

internal class LanternPaginationService : PaginationService {

    class SourcePaginations {

        private val paginations = ConcurrentHashMap<UUID, ActivePagination>()

        @Volatile
        var lastId: UUID? = null
            private set

        operator fun get(uuid: UUID): ActivePagination? = this.paginations[uuid]

        fun put(pagination: ActivePagination) {
            synchronized(this.paginations) {
                this.paginations[pagination.id] = pagination
                this.lastId = pagination.id
            }
        }

        fun keys(): Set<UUID> = this.paginations.keys
    }

    private val activePaginations = MapMaker().weakKeys().makeMap<Audience, SourcePaginations>()
    private val activePlayerPaginations = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build<UUID, SourcePaginations>()

    override fun builder(): PaginationList.Builder = LanternPaginationBuilder(this)

    fun getPaginationState(source: Audience, create: Boolean): SourcePaginations? {
        return when {
            source is Player -> this.activePlayerPaginations.get(source.uniqueId) { if (create) SourcePaginations() else null }
            create -> this.activePaginations.computeIfAbsent(source) { SourcePaginations() }
            else -> this.activePaginations[source]
        }
    }

    fun createPaginationCommand(): Command.Parameterized {
        val paginationIdParameter = Parameter.builder(ActivePagination::class.java)
                .parser(ActivePaginationParameter())
                .setKey("pagination-id")
                .build()
        val next = Command.builder()
                .setShortDescription(textOf("Go to the next page"))
                .setExecutor { context ->
                    context.requireOne(paginationIdParameter).nextPage()
                    CommandResult.success()
                }.build()
        val prev = Command.builder()
                .setShortDescription(textOf("Go to the previous page"))
                .setExecutor { context ->
                    context.requireOne(paginationIdParameter).previousPage()
                    CommandResult.success()
                }.build()
        val pageParameter = Parameter.integerNumber().setKey("page").build()
        val pageExecutor = CommandExecutor { context ->
            context.requireOne(paginationIdParameter).specificPage(context.requireOne(pageParameter))
            CommandResult.success()
        }
        val page = Command.builder()
                .setShortDescription(TextComponent.of("Go to a specific page"))
                .parameter(pageParameter)
                .setExecutor(pageExecutor)
                .build()
        return Command.builder()
                .parameters(paginationIdParameter, Parameter.firstOf(pageParameter,
                        Parameter.subcommand(next, "next", "n"),
                        Parameter.subcommand(prev, "prev", "p", "previous"),
                        Parameter.subcommand(page, "page")))
                .child(page, "page")
                .setExecutor(page)
                .setShortDescription(TextComponent.of("Helper command for paginations occurring"))
                .build()
    }

    private inner class ActivePaginationParameter : ValueParameter<ActivePagination> {

        override fun getValue(
                parameterKey: Parameter.Key<in ActivePagination>,
                reader: ArgumentReader.Mutable,
                context: CommandContext.Builder
        ): Optional<ActivePagination> {
            val source = context.cause.audience
            val paginations = getPaginationState(source, false)
            if (paginations == null) {
                val name = if (source is Nameable) source.name else source.toString()
                throw reader.createException(textOf("Source $name has no paginations!"))
            }
            val state = reader.immutable
            val id = try {
                UUID.fromString(reader.parseString())
            } catch (ex: IllegalArgumentException) {
                val lastId = paginations.lastId
                if (lastId != null) {
                    reader.setState(state)
                    return paginations[lastId].asOptional()
                }
                throw reader.createException(TextComponent.of("Input was not a valid UUID!"))
            }
            return paginations[id]?.asOptional() ?: throw reader.createException(textOf("No pagination registered for id $id"))
        }

        override fun complete(context: CommandContext): List<String> {
            val audience = context.cause.audience
            val paginations = getPaginationState(audience, false)
            return (paginations?.keys() ?: emptyList<UUID>()).map { it.toString() }.toImmutableList()
        }
    }
}
