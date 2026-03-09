package com.willfp.ecoscrolls.commands

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.ecoscrolls.plugin
import org.bukkit.command.CommandSender

object CommandEcoScrolls : PluginCommand(
    plugin,
    "ecoscrolls",
    "ecoscrolls.command.ecoscrolls",
    false
) {
    init {
        this.addSubcommand(CommandReload)
            .addSubcommand(CommandGive)
            .addSubcommand(CommandInscribeDirect)
    }

    override fun onExecute(sender: CommandSender, args: List<String>) {
        sender.sendMessage(
            plugin.langYml.getMessage("invalid-command")
        )
    }

    override fun getAliases(): List<String> {
        return listOf(
            "scrolls"
        )
    }
}
