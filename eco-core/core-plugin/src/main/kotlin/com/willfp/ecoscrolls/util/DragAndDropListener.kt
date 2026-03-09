package com.willfp.ecoscrolls.util

import com.willfp.eco.core.items.isEcoEmpty
import com.willfp.ecoscrolls.plugin
import com.willfp.ecoscrolls.scrolls.scroll
import com.willfp.ecoscrolls.scrolls.useScroll
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

object DragAndDropListener : Listener {
    @EventHandler
    fun handle(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return

        if (player.gameMode == GameMode.CREATIVE) {
            return
        }

        val current = event.currentItem ?: return
        val cursor = event.cursor ?: return

        if (current.isEcoEmpty) {
            return
        }

        val scroll = cursor.scroll ?: return

        if (!scroll.isDragAndDropEnabled) {
            return
        }

        if (!scroll.canInscribe(current)) {
            return
        }

        val didInscribe = plugin.inscriptionHandler.tryInscribe(current, scroll, player)

        if (didInscribe) {
            cursor.useScroll()
            event.isCancelled = true
        }
    }
}
