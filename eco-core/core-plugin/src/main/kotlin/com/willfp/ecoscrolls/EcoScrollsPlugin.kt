package com.willfp.ecoscrolls

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.display.DisplayModule
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.templates.DynamicPlaceholder
import com.willfp.ecoscrolls.commands.CommandEcoScrolls
import com.willfp.ecoscrolls.commands.CommandInscribe
import com.willfp.ecoscrolls.config.TargetsYml
import com.willfp.ecoscrolls.display.ScrollDisplay
import com.willfp.ecoscrolls.gui.updateInscribeMenu
import com.willfp.ecoscrolls.libreforge.ConditionHasScroll
import com.willfp.ecoscrolls.libreforge.EffectInscribeItem
import com.willfp.ecoscrolls.libreforge.FilterScroll
import com.willfp.ecoscrolls.libreforge.TriggerInscribe
import com.willfp.ecoscrolls.libreforge.TriggerTryInscribe
import com.willfp.ecoscrolls.scrolls.InscriptionHandler
import com.willfp.ecoscrolls.scrolls.ScrollLevel
import com.willfp.ecoscrolls.scrolls.ScrollTag
import com.willfp.ecoscrolls.scrolls.Scrolls
import com.willfp.ecoscrolls.target.ScrollFinder
import com.willfp.ecoscrolls.target.Targets
import com.willfp.ecoscrolls.util.AntiPlaceListener
import com.willfp.ecoscrolls.util.DiscoverRecipeListener
import com.willfp.ecoscrolls.util.DragAndDropListener
import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.ConfigCategory
import com.willfp.libreforge.registerHolderPlaceholderProvider
import com.willfp.libreforge.registerHolderProvider
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.event.Listener
import java.util.regex.Pattern

internal lateinit var plugin: EcoScrollsPlugin
    private set

class EcoScrollsPlugin : LibreforgePlugin() {
    val targetsYml: TargetsYml = TargetsYml(this)

    val inscriptionHandler = InscriptionHandler(this)

    init {
        plugin = this
    }

    override fun handleEnable() {
        Conditions.register(ConditionHasScroll)
        Effects.register(EffectInscribeItem)
        Filters.register(FilterScroll)
        Triggers.register(TriggerInscribe)
        Triggers.register(TriggerTryInscribe)

        Items.registerTag(ScrollTag)

        registerHolderProvider(ScrollFinder.toHolderProvider())

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
    }

    override fun handleReload() {
        updateInscribeMenu()
        Targets.update()
        inscriptionHandler.reload()
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
            DragAndDropListener,
            AntiPlaceListener
        )
    }

    override fun loadDisplayModules(): List<DisplayModule> {
        return listOf(
            ScrollDisplay
        )
    }
}
