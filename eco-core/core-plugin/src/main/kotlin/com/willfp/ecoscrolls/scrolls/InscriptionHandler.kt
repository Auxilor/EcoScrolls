package com.willfp.ecoscrolls.scrolls

import com.willfp.ecoscrolls.EcoScrollsPlugin
import com.willfp.ecoscrolls.scrolls.event.ScrollInscribeEvent
import com.willfp.ecoscrolls.scrolls.event.ScrollTryInscribeEvent
import com.willfp.ecoscrolls.target.Targets.targets
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import wiki.ecohub.config.ViolationContext
import wiki.ecohub.core.stdlib.NamedValue
import wiki.ecohub.game.entity.toDispatcher
import wiki.ecohub.game.sound.PlayableSound
import wiki.ecohub.rules.chain.Chain
import wiki.ecohub.rules.effect.Effects
import wiki.ecohub.rules.trigger.TriggerData
import java.util.Optional
import kotlin.math.min

class InscriptionHandler(private val plugin: EcoScrollsPlugin) {
    @Volatile
    private lateinit var applyEffects: Optional<Chain>

    @Volatile
    private lateinit var denyEffects: Optional<Chain>

    @Volatile
    private var globalScrollLimit = Int.MAX_VALUE

    internal fun reload() {
        globalScrollLimit = plugin.configYml.getInt("inscription.scroll-limit")
            .let { if (it <= 0) Int.MAX_VALUE else it }

        val context = ViolationContext(plugin, "Inscriptions")

        applyEffects = Optional.ofNullable(
            Effects.compileChain(
                plugin.configYml.getSubsections("inscription.apply-effects"),
                context.with("Apply Effects")
            )
        )

        denyEffects = Optional.ofNullable(
            Effects.compileChain(
                plugin.configYml.getSubsections("inscription.deny-effects"),
                context.with("Deny Effects")
            )
        )
    }

    private fun inscriptionTrigger(item: ItemStack, scroll: Scroll, player: Player) =
        TriggerData(
            player = player,
            item = item,
            text = scroll.name,
            value = item.getScrollLevel(scroll)?.level?.toDouble() ?: 1.0
        ).dispatch(player.toDispatcher())
            .apply {
                addPlaceholder(
                    NamedValue(
                        "scroll",
                        scroll.name
                    )
                )
                addPlaceholder(
                    NamedValue(
                        "scroll_id",
                        scroll.id
                    )
                )
                addPlaceholder(
                    NamedValue(
                        "level",
                        item.getScrollLevel(scroll)?.level?.toString() ?: "1"
                    )
                )
            }

    fun tryInscribe(item: ItemStack, scroll: Scroll, player: Player): Boolean {
        val tryEvent = ScrollTryInscribeEvent(player, scroll, item)
        plugin.server.pluginManager.callEvent(tryEvent)

        if (tryEvent.isCancelled) {
            return false
        }

        val attempt = scroll.attemptInscribe(item, player)

        if (attempt.inscribed) {
            applyEffects.ifPresent {
                it.trigger(inscriptionTrigger(item, scroll, player))
            }

            val event = ScrollInscribeEvent(player, scroll, item)
            plugin.server.pluginManager.callEvent(event)
        } else {
            denyEffects.ifPresent {
                it.trigger(inscriptionTrigger(item, scroll, player))
            }
            PlayableSound.create(plugin.configYml.getSubsection("sounds.inscribe-fail"))?.playTo(player)

            val denialReason = scroll.getDenialReason(item)
            val messageKey = if (denialReason != null) {
                denialReason.messageKey
            } else if (!scroll.canAfford(item, player)) {
                "denied-cannot-afford"
            } else if (attempt.hasConditionFeedback) {
                null
            } else {
                "denied-conditions"
            }
            if (messageKey != null) {
                player.sendMessage(plugin.langYml.getMessage(messageKey))
            }
        }

        return attempt.inscribed
    }

    fun getBaseScrollLimit(item: ItemStack): Int = item.scrollLimitBase
        ?: min(item.targets.mapNotNull { it.scrollLimitFor(item) }.minOrNull() ?: UNLIMITED, globalScrollLimit)

    fun getScrollLimit(item: ItemStack): Int {
        val base = getBaseScrollLimit(item)

        return if (base == UNLIMITED) {
            UNLIMITED
        } else {
            (base.toLong() + item.scrollLimitBonus)
                .coerceIn(minimumValue = 0L, maximumValue = UNLIMITED.toLong())
                .toInt()
        }
    }
}
