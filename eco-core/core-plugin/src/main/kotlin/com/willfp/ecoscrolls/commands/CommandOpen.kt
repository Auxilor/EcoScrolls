package com.willfp.ecoscrolls.commands

import com.willfp.ecoscrolls.gui.inscriptionTable
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import wiki.ecohub.core.command.CommandBuilder
import wiki.ecohub.game.entity.player.PlayerUtils
import wiki.ecohub.game.entity.runOwned
import wiki.ecohub.ui.text.StringUtils

internal fun CommandBuilder<CommandSender>.openCommand() = subcommand(
    name = "open",
    permission = "ecoscrolls.command.open"
) {
    executes {
        val target = Bukkit.getPlayer(argument(0, "needs-player")) ?: fail("invalid-player")

        target.runOwned {
            inscriptionTable.open(target)

            sender.sendMessage(
                plugin.langYml.getMessage("open-success", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                    .replace(oldValue = "%player%", newValue = PlayerUtils.getSavedDisplayName(target))
            )
        }
    }

    completes {
        if (args.size == 1) playerNames() else emptyList()
    }
}
