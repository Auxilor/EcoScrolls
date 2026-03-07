package com.willfp.ecoscrolls.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.ecoscrolls.gui.inscriptionTable
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandInscribe(
    plugin: EcoPlugin
) : PluginCommand(
    plugin,
    "inscribe",
    "ecoscrolls.command.inscribe",
    true
) {
    override fun onExecute(player: CommandSender, args: List<String>) {
        player as Player

        inscriptionTable.open(player)
    }
}
