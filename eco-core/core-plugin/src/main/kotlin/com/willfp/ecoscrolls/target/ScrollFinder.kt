package com.willfp.ecoscrolls.target

import com.willfp.ecoscrolls.scrolls.ScrollLevel
import com.willfp.ecoscrolls.scrolls.scrolls
import org.bukkit.inventory.ItemStack
import wiki.ecohub.rules.holder.slot.ItemHolderFinder
import wiki.ecohub.rules.holder.slot.SlotType

object ScrollFinder : ItemHolderFinder<ScrollLevel>() {
    override fun find(item: ItemStack): List<ScrollLevel> {
        return item.scrolls.toList()
    }

    override fun isValidInSlot(holder: ScrollLevel, slot: SlotType): Boolean {
        return holder.scroll.targets.map { it.slot }.any { it.isOrContains(slot) }
    }
}
