package com.willfp.ecoscrolls.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoscrolls.scrolls.Scrolls
import com.willfp.ecoscrolls.scrolls.getScrollLevel
import com.willfp.ecoscrolls.scrolls.scrolls
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData

object EffectUninscribeItem : Effect<NoCompileData>("uninscribe_item") {
    override val description = "Removes or decreases the level of the specified scroll on the found item."

    override val categories = setOf("inventory")

    override val additionalInfo = listOf(
        "Requires a trigger that provides an item, such as inscribe or try_inscribe.",
        "Does nothing if the item does not have the specified scroll inscribed."
    )

    override val isPermanent = false

    override val arguments = arguments {
        require(
            "scroll",
            "You must specify the scroll!",
            description = "The id of the scroll to remove or decrease on the item.",
            type = ArgType.STRING
        )
        optional(
            "type",
            description = "Whether to fully remove the scroll, or decrease its level by one.",
            type = ArgType.STRING,
            default = "remove",
            choices = listOf("remove", "decrease")
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem ?: return false

        val scroll = Scrolls[config.getString("scroll")] ?: return false

        val current = item.getScrollLevel(scroll) ?: return false

        val type = config.getStringOrNull("type") ?: "remove"

        if (type == "decrease") {
            val newLevel = current.level - 1
            if (newLevel <= 0) {
                item.scrolls = item.scrolls.filter { it.scroll != scroll }.toSet()
            } else {
                item.scrolls = item.scrolls.filter { it.scroll != scroll }.toSet() + scroll.getLevel(newLevel)
            }
        } else {
            item.scrolls = item.scrolls.filter { it.scroll != scroll }.toSet()
        }

        return true
    }
}