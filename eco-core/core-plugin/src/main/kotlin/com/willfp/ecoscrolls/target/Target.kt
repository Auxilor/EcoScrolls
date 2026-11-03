package com.willfp.ecoscrolls.target

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.eco.core.registry.Registrable
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoscrolls.plugin
import com.willfp.libreforge.slot.SlotType
import com.willfp.libreforge.slot.SlotTypes
import com.willfp.libreforge.slot.impl.SlotTypeAny
import java.util.Objects
import org.bukkit.inventory.ItemStack

interface Target : Registrable {
    val id: String
    val displayName: String
    val slot: SlotType
    val items: List<TestableItem>
    val scrollLimit: Int?

    /** The scroll limit this target gives [itemStack], or null for none. */
    fun scrollLimitFor(itemStack: ItemStack): Int? = scrollLimit

    fun matches(itemStack: ItemStack): Boolean {
        for (item in items) {
            if (item.matches(itemStack)) {
                return true
            }
        }
        return false
    }

    override fun getID(): String {
        return this.id
    }
}

class ConfiguredTarget(
    config: Config
) : Target {
    override val id = config.getString("id")
    override val displayName = config.getFormattedString("display-name")

    override val slot = SlotTypes[config.getString("slot")] ?:
    throw IllegalArgumentException("Invalid slot type: ${config.getString("slot")}, options are ${SlotTypes.values().map { it.id }}")

    override val items = config.getStrings("items")
        .map { Items.lookup(it) }
        .filterNot { it is EmptyTestableItem }

    override val scrollLimit = config.getIntOrNull("scroll-limit")

    private val scrollLimitExpression = config.getStringOrNull("scroll-limit")

    override fun scrollLimitFor(itemStack: ItemStack): Int? = scrollLimit
        ?: scrollLimitExpression?.let {
            NumberUtils.evaluateExpression(it, PlaceholderContext(null, itemStack, null, emptyList())).toInt()
        }

    override fun equals(other: Any?): Boolean {
        if (other !is Target) {
            return false
        }

        return this.id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(this.id)
    }
}

internal object AllTarget : Target {
    override val id = "all"
    override val displayName = plugin.langYml.getFormattedString("all")
    override val slot = SlotTypeAny

    @Volatile
    override var items = emptyList<TestableItem>()
        private set
    override val scrollLimit = null

    fun updateItems() {
        items = Targets.values()
            .filterNot { it == this }
            .flatMap { it.items }
    }

    override fun equals(other: Any?): Boolean {
        return other is AllTarget
    }
}