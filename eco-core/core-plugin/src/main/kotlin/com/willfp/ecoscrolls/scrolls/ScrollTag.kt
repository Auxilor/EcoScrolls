package com.willfp.ecoscrolls.scrolls

import com.willfp.eco.core.items.tag.CustomItemTag
import com.willfp.ecoscrolls.plugin
import org.bukkit.inventory.ItemStack

object ScrollTag : CustomItemTag(plugin.createNamespacedKey("scroll")) {
    override fun matches(p0: ItemStack): Boolean {
        return p0.scroll != null
    }

    override fun getExampleItem(): ItemStack? {
        return Scrolls.values().randomOrNull()?.item
    }
}
