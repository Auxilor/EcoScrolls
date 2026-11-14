package com.willfp.ecoscrolls.commands

import com.willfp.ecoscrolls.EcoScrollsPlugin
import com.willfp.ecoscrolls.gui.inscriptionTable
import wiki.ecohub.core.command.playerCommand

internal fun EcoScrollsPlugin.inscribeCommand() = playerCommand(
    name = "inscribe",
    permission = "ecoscrolls.command.inscribe"
) {
    executes {
        inscriptionTable.open(sender)
    }
}
