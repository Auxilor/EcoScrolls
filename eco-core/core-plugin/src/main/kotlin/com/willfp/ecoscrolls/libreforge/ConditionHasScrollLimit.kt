package com.willfp.ecoscrolls.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoscrolls.plugin
import com.willfp.ecoscrolls.scrolls.UNLIMITED
import com.willfp.ecoscrolls.scrolls.usedScrollLimit
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.getProvider
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

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

        return limit == UNLIMITED ||
                limit - item.usedScrollLimit >= config.getDoubleFromExpression("amount", dispatcher.get<Player>())
    }
}
