package dev.slne.surf.moderation.tools.faq

import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
<<<<<<< master
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class Faq(
    val id: String,
    val content: String,
    var enabled: Boolean = true
) : ComponentLike {
    override fun asComponent(): Component = miniMessage().deserialize(content)
=======

enum class Faq(val id: String, message: SurfComponentBuilder.() -> Unit) {
    ASK("ask", {
        text("Wenn du eine Frage hast, stell sie einfach. ")
        text("Such nicht nach bestimmten Leuten und frag nicht erst, ob du fragen darfst oder ob jemand da ist. ")
        text("Stell deine Frage direkt und hab kurz Geduld, bis du eine Antwort bekommst.")
    }),

    CUSTOM_ENCHANTMENTS("custom-enchantments", {
        append {
            variableValue("Hier")
            hoverEvent(buildText { spacer("Klicke, um zur Informationsseite zu gelangen.") })
            clickOpensUrl("https://server.castcrafter.de/custom-enchantments.html")
        }
        text(" erfährst du alles über die Custom Enchantments.")
    }),

    FEATURES_SURVIVAL_SERVER("features-survival-server", {
        text("Der Survival Server hat einige Features, welche du")
        appendSpace()
        append {
            variableValue("hier")
            hoverEvent(buildText { spacer("Klicke, um zur Informationsseite zu gelangen.") })
            clickOpensUrl("https://server.castcrafter.de/features.html")
        }
        appendSpace()
        text("einsehen kannst.")
    }),

    HOW_TO_INSTALL_VOICE_CHAT("how-to-install-voice-chat", {
        append {
            variableValue("Hier")
            hoverEvent(buildText { spacer("Klicke, um zur Informationsseite zu gelangen.") })
            clickOpensUrl("https://server.castcrafter.de/faq.html#install-voicechat")
        }
        appendSpace()
        text("erfährst du, wie du den Voice Chat installierst.")
    }),

    HOW_TO_JOIN("how-to-join", {
        text("Informationen, z. B. wie du dich whitelisten lassen kannst, findest du")
        appendSpace()
        append {
            variableValue("hier")
            hoverEvent(buildText { spacer("Klicke, um auf die Informationsseite zu gelangen.") })
            clickOpensUrl("https://server.castcrafter.de/how-to-join")
        }
        text(". Für Events brauchst du keine Whitelist – nur für den Survival-Server.")
    }),

    HOW_TO_OPEN_TICKET("how-to-open-ticket", {
        text("Eine Anleitung zum Erstellen eines Tickets findest du")
        appendSpace()
        append {
            variableValue("hier")
            hoverEvent(buildText { spacer("Klicke, um auf die Informationsseite zu gelangen.") })
            clickOpensUrl("https://server.castcrafter.de/support")
        }
        text(".")
    }),

    INFORMATION_ABOUT_THE_CLAN_SYSTEM("information-about-the-clan-system", {
        text("Du möchtest mehr über Clans wissen?")
        appendSpace()
        append {
            variableValue("Hier")
            hoverEvent(buildText { spacer("Klicke, um zur Informationsseite zu gelangen.") })
            clickOpensUrl("https://server.castcrafter.de/clan-more-info")
        }
        appendSpace()
        text("findest du eine Übersicht mit zusätzlichen Informationen.")
    }),

    INFORMATION_ABOUT_THE_EVENT_SERVER("information-about-the-event-server", {
        append {
            variableValue("Hier")
            hoverEvent(buildText { spacer("Klicke, um zur Informationsseite zu gelangen.") })
            clickOpensUrl("https://server.castcrafter.de/event-server.html")
        }
        appendSpace()
        text("erfährst du alles über den Event-Server.")
    }),

    INFORMATION_ABOUT_THE_PLOTS("information-about-the-plots", {
        append {
            variableValue("Hier")
            hoverEvent(buildText { spacer("Klicke, um zur Informationsseite zu gelangen.") })
            clickOpensUrl("https://server.castcrafter.de/plots-homepage.html")
        }
        appendSpace()
        text("erfährst du alles über das Plotsystem.")
    }),

    INFORMATION_ABOUT_THE_SURVIVAL_SERVER("information-about-the-survival-server", {
        append {
            variableValue("Hier")
            hoverEvent(buildText { spacer("Klicke, um zur Informationsseite zu gelangen.") })
            clickOpensUrl("https://server.castcrafter.de/survival-server.html")
        }
        appendSpace()
        text("erfährst du alles über den Survival-Server.")
    }),

    NEXT_EVENT("next-event", {
        text("Wenn ein Event geplant ist, wird es im")
        appendSpace()
        append {
            variableValue("Discord")
            hoverEvent(buildText { spacer("Klicke, um zum Discord zu gelangen.") })
            clickOpensUrl("https://discord.com/channels/133198459531558912/980810495877607524")
        }
        appendSpace()
        text("oder von CastCrafter im")
        appendSpace()
        append {
            variableValue("Stream")
            hoverEvent(buildText { spacer("Klicke, um zum Twitch-Kanal zu gelangen.") })
            clickOpensUrl("https://www.twitch.tv/castcrafter")
        }
        appendSpace()
        text("angekündigt.")
    }),

    PATIENCE("patience", {
        text("Wenn dir nicht sofort geantwortet wird, habe bitte etwas Geduld. Wir machen das alles in unserer Freizeit und ehrenamtlich.")
    }),

    READ_THE_DOCS("read-the-docs", {
        text("Diese und weitere Fragen werden in unserer ausführlichen")
        appendSpace()
        append {
            variableValue("Dokumentation")
            hoverEvent(buildText { spacer("Klicke, um auf die Informationsseite zu gelangen.") })
            clickOpensUrl("https://server.castcrafter.de/community-server-landing-page")
        }
        appendSpace()
        text("beantwortet.")
    }),

    REPORT_BUG("report-bug", {
        text("Du hast einen Bug gefunden?")
        appendSpace()
        append {
            variableValue("Hier")
            hoverEvent(buildText { spacer("Klicke, um auf die Informationsseite zu gelangen.") })
            clickOpensUrl("https://server.castcrafter.de/support#bugreport-ticket")
        }
        appendSpace()
        text("erfährst du, wie du ihn melden kannst, um dem Server zu helfen.")
    }),

    REPORT_PLAYER("report-player", {
        text("Du möchtest einen Spieler melden?")
        appendSpace()
        append {
            variableValue("Hier")
            hoverEvent(buildText { spacer("Klicke, um zur Informationsseite zu gelangen.") })
            clickOpensUrl("https://server.castcrafter.de/support#report-ticket")
        }
        appendSpace()
        text("zeigen wir dir, wie das geht.")
    }),

    RULEBOOK("rulebook", {
        text("Alle Regeln des Servers findest du")
        appendSpace()
        append {
            variableValue("hier")
            hoverEvent(buildText { spacer("Klicke, um auf die Informationsseite zu gelangen.") })
            clickOpensUrl("https://server.castcrafter.de/rules")
        }
        text(".")
    }),

    SERVER_MODPACK("server-modpack", {
        append {
            variableValue("Hier")
            hoverEvent(buildText { spacer("Klicke, um auf die Downloadseite zu gelangen.") })
            clickOpensUrl("https://modrinth.com/modpack/castcrafter-survival-server")
        }
        appendSpace()
        text("kannst du das Server-Modpack herunterladen.")
    }),

    SURVIVAL_EVENTS("survival-events", {
        text("Welche Survival-Events es auf dem Survival-Server gibt und wann diese stattfinden, erfährst du")
        appendSpace()
        append {
            variableValue("hier")
            hoverEvent(buildText { spacer("Klicke, um zur Informationsseite zu gelangen.") })
            clickOpensUrl("https://server.castcrafter.de/events.html")
        }
        text(".")
    }),

    TAKE_PART_IN_EVENT("take-part-in-event", {
        text("Wie du an einem Event teilnehmen kannst, erfährst du")
        appendSpace()
        append {
            variableValue("hier")
            hoverEvent(buildText { spacer("Klicke, um zur Informationsseite zu gelangen.") })
            clickOpensUrl("https://server.castcrafter.de/how-to-take-part-in-an-event")
        }
        text(".")
    }),

    VETERAN_BENEFITS("veteran-benefits", {
        text("Veteranen haben den Vorteil einer Priority Queue und kommen dadurch schneller auf den Server. ")
        text("Wie du den Rang bekommst, erfährst du")
        appendSpace()
        append {
            variableValue("hier")
            hoverEvent(buildText { spacer("Klicke, um zur Informationsseite zu gelangen.") })
            clickOpensUrl("https://server.castcrafter.de/faq.html#veteran-info")
        }
        text(".")
    }),

    WHEN_DOES_THE_END_OPEN("when-does-the-end-open", {
        text("Wann das End öffnet, ist noch unklar. Falls es aber feststeht, wird es in den Server-Updates auf dem")
        appendSpace()
        append {
            variableValue("Discord")
            hoverEvent(buildText { spacer("Klicke, um zum Discord zu gelangen.") })
            clickOpensUrl("https://discord.com/channels/133198459531558912/980810495877607524")
        }
        appendSpace()
        text("angekündigt.")
    }),

    WHEN_DOES_THE_NETHER_OPEN("when-does-the-nether-open", {
        text("Wann der Nether öffnet, ist noch unklar. Falls es aber feststeht, wird es in den Server-Updates auf dem")
        appendSpace()
        append {
            variableValue("Discord")
            hoverEvent(buildText { spacer("Klicke, um zum Discord zu gelangen.") })
            clickOpensUrl("https://discord.com/channels/133198459531558912/980810495877607524")
        }
        appendSpace()
        text("angekündigt.")
    }),

    WHY_NO_ELYTRA_IN_THE_END("why-no-elytra-in-the-end", {
        text("Du fragst dich, warum man im End nicht mit Elytren fliegen kann?")
        appendSpace()
        append {
            variableValue("Hier")
            hoverEvent(buildText { spacer("Klicke, um zur Informationsseite zu gelangen.") })
            clickOpensUrl("https://server.castcrafter.de/faq#why-no-elytra-in-end")
        }
        appendSpace()
        text("findest du die Antwort.")
    }),

    WHY_NO_TELEPORTATION("why-no-teleportation", {
        text("Du fragst dich, warum es auf dem Server keine Teleportation gibt?")
        appendSpace()
        append {
            variableValue("Hier")
            hoverEvent(buildText { spacer("Klicke, um zur Informationsseite zu gelangen.") })
            clickOpensUrl("https://server.castcrafter.de/faq#why-no-teleportation")
        }
        appendSpace()
        text("findest du die Antwort.")
    }),

    WHY_NO_VILLAGERS("why-no-villagers", {
        text("Du fragst dich, warum es auf dem Server keine Dorfbewohner gibt?")
        appendSpace()
        append {
            variableValue("Hier")
            hoverEvent(buildText { spacer("Klicke, um zur Informationsseite zu gelangen.") })
            clickOpensUrl("https://server.castcrafter.de/specials#no-villagers")
        }
        appendSpace()
        text("findest du die Antwort.")
    });

    val message = SurfComponentBuilder(message)
>>>>>>> version/1.21.11

    companion object {
        fun create(
            id: String,
            content: SurfComponentBuilder.() -> Unit,
            enabled: Boolean = true
        ): Faq =
            Faq(
                id,
                miniMessage().serialize(SurfComponentBuilder(content)),
                enabled
            )
    }
}
