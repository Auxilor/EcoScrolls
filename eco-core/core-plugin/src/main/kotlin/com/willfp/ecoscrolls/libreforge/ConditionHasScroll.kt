package com.willfp.ecoscrolls.libreforge

import com.willfp.ecoscrolls.scrolls.ScrollLevel
import wiki.ecohub.config.ArgType
import wiki.ecohub.config.Config
import wiki.ecohub.config.arguments
import wiki.ecohub.core.registry.NoCompileData
import wiki.ecohub.rules.condition.Condition
import wiki.ecohub.rules.holder.Dispatcher
import wiki.ecohub.rules.holder.ProvidedHolder
import wiki.ecohub.rules.holder.holders

object ConditionHasScroll : Condition<NoCompileData>("has_scroll") {
    override val description = "Passes when the dispatcher has the specified scroll inscribed at or above the given level."

    override val categories = setOf("inventory")

    override val arguments = arguments {
        require(
            "scroll",
            "You must specify the scroll!",
            description = "The id of the scroll that must be inscribed.",
            type = ArgType.STRING
        )
        optional(
            "level",
            description = "The minimum level the scroll must be inscribed at.",
            type = ArgType.INT,
            default = "1"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val scroll = config.getString("scroll")
        val level = config.getIntOrNull("level") ?: 1

        return dispatcher.holders.mapNotNull { it.holder as? ScrollLevel }
            .any {
                it.scroll.id == scroll && it.level >= level
            }
    }
}
