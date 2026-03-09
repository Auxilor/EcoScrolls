package com.willfp.ecoscrolls.display

import com.willfp.eco.core.display.DisplayModule
import com.willfp.eco.core.display.DisplayPriority
import com.willfp.eco.core.fast.fast
import com.willfp.ecoscrolls.plugin
import com.willfp.ecoscrolls.scrolls.scroll
import com.willfp.ecoscrolls.scrolls.scrolls
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object ScrollDisplay : DisplayModule(plugin, DisplayPriority.HIGHEST) {
    override fun display(itemStack: ItemStack, player: Player?, vararg args: Any) {
        val fis = itemStack.fast()

        fis.scroll?.displayScroll(fis, player)

        for (scroll in fis.scrolls) {
            fis.lore = fis.lore + scroll.scroll.getLore(itemStack, player)
        }
    }
}
