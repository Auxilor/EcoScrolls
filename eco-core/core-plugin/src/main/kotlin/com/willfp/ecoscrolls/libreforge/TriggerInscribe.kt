package com.willfp.ecoscrolls.libreforge

import com.willfp.ecoscrolls.scrolls.event.ScrollInscribeEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerInscribe : Trigger("inscribe") {
    override val description = "Fires when the player successfully inscribes an item with a scroll."

    override val categories = setOf("inventory")

    override val parameters = setOf(
        TriggerParameter.ITEM,
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.ITEM to "The item that was inscribed with the scroll."
    )

    @EventHandler
    fun handle(event: ScrollInscribeEvent) {
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
