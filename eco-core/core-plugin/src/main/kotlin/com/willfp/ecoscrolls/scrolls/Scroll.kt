package com.willfp.ecoscrolls.scrolls

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.display.Display
import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.core.fast.fast
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.eco.core.placeholder.PlaceholderInjectable
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.eco.core.placeholder.templates.DynamicInjectablePlaceholder
import com.willfp.eco.core.price.ConfiguredPrice
import com.willfp.eco.core.recipe.Recipes
import com.willfp.eco.core.recipe.recipes.CraftingRecipe
import com.willfp.eco.core.registry.KRegistrable
import com.willfp.eco.core.sound.PlayableSound
import com.willfp.eco.util.evaluateExpressionOrNull
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.toNumeral
import com.willfp.ecoscrolls.plugin
import com.willfp.ecoscrolls.target.Targets
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.Objects
import java.util.regex.Pattern

class Scroll(
    override val id: String,
    val config: Config
) : KRegistrable {
    private val context = ViolationContext(plugin, "scroll $id")

    private val levels = mutableMapOf<Int, ScrollLevel>()

    private val effects = Effects.compile(
        config.getSubsections("effects"),
        context.with("effects")
    )

    private val conditions = Conditions.compile(
        config.getSubsections("conditions"),
        context.with("conditions")
    )

    val name = config.getFormattedString("name")

    val maxLevel = config.getInt("max-level")

    val maxUses = config.getInt("max-uses")

    private val itemName = config.getString("item.name")
    private val itemLore = config.getStrings("item.lore")

    private val _item = run {
        val base = Items.lookup(config.getString("item.item")).item
        val fis = base.fast()
        fis.scroll = this
        fis.displayName = itemName.formatEco()
        fis.lore = itemLore.formatEco().map { Display.PREFIX + it } + fis.lore
        fis.scrollUsesLeft = maxUses
        fis.unwrap()
    }

    val item: ItemStack
        get() = _item.clone()

    val customItem = CustomItem(
        plugin.createNamespacedKey("scroll_$id"),
        { it.scroll == this },
        _item
    ).apply { register() }

    val itemRecipe: CraftingRecipe? = config.getBool("item.craftable")
        .takeIf { it }
        ?.let {
            val recipeStrings = config.getStrings("item.recipe")
            if (recipeStrings.isEmpty()) return@let null

            Recipes.createAndRegisterRecipe(
                plugin,
                id,
                item,
                recipeStrings,
                config.getStringOrNull("item.recipe-permission"),
                config.getBool("item.shapeless")
            )
        }

    val targets = config.getStrings("targets")
        .mapNotNull { Targets[it] }

    private val conflicts = config.getStrings("conflicts")

    private val requirements = config.getSubsections("requirements")
        .map {
            ScrollRequirement(
                it.getString("scroll"),
                it.getIntOrNull("level")
            )
        }

    private val removeRequirements = config.getBool("remove-requirements")

    private val inscriptionConditions = Conditions.compile(
        config.getSubsections("inscription.conditions"),
        context.with("inscription conditions")
    )

    private val inscriptionEffects = Effects.compileChain(
        config.getSubsections("inscription.effects"),
        context.with("inscription effects")
    )

    private val inscriptionPrice = ConfiguredPrice.createOrFree(
        config.getSubsection("inscription.price"),
    )

    private val priceLevelMultiplier = config.getStringOrNull("inscription.price-level-multiplier")

    val isDragAndDropEnabled = config.getBool("inscription.drag-and-drop")

    val isInscriptionTableEnabled = config.getBool("inscription.inscription-table")

    private val lore = config.getStrings("lore")

    private val scrollPlaceholderContext = object : PlaceholderInjectable {
        private val levelPlaceholder = object : DynamicInjectablePlaceholder(Pattern.compile("level")) {
            override fun getValue(p0: String, p1: PlaceholderContext): String? {
                return p1.itemStack?.getScrollLevel(this@Scroll)?.level?.toString()
            }
        }

        private val levelNumeralPlaceholder = object : DynamicInjectablePlaceholder(Pattern.compile("level_numeral")) {
            override fun getValue(p0: String, p1: PlaceholderContext): String? {
                return p1.itemStack?.getScrollLevel(this@Scroll)?.level?.toNumeral()
            }
        }

        private val usesLeftPlaceholder = object : DynamicInjectablePlaceholder(Pattern.compile("uses_left")) {
            override fun getValue(p0: String, p1: PlaceholderContext): String? {
                val item = p1.itemStack ?: return null
                return item.scrollUsesLeft.toString()
            }
        }

        private val usesPlaceholder = object : DynamicInjectablePlaceholder(Pattern.compile("uses")) {
            override fun getValue(p0: String, p1: PlaceholderContext): String? {
                val item = p1.itemStack ?: return null
                val scroll = item.scroll ?: return null
                return (scroll.maxUses - item.scrollUsesLeft).toString()
            }
        }

        private val maxUsesPlaceholder = object : DynamicInjectablePlaceholder(Pattern.compile("max_uses")) {
            override fun getValue(p0: String, p1: PlaceholderContext): String? {
                val item = p1.itemStack ?: return null
                val scroll = item.scroll ?: return null
                return scroll.maxUses.toString()
            }
        }

        override fun getPlaceholderInjections(): List<InjectablePlaceholder> {
            return listOf(
                levelPlaceholder,
                levelNumeralPlaceholder,
                usesLeftPlaceholder,
                usesPlaceholder,
                maxUsesPlaceholder
            )
        }

        override fun addInjectablePlaceholder(p0: MutableIterable<InjectablePlaceholder>) {
            //
        }

        override fun clearInjectedPlaceholders() {
            //
        }
    }

    fun getLevel(level: Int): ScrollLevel {
        return levels.getOrPut(level) {
            createLevel(level)
        }
    }

    private fun createLevel(level: Int): ScrollLevel {
        return ScrollLevel(this, level, effects, conditions)
    }

    fun canInscribe(itemStack: ItemStack): Boolean {
        if (targets.none { it.matches(itemStack) }) {
            return false
        }

        val currentScrolls = itemStack.scrolls

        if (currentScrolls.size >= plugin.inscriptionHandler.getScrollLimit(itemStack)) {
            return false
        }

        if (currentScrolls.any { it.scroll.conflictsWith(this) }) {
            return false
        }

        if (requirements.any { !it.isPresent(itemStack) }) {
            return false
        }

        val level = itemStack.getScrollLevel(this)?.level ?: 0

        return level < maxLevel
    }

    fun inscribe(itemStack: ItemStack, player: Player): Boolean {
        if (!canInscribe(itemStack)) {
            return false
        }

        if (!inscriptionConditions.areMetAndTrigger(
                TriggerData(
                    player = player,
                    item = itemStack
                ).dispatch(player.toDispatcher())
            )
        ) {
            return false
        }

        if (!inscriptionPrice.canAfford(player, getPriceMultiplier(itemStack))) {
            return false
        }

        inscriptionPrice.pay(player, getPriceMultiplier(itemStack))

        inscribe(itemStack)

        inscriptionEffects?.trigger(
            player.toDispatcher(),
            TriggerData(
                player = player,
                item = itemStack,
                value = itemStack.getScrollLevel(this)?.level?.toDouble() ?: 1.0
            )
        )

        PlayableSound.create(plugin.configYml.getSubsection("sounds.inscribe"))?.playTo(player)

        return true
    }

    fun inscribe(itemStack: ItemStack) {
        val current = itemStack.scrolls.find { it.scroll == this }?.level ?: 0
        val next = current + 1

        val level = this.getLevel(next)

        var scrolls = itemStack.scrolls.filter { it.scroll != this }.toSet() + level

        if (removeRequirements) {
            scrolls = scrolls.filter { scroll ->
                !requirements.any { it.scrollId == scroll.scroll.id }
            }.toSet()
        }

        itemStack.scrolls = scrolls
    }

    fun getLore(itemStack: ItemStack, player: Player?): List<String> {
        return lore.formatEco(
            placeholderContext(
                player = player,
                item = itemStack,
                injectable = scrollPlaceholderContext
            )
        ).map { Display.PREFIX + it }
    }

    fun displayScroll(fis: FastItemStack, player: Player?) {
        val context = placeholderContext(player = player, item = fis.unwrap())
            .withInjectableContext(scrollPlaceholderContext)

        fis.displayName = itemName.formatEco(context)
        fis.lore = itemLore.formatEco(context)
            .map { Display.PREFIX + it } + fis.lore
    }

    fun getPlaceholder(identifier: String, context: PlaceholderContext): String? {
        if ((context.itemStack?.getScrollLevel(this)?.level ?: 0) < 1) {
            return null
        }

        val expression = config.getString("placeholders.$identifier")
        return expression.formatEco(context.withInjectableContext(scrollPlaceholderContext))
    }

    fun conflictsWith(other: Scroll): Boolean {
        return this.conflicts.contains(other.id) ||
                other.conflicts.contains(this.id)
    }

    fun getPriceMultiplier(itemStack: ItemStack?): Double {
        if (itemStack == null || priceLevelMultiplier == null) {
            return 1.0
        }

        val level = itemStack.getScrollLevel(this)?.level ?: 0

        return evaluateExpressionOrNull(
            // Less elegant than actually using placeholders, but it avoids a bunch
            // of extra code.
            priceLevelMultiplier.replace("%level%", level.toString()),
            placeholderContext(
                item = itemStack
            )
        ) ?: 1.0
    }

    fun getInscriptionPriceDisplay(player: Player, itemStack: ItemStack?): String {
        return inscriptionPrice.getDisplay(player, getPriceMultiplier(itemStack))
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Scroll) {
            return false
        }

        return this.id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(this.id)
    }
}
