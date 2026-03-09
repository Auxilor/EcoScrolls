package com.willfp.ecoscrolls.commands

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.ecoscrolls.gui.inscriptionTable
import com.willfp.ecoscrolls.plugin
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object CommandInscribe : PluginCommand(
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
