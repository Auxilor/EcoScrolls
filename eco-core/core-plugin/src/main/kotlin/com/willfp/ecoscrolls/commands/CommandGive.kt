package com.willfp.ecoscrolls.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.util.StringUtils
import com.willfp.ecoscrolls.plugin
import com.willfp.ecoscrolls.scrolls.Scrolls
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

object CommandGive : Subcommand(
    plugin,
    "give",
    "ecoscrolls.command.give",
    false
) {
    private val numbers = listOf(
        "1",
        "2",
        "3",
        "4",
        "5",
        "10",
        "32",
        "64"
    )

    override fun onExecute(sender: CommandSender, args: MutableList<String>) {
        if (args.isEmpty()) {
            sender.sendMessage(plugin.langYml.getMessage("needs-player"))
            return
        }

        if (args.size == 1) {
            sender.sendMessage(plugin.langYml.getMessage("needs-scroll"))
            return
        }

        val amount = if (args.size > 2) args[2].toIntOrNull() ?: 1 else 1

        val receiver = Bukkit.getPlayer(args[0])

        if (receiver == null) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-player"))
            return
        }

        val scroll = Scrolls[args[1]]

        if (scroll == null) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-scroll"))
            return
        }

        val itemStack = scroll.item.apply {
            this.amount = amount
        }

        DropQueue(receiver)
            .addItem(itemStack)
            .forceTelekinesis()
            .push()

        val message = plugin.langYml.getMessage("give-success", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
            .replace("%scroll%", scroll.name)
            .replace("%player%", receiver.name)

        sender.sendMessage(message)
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        val completions = mutableListOf<String>()

        if (args.size == 1) {
            StringUtil.copyPartialMatches(
                args[0],
                Bukkit.getOnlinePlayers().map { it.name },
                completions
            )
            return completions
        }

        if (args.size == 2) {
            StringUtil.copyPartialMatches(
                args[1],
                Scrolls.values().map { it.id },
                completions
            )
            completions.sort()
            return completions
        }

        if (args.size == 3) {
            StringUtil.copyPartialMatches(args[2], numbers, completions)
            completions.sortWith { s1, s2 ->
                val t1 = s1.toInt()
                val t2 = s2.toInt()
                t1 - t2
            }
            return completions
        }

        return emptyList()
    }
}
