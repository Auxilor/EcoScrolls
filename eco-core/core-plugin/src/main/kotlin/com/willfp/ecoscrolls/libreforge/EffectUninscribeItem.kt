package com.willfp.ecoscrolls.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoscrolls.scrolls.Scrolls
import com.willfp.ecoscrolls.scrolls.getScrollLevel
import com.willfp.ecoscrolls.scrolls.scrolls
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData

object EffectUninscribeItem : Effect<NoCompileData>("uninscribe_item") {
    override val isPermanent = false

    override val arguments = arguments {
        require("scroll", "You must specify the scroll!")
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