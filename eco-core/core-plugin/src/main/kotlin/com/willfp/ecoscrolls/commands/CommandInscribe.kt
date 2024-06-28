package com.willfp.ecoscrolls.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.ecoscrolls.gui.inscriptionTable
import org.bukkit.Sound
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

        if (plugin.configYml.getBool("gui.open-sound.enabled")) {
            player.playSound(
                player.location,
                Sound.valueOf(plugin.configYml.getString("gui.open-sound.id").uppercase()),
                1f,
                plugin.configYml.getDouble("gui.open-sound.pitch").toFloat()
            )
        }

        inscriptionTable.open(player)
    }
}
