package com.willfp.ecoscrolls.commands

import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.util.StringUtils
import com.willfp.eco.util.savedDisplayName
import com.willfp.ecoscrolls.gui.inscriptionTable
import com.willfp.ecoscrolls.plugin
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

object CommandOpen : Subcommand(
    plugin,
    "open",
    "ecoscrolls.command.open",
    false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        if (args.isEmpty()) {
            sender.sendMessage(plugin.langYml.getMessage("needs-player"))
            return
        }

        val target = Bukkit.getPlayer(args[0])

        if (target == null) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-player"))
            return
        }

        inscriptionTable.open(target)

        sender.sendMessage(
            plugin.langYml.getMessage("open-success", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                .replace("%player%", target.savedDisplayName)
        )
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        val completions = mutableListOf<String>()

        if (args.size == 1) {
            StringUtil.copyPartialMatches(
                args[0],
                Bukkit.getOnlinePlayers().map { it.name },
                completions
            )
        }

        return completions
    }
}
