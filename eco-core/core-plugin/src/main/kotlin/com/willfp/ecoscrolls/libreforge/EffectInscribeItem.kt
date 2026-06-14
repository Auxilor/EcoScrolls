package com.willfp.ecoscrolls.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoscrolls.scrolls.Scrolls
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData

object EffectInscribeItem : Effect<NoCompileData>("inscribe_item") {
    override val description = "Inscribes the found item with the specified scroll, increasing its level by one."

    override val categories = setOf("inventory")

    override val additionalInfo = listOf(
        "Requires a trigger that provides an item, such as inscribe or try_inscribe."
    )

    override val isPermanent = false

    override val arguments = arguments {
        require(
            "scroll",
            "You must specify the scroll!",
            description = "The id of the scroll to inscribe onto the item.",
            type = ArgType.STRING
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem ?: return false

        val scroll = Scrolls[config.getString("scroll")] ?: return false

        scroll.inscribe(item)

        return true
    }
}
