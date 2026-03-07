package com.willfp.ecoscrolls.scrolls

import com.willfp.eco.core.sound.PlayableSound
import com.willfp.ecoscrolls.EcoScrollsPlugin
import com.willfp.ecoscrolls.scrolls.event.ScrollInscribeEvent
import com.willfp.ecoscrolls.scrolls.event.ScrollTryInscribeEvent
import com.willfp.ecoscrolls.target.Targets.targets
import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.Optional
import kotlin.math.min

class InscriptionHandler(private val plugin: EcoScrollsPlugin) {
    private lateinit var applyEffects: Optional<Chain>
    private lateinit var denyEffects: Optional<Chain>

    private val globalScrollLimit = plugin.configYml.getInt("inscription.scroll-limit")
        .let { if (it <= 0) Int.MAX_VALUE else it }

    internal fun reload() {
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

        val didInscribe = scroll.inscribe(item, player)

        if (didInscribe) {
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
        }

        return didInscribe
    }

    fun getScrollLimit(item: ItemStack): Int {
        val targets = item.targets

        var highest = Int.MAX_VALUE

        for (target in targets) {
            val limit = target.scrollLimit ?: continue
            if (limit < highest) {
                highest = limit
            }
        }

        return min(highest, globalScrollLimit)
    }
}
