package dev.slne.surf.moderation.tools.faq

import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
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
