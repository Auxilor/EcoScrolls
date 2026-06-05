package com.willfp.ecoscrolls.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoscrolls.scrolls.Scroll
import com.willfp.ecoscrolls.scrolls.ScrollTypes
import com.willfp.ecoscrolls.scrolls.Scrolls
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData

object EffectApplyRandomScroll : Effect<NoCompileData>("apply_random_scroll") {
    override val isPermanent = false

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