package com.willfp.ecoscrolls.display

import com.willfp.ecoscrolls.plugin
import com.willfp.ecoscrolls.scrolls.scroll
import com.willfp.ecoscrolls.scrolls.scrolls
import wiki.ecohub.game.item.fast
import wiki.ecohub.ui.display.DisplayContext
import wiki.ecohub.ui.display.DisplayModule
import wiki.ecohub.ui.display.DisplayPriority

object ScrollDisplay : DisplayModule(plugin, DisplayPriority.HIGHEST) {
    @Volatile
    private var loreOrder: List<String> = emptyList()

    fun reload() {
        loreOrder = plugin.configYml.getStrings("lore-order")
    }

    override fun display(context: DisplayContext) {
        val itemStack = context.getItemStack()
        val fis = itemStack.fast()

        fis.scroll?.displayScroll(context)

        val loreOrder = loreOrder

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
            context.getLore().append(scroll.scroll.getLoreComponents(itemStack, context.getPlayer()))
        }
    }
}
