package com.willfp.ecoscrolls.libreforge

import com.willfp.ecoscrolls.scrolls.event.ScrollEvent
import wiki.ecohub.config.ArgType
import wiki.ecohub.config.Config
import wiki.ecohub.core.registry.NoCompileData
import wiki.ecohub.rules.filter.Filter
import wiki.ecohub.rules.trigger.TriggerData

object FilterScroll : Filter<NoCompileData, Collection<String>>("scroll") {
    override val description = "Matches when the scroll involved in the triggering scroll event is one of the given scroll ids."

    override val categories = setOf("inventory")

    override val valueType = ArgType.STRING_LIST

    override val additionalInfo = listOf(
        "Passes automatically when the trigger event is not a scroll-related event."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? ScrollEvent ?: return true

        return value.any { id ->
            id.equals(event.scroll.id, ignoreCase = true)
        }
    }
}
