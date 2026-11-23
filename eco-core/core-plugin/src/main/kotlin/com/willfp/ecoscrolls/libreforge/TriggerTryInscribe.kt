package com.willfp.ecoscrolls.libreforge

import com.willfp.ecoscrolls.scrolls.event.ScrollTryInscribeEvent
import org.bukkit.event.EventHandler
import wiki.ecohub.game.entity.toDispatcher
import wiki.ecohub.rules.trigger.Trigger
import wiki.ecohub.rules.trigger.TriggerData
import wiki.ecohub.rules.trigger.TriggerParameter

object TriggerTryInscribe : Trigger("try_inscribe") {
    override val description = "Fires when the player attempts to inscribe an item with a scroll, before the inscription is applied."

    override val categories = setOf("inventory")

    override val additionalInfo = listOf(
        "The inscription can still be cancelled or fail after this trigger fires, such as if the item does not meet the requirements."
    )

    override val parameters = setOf(
        TriggerParameter.ITEM,
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.ITEM to "The item being inscribed with the scroll."
    )

    @EventHandler
    fun handle(event: ScrollTryInscribeEvent) {
        this.dispatch(
            event.player.toDispatcher(),
            TriggerData(
                player = event.player,
                item = event.itemStack,
                event = event
            )
        )
    }
}
