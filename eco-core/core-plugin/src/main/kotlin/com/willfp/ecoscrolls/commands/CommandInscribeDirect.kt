package com.willfp.ecoscrolls.commands

import com.willfp.ecoscrolls.scrolls.Scroll
import com.willfp.ecoscrolls.scrolls.Scrolls
import com.willfp.ecoscrolls.scrolls.getScrollLevel
import com.willfp.ecoscrolls.scrolls.scrolls
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import wiki.ecohub.core.command.CommandBuilder
import wiki.ecohub.core.command.CommandContext
import wiki.ecohub.game.entity.player.PlayerUtils
import wiki.ecohub.game.entity.runOwned
import wiki.ecohub.ui.text.StringUtils

private const val DEFAULT_MAX_LEVEL = 5

/**
 * A player inscribes their own held item; the console names the player first. Checks after the
 * region switch reply directly, as they may run off the command's thread.
 */
internal fun CommandBuilder<CommandSender>.inscribeDirectCommand() = subcommand(
    name = "inscribe",
    permission = "ecoscrolls.command.inscribedirect"
) {
    executes {
        val self = sender as? Player
        val target = self ?: argument(0, "invalid-player", "invalid-player") { Bukkit.getPlayer(it) }
        val offset = if (self == null) 1 else 0

        val scroll = args.getOrNull(offset)?.let { Scrolls[it.lowercase()] } ?: fail("invalid-scroll")
        val level = args.getOrNull(offset + 1)?.toIntOrNull() ?: 1

        if (level > scroll.maxLevel) {
            fail("denied-max-level")
        }

        target.runOwned { inscribe(target, scroll, level) }
    }

    completes {
        val rest = if (sender is Player) args else args.drop(1)

        when (rest.size) {
            1 -> Scrolls.values().map { it.id }
            2 -> (0..(Scrolls[rest[0].lowercase()]?.maxLevel ?: DEFAULT_MAX_LEVEL)).map { it.toString() }
            else -> emptyList()
        }
    }
}

private fun CommandContext<CommandSender>.inscribe(target: Player, scroll: Scroll, level: Int) {
    val item = target.inventory.itemInMainHand

    if (item.type.isAir) {
        reply("needs-item")
        return
    }

    val currentLevel = item.getScrollLevel(scroll)?.level ?: 0
    val others = item.scrolls.filter { it.scroll != scroll }.toSet()

    val inscribed = when {
        level <= 0 -> {
            item.scrolls = others
            true
        }

        level < currentLevel -> {
            item.scrolls = others + scroll.getLevel(level)
            true
        }

        else -> {
            inscribeLevels(item, scroll, level - currentLevel)
        }
    }

    if (inscribed) {
        sender.sendMessage(
            plugin.langYml.getMessage("inscribed-item", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                .replace(oldValue = "%scroll%", newValue = scroll.name)
                .replace(oldValue = "%player%", newValue = PlayerUtils.getSavedDisplayName(target))
        )
    }
}

private fun CommandContext<CommandSender>.inscribeLevels(item: ItemStack, scroll: Scroll, times: Int): Boolean {
    repeat(times) {
        val denialReason = scroll.getDenialReason(item)

        if (denialReason != null) {
            reply(denialReason.messageKey ?: "denied-other")
            return false
        }

        scroll.inscribe(item)
    }

    return true
}
