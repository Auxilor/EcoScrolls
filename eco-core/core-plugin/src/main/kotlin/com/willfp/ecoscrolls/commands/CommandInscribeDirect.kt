package com.willfp.ecoscrolls.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.util.StringUtils
import com.willfp.eco.util.savedDisplayName
import com.willfp.ecoscrolls.plugin
import com.willfp.ecoscrolls.runOwned
import com.willfp.ecoscrolls.scrolls.Scroll
import com.willfp.ecoscrolls.scrolls.Scrolls
import com.willfp.ecoscrolls.scrolls.getScrollLevel
import com.willfp.ecoscrolls.scrolls.scrolls
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.StringUtil

object CommandInscribeDirect : Subcommand(
    plugin,
    "inscribe",
    "ecoscrolls.command.inscribedirect",
    false
) {
    override fun onExecute(sender: CommandSender, rawArgs: List<String>) {
        var args = rawArgs
        var player = sender as? Player

        if (sender !is Player) {
            player = notifyPlayerRequired(args.getOrNull(0), "invalid-player")
            args = rawArgs.subList(1, rawArgs.size)
        }

        val target = player ?: return

        val scroll = notifyNull(
            args.getOrNull(0)?.lowercase()?.let { Scrolls[it] },
            "invalid-scroll"
        )

        val level = args.getOrNull(1)?.toIntOrNull() ?: 1

        if (level > scroll.maxLevel) {
            sender.sendMessage(plugin.langYml.getMessage("denied-max-level"))
            return
        }

        target.runOwned { inscribe(sender, target, scroll, level) }
    }

    private fun inscribe(sender: CommandSender, target: Player, scroll: Scroll, level: Int) {
        val item = target.inventory.itemInMainHand

        if (item.type.isAir) {
            sender.sendMessage(plugin.langYml.getMessage("needs-item"))
            return
        }

        val currentLevel = item.getScrollLevel(scroll)?.level ?: 0

        val inscribed = when {
            level <= 0 -> {
                item.scrolls = item.scrolls.filter { it.scroll != scroll }.toSet()
                true
            }
            level < currentLevel -> {
                item.scrolls = item.scrolls.filter { it.scroll != scroll }.toSet() + scroll.getLevel(level)
                true
            }
            else -> {
                inscribeLevels(sender, item, scroll, level - currentLevel)
            }
        }

        if (inscribed) {
            sender.sendMessage(
                plugin.langYml.getMessage("inscribed-item", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                    .replace("%scroll%", scroll.name)
                    .replace("%player%", target.savedDisplayName)
            )
        }
    }

    private fun inscribeLevels(sender: CommandSender, item: ItemStack, scroll: Scroll, times: Int): Boolean {
        repeat(times) {
            val denialReason = scroll.getDenialReason(item)
            if (denialReason != null) {
                sender.sendMessage(plugin.langYml.getMessage(denialReason.messageKey ?: "denied-other"))
                return false
            }
            scroll.inscribe(item)
        }
        return true
    }

    override fun tabComplete(sender: CommandSender, rawArgs: List<String>): List<String> {
        val completions = mutableListOf<String>()

        var args = rawArgs

        if (sender !is Player) {
            args = rawArgs.subList(1, rawArgs.size)
        }

        if (args.size == 1) {
            StringUtil.copyPartialMatches(
                args[0],
                Scrolls.values().map { it.id },
                completions
            )
        }

        if (args.size == 2) {
            val scroll = Scrolls[args[0].lowercase()]

            val levels = if (scroll != null) {
                val maxLevel = scroll.maxLevel
                (0..maxLevel).toList()
            } else {
                (0..5).toList()
            }

            StringUtil.copyPartialMatches(
                args[1],
                levels.map { it.toString() },
                completions
            )
        }

        return completions
    }
}
