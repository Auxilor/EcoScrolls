package com.willfp.ecoscrolls.util

import com.willfp.eco.core.dragdrop.DragAndDropHandler
import com.willfp.eco.core.dragdrop.DragAndDropResult
import com.willfp.eco.core.items.isEcoEmpty
import com.willfp.ecoscrolls.plugin
import com.willfp.ecoscrolls.scrolls.InscriptionDenialReason
import com.willfp.ecoscrolls.scrolls.scroll
import com.willfp.ecoscrolls.scrolls.scrollUsesLeft
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

        if (cursor.scrollUsesLeft > 1) {
            cursor.useScroll()
            return DragAndDropResult.DENIED
        }

        // APPLIED makes eco consume one item from the cursor. Reset the uses before
        // consuming so that the next scroll in a stack starts with its full uses.
        cursor.scrollUsesLeft = scroll.maxUses
        return DragAndDropResult.APPLIED
    }
}
