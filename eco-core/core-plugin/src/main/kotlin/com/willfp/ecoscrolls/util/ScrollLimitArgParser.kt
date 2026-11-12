package com.willfp.ecoscrolls.util

import com.willfp.ecoscrolls.scrolls.scrollLimitBase
import org.bukkit.inventory.ItemStack
import wiki.ecohub.game.item.lookup.LookupArgParser
import java.util.function.Predicate

object ScrollLimitArgParser : LookupArgParser {
    override fun parseArguments(
        args: Array<out String>,
        item: ItemStack
    ): Predicate<ItemStack>? {
        val limit = args
            .filter { arg -> arg.substringBefore(':').equals("scroll_limit", ignoreCase = true) }
            .firstNotNullOfOrNull { arg -> arg.substringAfter(':').toIntOrNull()?.takeIf { it >= 0 } }
            ?: return null

        item.editMeta { it.persistentDataContainer.scrollLimitBase = limit }

        return Predicate { true }
    }

    override fun serializeBack(item: ItemStack): String? =
        item.itemMeta?.persistentDataContainer?.scrollLimitBase?.let { "scroll_limit:$it" }
}
