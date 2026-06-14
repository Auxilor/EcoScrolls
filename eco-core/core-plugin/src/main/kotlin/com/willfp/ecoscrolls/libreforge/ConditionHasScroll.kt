package com.willfp.ecoscrolls.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoscrolls.scrolls.ScrollLevel
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.getHoldersOfType

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

        return dispatcher.getHoldersOfType<ScrollLevel>()
            .any {
                it.scroll.id == scroll && it.level >= level
            }
    }
}
