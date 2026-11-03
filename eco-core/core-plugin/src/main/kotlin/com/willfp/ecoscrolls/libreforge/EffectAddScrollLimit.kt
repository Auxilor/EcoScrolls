package com.willfp.ecoscrolls.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoscrolls.plugin
import com.willfp.ecoscrolls.scrolls.UNLIMITED
import com.willfp.ecoscrolls.scrolls.scrollLimitBonus
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData

object EffectAddScrollLimit : Effect<NoCompileData>("add_scroll_limit") {
    override val description = "Adds to the scroll limit of the found item. A negative amount removes from it."

    override val categories = setOf("inventory")

    override val additionalInfo = listOf(
        "The scroll limit of the item never goes below 0. Items with no scroll limit are not changed.",
        "Scrolls already on the item stay if the limit goes below the amount used."
    )

    override val isPermanent = false

    override val arguments = arguments {
        require(
            name = "amount",
            message = "You must specify the amount!",
            description = "The amount of scroll limit to add. Use a negative number to remove limit.",
            type = ArgType.EXPRESSION,
            example = "1"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem ?: return false
        val base = plugin.inscriptionHandler.getBaseScrollLimit(item)

        if (base != UNLIMITED) {
            item.scrollLimitBonus = config.getDoubleFromExpression("amount", data)
                .coerceIn(minimumValue = Int.MIN_VALUE.toDouble(), maximumValue = Int.MAX_VALUE.toDouble())
                .toLong()
                .plus(item.scrollLimitBonus)
                .coerceIn(minimumValue = -base.toLong(), maximumValue = UNLIMITED.toLong())
                .toInt()
        }

        return base != UNLIMITED
    }
}
