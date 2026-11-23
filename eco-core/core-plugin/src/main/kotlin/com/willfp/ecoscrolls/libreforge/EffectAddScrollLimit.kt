package com.willfp.ecoscrolls.libreforge

import com.willfp.ecoscrolls.plugin
import com.willfp.ecoscrolls.scrolls.UNLIMITED
import com.willfp.ecoscrolls.scrolls.scrollLimitBonus
import wiki.ecohub.config.ArgType
import wiki.ecohub.config.Config
import wiki.ecohub.config.arguments
import wiki.ecohub.config.section.getDoubleFromExpression
import wiki.ecohub.core.registry.NoCompileData
import wiki.ecohub.rules.effect.Effect
import wiki.ecohub.rules.trigger.TriggerData

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
