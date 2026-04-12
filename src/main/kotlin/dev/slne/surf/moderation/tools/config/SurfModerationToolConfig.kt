package dev.slne.surf.moderation.tools.config

import dev.slne.surf.api.core.config.SpongeYmlConfigClass
import dev.slne.surf.moderation.tools.faq.Faq
import dev.slne.surf.moderation.tools.faq.StaticFaqs
import dev.slne.surf.moderation.tools.plugin
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class SurfModerationToolConfig(
    var faqCooldown: Long = 5_000,
    var faqs: MutableList<Faq> = StaticFaqs.entries.map { Faq.create(it.id, it.message) }.toMutableList()

) {
    companion object : SpongeYmlConfigClass<SurfModerationToolConfig>(
        SurfModerationToolConfig::class.java,
        plugin.dataPath,
        "config.yml"
    )
}

