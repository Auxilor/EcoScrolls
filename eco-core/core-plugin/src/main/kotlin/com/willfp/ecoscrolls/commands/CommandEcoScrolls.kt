package com.willfp.ecoscrolls.commands

import com.willfp.ecoscrolls.EcoScrollsPlugin
import wiki.ecohub.core.command.command

internal fun EcoScrollsPlugin.ecoScrollsCommand() = command(
    name = "ecoscrolls",
    permission = "ecoscrolls.command.ecoscrolls"
) {
    aliases("scrolls")

    reloadCommand()
    giveCommand()
    inscribeDirectCommand()
    openCommand()
}
