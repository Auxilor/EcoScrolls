package com.willfp.ecoscrolls

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.ecoscrolls.commands.CommandEcoScrolls
import com.willfp.ecoscrolls.commands.CommandInscribe
import com.willfp.ecoscrolls.config.TargetsYml
import com.willfp.ecoscrolls.config.TypesYml
import com.willfp.ecoscrolls.display.ScrollDisplay
import com.willfp.ecoscrolls.gui.updateInscribeMenu
import com.willfp.ecoscrolls.libreforge.ConditionHasScroll
import com.willfp.ecoscrolls.libreforge.ConditionHasScrollLimit
import com.willfp.ecoscrolls.libreforge.EffectAddScrollLimit
import com.willfp.ecoscrolls.libreforge.EffectApplyRandomScroll
import com.willfp.ecoscrolls.libreforge.EffectInscribeItem
import com.willfp.ecoscrolls.libreforge.EffectUninscribeItem
import com.willfp.ecoscrolls.libreforge.FilterScroll
import com.willfp.ecoscrolls.libreforge.TriggerInscribe
import com.willfp.ecoscrolls.libreforge.TriggerTryInscribe
import com.willfp.ecoscrolls.scrolls.InscriptionHandler
import com.willfp.ecoscrolls.scrolls.ScrollLevel
import com.willfp.ecoscrolls.scrolls.ScrollTag
import com.willfp.ecoscrolls.scrolls.ScrollTypes
import com.willfp.ecoscrolls.scrolls.Scrolls
import com.willfp.ecoscrolls.scrolls.UNLIMITED
import com.willfp.ecoscrolls.scrolls.usedScrollLimit
import com.willfp.ecoscrolls.target.ScrollFinder
import com.willfp.ecoscrolls.target.Targets
import com.willfp.ecoscrolls.util.AntiPlaceListener
import com.willfp.ecoscrolls.util.DiscoverRecipeListener
import com.willfp.ecoscrolls.util.DragAndDropListener
import com.willfp.ecoscrolls.util.ScrollLimitArgParser
import org.bukkit.event.Listener
import wiki.ecohub.config.category.NativeConfigCategory
import wiki.ecohub.core.plugin.EcoPlugin
import wiki.ecohub.core.stdlib.NamedValue
import wiki.ecohub.game.item.Items
import wiki.ecohub.modules.menus.drag.DragAndDropHandlers
import wiki.ecohub.provider.metrics.EcoMetricsChart
import wiki.ecohub.rules.condition.Conditions
import wiki.ecohub.rules.effect.Effects
import wiki.ecohub.rules.filter.Filters
import wiki.ecohub.rules.holder.registerHolderPlaceholderProvider
import wiki.ecohub.rules.holder.registerHolderProvider
import wiki.ecohub.rules.trigger.Triggers
import wiki.ecohub.ui.display.DisplayModule
import wiki.ecohub.ui.placeholder.PlaceholderContext
import wiki.ecohub.ui.placeholder.PlaceholderManager
import wiki.ecohub.ui.placeholder.type.DynamicPlaceholder
import java.util.regex.Pattern

internal lateinit var plugin: EcoScrollsPlugin
    private set

class EcoScrollsPlugin : LibreforgePlugin() {
    val targetsYml: TargetsYml = TargetsYml(this)
    val typesYml: TypesYml = TypesYml(this)

    val inscriptionHandler = InscriptionHandler(this)

    private val limitPlaceholderPattern = Pattern.compile("limit_(used|total|free)")

    init {
        plugin = this
    }

    override fun handleEnable() {
        Conditions.register(ConditionHasScroll)
        Conditions.register(ConditionHasScrollLimit)
        Effects.register(EffectAddScrollLimit)
        Effects.register(EffectInscribeItem)
        Effects.register(EffectUninscribeItem)
        Effects.register(EffectApplyRandomScroll)
        Filters.register(FilterScroll)
        Triggers.register(TriggerInscribe)
        Triggers.register(TriggerTryInscribe)

        Items.registerTag(ScrollTag)
        Items.registerArgParser(ScrollLimitArgParser)

        registerHolderProvider(ScrollFinder.toHolderProvider())

        DragAndDropHandlers.register(DragAndDropListener)

        registerHolderPlaceholderProvider<ScrollLevel> { it, _ ->
            listOf(
                NamedValue("level", it.level),
            )
        }

        PlaceholderManager.registerPlaceholder(
            object : DynamicPlaceholder(plugin, Pattern.compile("scroll_([a-z0-9_]+):([a-zA-Z0-9_]+)")) {
                override fun getValue(args: String, context: PlaceholderContext): String? {
                    val matcher = pattern.matcher(args)

                    if (!matcher.matches()) {
                        return null
                    }

                    // Get the scroll and identifier from the matched groups
                    val scroll = matcher.group(1)
                    val identifier = matcher.group(2)

                    // Return empty instead of null for optional placeholders, like hot potato books
                    val scrollInstance = Scrolls[scroll] ?: return ""

                    return scrollInstance.getPlaceholder(identifier, context) ?: ""
                }
            }
        )

        PlaceholderManager.registerPlaceholder(
            object : DynamicPlaceholder(plugin, limitPlaceholderPattern) {
                override fun getValue(args: String, context: PlaceholderContext): String {
                    val item = context.itemStack ?: return ""
                    val limit = inscriptionHandler.getScrollLimit(item)

                    return when {
                        args == "limit_used" -> item.usedScrollLimit.toString()
                        limit == UNLIMITED -> ""
                        args == "limit_total" -> limit.toString()
                        else -> (limit - item.usedScrollLimit).coerceAtLeast(0).toString()
                    }
                }
            }
        )
    }

    override fun handleReload() {
        updateInscribeMenu()
        ScrollTypes.update()
        Targets.update()
        inscriptionHandler.reload()
        ScrollDisplay.reload()
    }

    override fun handleDisable() {
        DragAndDropHandlers.unregisterAll("ecoscrolls")
    }

    override fun loadConfigCategories(): List<ConfigCategory> {
        return listOf(
            Scrolls
        )
    }

    override fun loadPluginCommands(): List<PluginCommand> {
        return listOf(
            CommandEcoScrolls,
            CommandInscribe
        )
    }

    override fun loadListeners(): List<Listener> {
        return listOf(
            DiscoverRecipeListener,
            AntiPlaceListener
        )
    }

    override fun loadDisplayModules(): List<DisplayModule> {
        return listOf(
            ScrollDisplay
        )
    }

    override fun getCustomCharts() = listOf(
        EcoMetricsChart.SingleLine("total_scrolls") { Scrolls.values().size },
        EcoMetricsChart.SingleLine("total_scroll_types") { ScrollTypes.values().size },
        EcoMetricsChart.SingleLine("total_targets") { Targets.values().size },
        EcoMetricsChart.SingleLine("inscription_scroll_limit") { configYml.getInt("inscription.scroll-limit") },
        EcoMetricsChart.SimplePie("discover_recipes") {
            if (configYml.getBool("discover-recipes")) "enabled" else "disabled"
        }
    )
}
