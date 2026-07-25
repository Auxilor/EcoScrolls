package com.willfp.ecoscrolls.util

import com.willfp.eco.core.dragdrop.DragAndDropHandler
import com.willfp.eco.core.dragdrop.DragAndDropResult
import com.willfp.eco.core.items.isEcoEmpty
import com.willfp.ecoscrolls.plugin
import com.willfp.ecoscrolls.scrolls.InscriptionDenialReason
import com.willfp.ecoscrolls.scrolls.scroll
import com.willfp.ecoscrolls.scrolls.useScroll
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object DragAndDropListener : DragAndDropHandler {
    override val id = "ecoscrolls:inscribe"

    override fun matches(cursor: ItemStack, current: ItemStack): Boolean {
        if (current.isEcoEmpty) return false
        val scroll = cursor.scroll ?: return false
        if (!scroll.isDragAndDropEnabled) return false
        return scroll.getDenialReason(current) != InscriptionDenialReason.OTHER
    }

    override fun apply(player: Player, cursor: ItemStack, current: ItemStack): DragAndDropResult {
        val scroll = cursor.scroll ?: return DragAndDropResult.DENIED
        val didInscribe = plugin.inscriptionHandler.tryInscribe(current, scroll, player)

        if (!didInscribe) return DragAndDropResult.DENIED

        cursor.useScroll()
        return DragAndDropResult.APPLIED
    }
}
