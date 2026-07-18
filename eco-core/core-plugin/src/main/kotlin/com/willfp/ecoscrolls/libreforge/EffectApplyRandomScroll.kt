package com.willfp.ecoscrolls.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoscrolls.scrolls.Scroll
import com.willfp.ecoscrolls.scrolls.ScrollTypes
import com.willfp.ecoscrolls.scrolls.Scrolls
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData

object EffectApplyRandomScroll : Effect<NoCompileData>("apply_random_scroll") {
    override val description = "Inscribes the found item with a randomly chosen scroll that it can be inscribed with."

    override val categories = setOf("inventory")

    override val additionalInfo = listOf(
        "Requires a trigger that provides an item, such as inscribe or try_inscribe."
    )

    override val isPermanent = false

    override val arguments = arguments {
        optional(
            "scrolls",
            description = "The ids of the scrolls to choose from. If empty, all registered scrolls are considered.",
            type = ArgType.STRING_LIST
        )
        optional(
            "type",
            description = "If specified, only scrolls of this scroll type are considered.",
            type = ArgType.STRING
        )
        optional(
            "allow_unsafe",
            description = "If true, scrolls that the item does not meet the inscription requirements for can still be chosen.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem ?: return false

        val allowUnsafe = config.getBool("allow_unsafe")

        val scrollList = config.getStrings("scrolls")
        var candidates: Collection<Scroll> = if (scrollList.isNotEmpty()) {
            scrollList.mapNotNull { Scrolls[it] }
        } else {
            Scrolls.values()
        }

        val typeId = config.getStringOrNull("type")
        if (typeId != null) {
            val type = ScrollTypes[typeId] ?: return false
            candidates = candidates.filter { it.type == type }
        }

        val applicable = candidates.filter {
            if (allowUnsafe) it.matchesTarget(item) else it.canInscribe(item)
        }

        val scroll = applicable.randomOrNull() ?: return false

        scroll.inscribe(item)

        return true
    }
}