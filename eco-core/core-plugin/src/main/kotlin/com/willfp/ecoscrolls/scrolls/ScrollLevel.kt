package com.willfp.ecoscrolls.scrolls

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.Holder
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.effects.EffectList

class ScrollLevel(
    plugin: EcoPlugin,
    val scroll: Scroll,
    val level: Int,
    override val effects: EffectList,
    override val conditions: ConditionList
) : Holder {
    override val id = plugin.createNamespacedKey("${scroll.id}_$level")

    override fun equals(other: Any?): Boolean {
        if (other !is ScrollLevel) {
            return false
        }

        return this.id == other.id
    }

    override fun toString(): String {
        return "ScrollLevel(scroll=$scroll, level=$level)"
    }

    override fun hashCode(): Int {
        return this.id.hashCode()
    }
}
