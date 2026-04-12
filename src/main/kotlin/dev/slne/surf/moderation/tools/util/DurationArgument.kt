package dev.slne.surf.moderation.tools.util

import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.api.core.util.object2IntMapOf
import org.bukkit.command.CommandSender
import kotlin.math.round
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class DurationArgument(nodeName: String, minimum: Duration = 1.milliseconds) :
    CustomArgument<Long, String>(StringArgument(nodeName), { info ->
        try {
            val reader = StringReader(info.input())
            val rawDuration = reader.readFloat()
            val unit = reader.readUnquotedString()
            val multiplier = UNITS.getOrDefault(unit, 0)

            if (multiplier == 0) {
                throw ERROR_INVALID_UNIT.createWithContext(reader)
            }

            val rounded = round(rawDuration * multiplier).toLong()
            if (rounded < minimum.inWholeMilliseconds) {
                throw ERROR_DURATION_TOO_LOW.create(rounded, minimum)
            }

            rounded
        } catch (e: CommandSyntaxException) {
            throw e
        }
    }) {

    init {
        replaceSuggestions { info, builder ->
            val reader = StringReader(builder.remaining)

            try {
                reader.readFloat()
                ArgumentSuggestions.strings<CommandSender>(UNITS.keys)
                    .suggest(info, builder.createOffset(builder.start + reader.cursor))
            } catch (_: CommandSyntaxException) {
                builder.buildFuture()
            }
        }
    }

    companion object {
        private val ERROR_INVALID_UNIT = SimpleCommandExceptionType(LiteralMessage("Ungültige Zeiteinheit"))
        private val ERROR_DURATION_TOO_LOW = Dynamic2CommandExceptionType { provided, minimum ->
            require(provided is Long)
            require(minimum is Duration)

            LiteralMessage("Die angegebene Dauer (${provided.milliseconds}) ist zu niedrig. Minimum: $minimum")
        }

        private val UNITS = object2IntMapOf(
            "" to 1,
            "ms" to 1,
            "s" to 1000,
            "m" to 60_000,
            "h" to 3_600_000,
            "d" to 86_400_000,
            "w" to 604_800_000,
        )
    }
}

inline fun CommandAPICommand.durationArgument(
    nodeName: String,
    minimum: Duration = 1.milliseconds,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand = withArguments(DurationArgument(nodeName, minimum).setOptional(optional).apply(block))