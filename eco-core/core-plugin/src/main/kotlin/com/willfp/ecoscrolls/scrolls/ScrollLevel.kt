package com.willfp.ecoscrolls.scrolls

import com.willfp.ecoscrolls.plugin
import wiki.ecohub.rules.condition.ConditionList
import wiki.ecohub.rules.effect.EffectList
import wiki.ecohub.rules.holder.Holder

class ScrollLevel(
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
