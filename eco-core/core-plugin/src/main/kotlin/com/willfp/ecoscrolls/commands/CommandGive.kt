package com.willfp.ecoscrolls.commands

import com.willfp.ecoscrolls.scrolls.Scrolls
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import wiki.ecohub.core.command.CommandBuilder
import wiki.ecohub.game.drop.DropQueue
import wiki.ecohub.game.entity.runOwned
import wiki.ecohub.ui.text.StringUtils

private val amountCompletions = listOf("1", "2", "3", "4", "5", "10", "32", "64")

private const val AMOUNT_ARGS = 3

internal fun CommandBuilder<CommandSender>.giveCommand() = subcommand(
    name = "give",
    permission = "ecoscrolls.command.give"
) {
    executes {
        val receiverName = argument(0, "needs-player")
        val scrollId = argument(1, "needs-scroll")
        val amount = args.getOrNull(2)?.toIntOrNull() ?: 1

        if (amount <= 0) {
            fail("invalid-amount")
        }

        val receiver = Bukkit.getPlayer(receiverName) ?: fail("invalid-player")
        val scroll = Scrolls[scrollId] ?: fail("invalid-scroll")
        val itemStack = scroll.item.apply { this.amount = amount }

        receiver.runOwned {
            DropQueue(receiver)
                .addItem(itemStack)
                .forceTelekinesis()
                .push()

            sender.sendMessage(
                plugin.langYml.getMessage("give-success", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                    .replace(oldValue = "%scroll%", newValue = scroll.name)
                    .replace(oldValue = "%player%", newValue = receiver.name)
            )
        }
    }

    completes {
        when (args.size) {
            1 -> playerNames()
            2 -> Scrolls.values().map { it.id }
            AMOUNT_ARGS -> amountCompletions
            else -> emptyList()
        }
    }
}
