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
    private var loreOrder: List<String> = emptyList()

    fun reload() {
        loreOrder = plugin.configYml.getStrings("lore-order")
    }

    override fun display(itemStack: ItemStack, player: Player?, vararg args: Any) {
        val fis = itemStack.fast()

        fis.scroll?.displayScroll(fis, player)

        // fis.scrolls is backed by LinkedHashSet (insertion order); sortedWith is stable,
        // so scrolls within the same type group retain their inscription order.
        val sortedScrolls = if (loreOrder.isEmpty()) {
            fis.scrolls.toList()
        } else {
            val otherIndex = loreOrder.indexOf("other").let { if (it < 0) Int.MAX_VALUE else it }
            fis.scrolls.sortedWith(compareBy { scrollLevel ->
                val typeId = scrollLevel.scroll.type?.id
                if (typeId == null) {
                    otherIndex
                } else {
                    val idx = loreOrder.indexOf(typeId)
                    if (idx < 0) otherIndex else idx
                }
            })
        }

        for (scroll in sortedScrolls) {
            fis.lore = fis.lore + scroll.scroll.getLore(itemStack, player)
        }
    }
}
