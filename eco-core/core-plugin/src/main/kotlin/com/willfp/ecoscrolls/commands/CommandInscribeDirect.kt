package com.willfp.ecoscrolls.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.util.StringUtils
import com.willfp.eco.util.savedDisplayName
import com.willfp.ecoscrolls.plugin
import com.willfp.ecoscrolls.scrolls.Scrolls
import com.willfp.ecoscrolls.scrolls.getScrollLevel
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

object CommandInscribeDirect : Subcommand(
    plugin,
    "inscribe",
    "ecoscrolls.command.incscribedirect",
    false
) {
    override fun onExecute(sender: CommandSender, rawArgs: List<String>) {
        var args = rawArgs
        var player = sender as? Player

        if (sender !is Player) {
            player = notifyPlayerRequired(args.getOrNull(0), "invalid-player")
            args = rawArgs.subList(1, rawArgs.size)
        }

        if (player == null) {
            return
        }

        val scroll = notifyNull(
            args.getOrNull(0)?.lowercase()?.let { Scrolls[it] },
            "invalid-scroll"
        )

        val item = player.inventory.itemInMainHand

        val level = args.getOrNull(1)?.toIntOrNull() ?: 1
        val currentLevel = item.getScrollLevel(scroll)?.level ?: 0
        val levelsToAdd = level - currentLevel

        repeat(levelsToAdd) {
            scroll.inscribe(item)
        }

        sender.sendMessage(
            plugin.langYml.getMessage("inscribed-item", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                .replace("%scroll%", scroll.name)
                .replace("%player%", player.savedDisplayName)
        )
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
