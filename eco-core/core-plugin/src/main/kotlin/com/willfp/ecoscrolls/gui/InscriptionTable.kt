package com.willfp.ecoscrolls.gui

import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.fast.fast
import com.willfp.eco.core.gui.GUIComponent
import com.willfp.eco.core.gui.captiveSlot
import com.willfp.eco.core.gui.menu
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.menu.events.CaptiveItemChangeEvent
import com.willfp.eco.core.gui.onEvent
import com.willfp.eco.core.gui.onLeftClick
import com.willfp.eco.core.gui.slot
import com.willfp.eco.core.gui.slot.ConfigSlot
import com.willfp.eco.core.gui.slot.CustomSlot
import com.willfp.eco.core.gui.slot.FillerMask
import com.willfp.eco.core.gui.slot.MaskItems
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.modify
import com.willfp.eco.util.formatEco
import com.willfp.ecomponent.CaptiveItem
import com.willfp.ecomponent.addComponent
import com.willfp.ecomponent.menuStateVar
import com.willfp.ecoscrolls.plugin
import com.willfp.ecoscrolls.scrolls.Scroll
import com.willfp.ecoscrolls.scrolls.scroll
import com.willfp.ecoscrolls.scrolls.useScroll
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

lateinit var inscriptionTable: Menu
    private set

private enum class InscriptionStatus {
    ALLOW,
    DENY,
    EMPTY
}

private val Menu.status by menuStateVar(InscriptionStatus.EMPTY)

private val Menu.scroll by menuStateVar<Optional<Scroll>>()

private lateinit var capturedItem: CaptiveItem
private lateinit var capturedScrollItem: CaptiveItem

internal fun updateInscribeMenu() {
    capturedItem = CaptiveItem()
    capturedScrollItem = CaptiveItem()

    val violationContext = ViolationContext(plugin, "Inscription Table")

    val openEffects = Effects.compileChain(
        plugin.configYml.getSubsections("gui.open-effects"),
        violationContext.with("Open Effects")
    )

    val closeEffects = Effects.compileChain(
        plugin.configYml.getSubsections("gui.close-effects"),
        violationContext.with("Close Effects")
    )

    inscriptionTable = menu(plugin.configYml.getInt("gui.rows")) {
        allowChangingHeldItem()

        title = plugin.configYml.getFormattedString("gui.title")

        val indicatorPattern = plugin.configYml.getStrings("gui.indicator.pattern")
        for (i in 1..indicatorPattern.size) {
            val row = indicatorPattern[i - 1]
            for (j in 1..9) {
                if (row[j - 1] != '0') {
                    setSlot(i, j, IndicatorSlot)
                }
            }
        }

        setMask(
            FillerMask(
                MaskItems.fromItemNames(
                    plugin.configYml.getStrings("gui.mask.items")
                ),
                *plugin.configYml.getStrings("gui.mask.pattern").toTypedArray()
            )
        )

        addComponent(
            plugin.configYml.getInt("gui.inscribe-slot.row"),
            plugin.configYml.getInt("gui.inscribe-slot.column"),
            Inscriber,
        )

        addComponent(
            plugin.configYml.getInt("gui.scroll-slot.row"),
            plugin.configYml.getInt("gui.scroll-slot.column"),
            captiveSlot(),
            bindCaptive = capturedScrollItem
        )

        addComponent(
            plugin.configYml.getInt("gui.item-slot.row"),
            plugin.configYml.getInt("gui.item-slot.column"),
            captiveSlot(),
            bindCaptive = capturedItem
        )

        addComponent(
            plugin.configYml.getInt("gui.close.location.row"),
            plugin.configYml.getInt("gui.close.location.column"),
            CloseSlot
        )

        onEvent<CaptiveItemChangeEvent> { player, menu, _ ->
            menu.scroll[player] = Optional.empty()
            val item = capturedItem[player]
            val scrollItem = capturedScrollItem[player]

            if (item == null || scrollItem == null) {
                menu.status[player] = InscriptionStatus.EMPTY
            } else {
                val scroll = scrollItem.scroll
                if (scroll == null || !scroll.isInscriptionTableEnabled) {
                    menu.status[player] = InscriptionStatus.DENY
                } else if (scroll.canInscribe(item)) {
                    menu.scroll[player] = Optional.of(scroll)
                    menu.status[player] = InscriptionStatus.ALLOW
                } else {
                    menu.status[player] = InscriptionStatus.DENY
                }
            }
        }

        onOpen { player, _ ->
            openEffects?.trigger(player.toDispatcher(), TriggerData(player = player))
        }

        onClose { event, menu ->
            val player = event.player as Player

            closeEffects?.trigger(player.toDispatcher(), TriggerData(player = player))

            DropQueue(player)
                .addItems(menu.getCaptiveItems(player))
                .setLocation(player.eyeLocation)
                .forceTelekinesis()
                .push()
        }

        for (config in plugin.configYml.getSubsections("gui.custom-slots")) {
            setSlot(
                config.getInt("row"),
                config.getInt("column"),
                ConfigSlot(config)
            )
        }
    }
}

private object IndicatorSlot : CustomSlot() {
    private val allowed = Items.lookup(plugin.configYml.getString("gui.indicator.allow-item")).item
    private val denied = Items.lookup(plugin.configYml.getString("gui.indicator.deny-item")).item

    init {
        create()
    }

    private fun create() {
        init(slot { player, menu ->
            val status = menu.status[player]

            when (status) {
                InscriptionStatus.ALLOW -> allowed
                else -> denied
            }
        })
    }
}

private object CloseSlot : CustomSlot() {
    private val item = Items.lookup(plugin.configYml.getString("gui.close.item")).modify {
        setDisplayName(plugin.configYml.getFormattedString("gui.close.name"))
        addLoreLines(plugin.configYml.getFormattedStrings("gui.close.lore"))
    }

    init {
        create()
    }

    private fun create() {
        init(slot(item) {
            onLeftClick { player, _, _, _ ->
                player.closeInventory()
            }
        })
    }
}


private object Inscriber : GUIComponent {
    private val allowSlot = AllowSlot
    private val denySlot = DenySlot
    private val emptySlot = EmptySlot

    override fun getColumns() = 1
    override fun getRows() = 1

    override fun getSlotAt(row: Int, column: Int, player: Player, menu: Menu): Slot {
        val status = menu.status[player]

        return when (status) {
            InscriptionStatus.ALLOW -> allowSlot
            InscriptionStatus.DENY -> denySlot
            InscriptionStatus.EMPTY -> emptySlot
        }
    }
}

private abstract class MenuSlot(
    private val status: InscriptionStatus
) : CustomSlot() {
    private val item = Items.lookup(plugin.configYml.getString("gui.${status.name.lowercase()}.item"))
        .item

    init {
        create()
    }

    protected open fun onClick(player: Player, event: InventoryClickEvent, slot: Slot, menu: Menu) {
        // Override
    }

    private fun create() {
        init(slot(::getItem) {
            onLeftClick { player, event, slot, menu ->
                onClick(player, event, slot, menu)
            }
        })
    }

    private fun getItem(player: Player, menu: Menu): ItemStack {
        val name = plugin.configYml.getString("gui.${status.name.lowercase()}.name")
            .injectPlaceholders(player, menu)

        val lore = plugin.configYml.getStrings("gui.${status.name.lowercase()}.lore")
            .map { it.injectPlaceholders(player, menu) }

        return item.fast().apply {
            this.displayName = name
            this.lore = lore
        }.unwrap()
    }

    private fun String.injectPlaceholders(player: Player, menu: Menu): String {
        val scroll = menu.scroll[player]?.getOrNull()
        val price = scroll?.getInscriptionPriceDisplay(player, capturedItem[player])

        return this
            .replaceNullable("%price%", price)
            .replaceNullable("%scroll%", scroll?.name)
            .formatEco(player = player, formatPlaceholders = true)
    }

    private fun String.replaceNullable(find: String, replace: String?): String {
        return if (replace == null) {
            this
        } else {
            this.replace(find, replace)
        }
    }
}

private object AllowSlot : MenuSlot(InscriptionStatus.ALLOW) {
    override fun onClick(player: Player, event: InventoryClickEvent, slot: Slot, menu: Menu) {
        val scroll = menu.scroll[player]?.getOrNull() ?: return
        val item = capturedItem[player] ?: return

        val didInscribe = plugin.inscriptionHandler.tryInscribe(item, scroll, player)

        if (didInscribe) {
            val scrollItem = capturedScrollItem[player]
                ?: throw IllegalStateException("Scroll item is null")

            scrollItem.useScroll()
        }

        // Cheat to update the menu
        menu.callEvent(player, CaptiveItemChangeEvent(0, 0, null, null))
    }
}

private object DenySlot : MenuSlot(InscriptionStatus.DENY)
private object EmptySlot : MenuSlot(InscriptionStatus.EMPTY)
