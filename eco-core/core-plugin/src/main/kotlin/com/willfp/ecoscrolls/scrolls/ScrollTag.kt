package com.willfp.ecoscrolls.scrolls

import com.willfp.ecoscrolls.plugin
import org.bukkit.inventory.ItemStack
import wiki.ecohub.game.item.lookup.CustomItemTag

object ScrollTag : CustomItemTag(plugin.createNamespacedKey("scroll")) {
    override fun matches(p0: ItemStack): Boolean {
        return p0.scroll != null
    }

    override fun getExampleItem(): ItemStack? {
        return Scrolls.values().randomOrNull()?.item
    }
}
