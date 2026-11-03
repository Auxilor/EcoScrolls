package com.willfp.ecoscrolls.scrolls

import com.willfp.libreforge.conditions.ConditionBlock

internal class InscriptionAttempt(
    val inscribed: Boolean,
    val notMetConditions: List<ConditionBlock<*>> = emptyList()
) {
    val hasConditionFeedback: Boolean
        get() = notMetConditions.any { !it.notMetEffects.isNullOrEmpty() }
}
