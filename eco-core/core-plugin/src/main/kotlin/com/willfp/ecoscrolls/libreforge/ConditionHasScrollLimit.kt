package com.willfp.ecoscrolls.libreforge

import com.willfp.ecoscrolls.plugin
import com.willfp.ecoscrolls.scrolls.UNLIMITED
import com.willfp.ecoscrolls.scrolls.usedScrollLimit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import wiki.ecohub.config.ArgType
import wiki.ecohub.config.Config
import wiki.ecohub.config.arguments
import wiki.ecohub.core.registry.NoCompileData
import wiki.ecohub.rules.condition.Condition
import wiki.ecohub.rules.holder.Dispatcher
import wiki.ecohub.rules.holder.ProvidedHolder
import wiki.ecohub.rules.holder.get
import wiki.ecohub.rules.holder.getProvider

object ConditionHasScrollLimit : Condition<NoCompileData>("has_scroll_limit") {
    override val description = "Passes when the item has at least the specified amount of free scroll limit."

    override val categories = setOf("inventory")

    override val additionalInfo = listOf(
        "Items with no scroll limit always pass.",
        "Needs a holder that provides an item, such as scroll inscription conditions."
    )

    override val arguments = arguments {
        require(
            name = "amount",
            message = "You must specify the amount!",
            description = "The minimum free scroll limit the item must have.",
            type = ArgType.EXPRESSION,
            example = "1"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val item = holder.getProvider<ItemStack>() ?: return false
        val limit = plugin.inscriptionHandler.getScrollLimit(item)

        val freeLimit = (limit - item.usedScrollLimit).coerceAtLeast(0)

        return limit == UNLIMITED ||
                freeLimit >= config.getDoubleFromExpression("amount", dispatcher.get<Player>())
    }
}
