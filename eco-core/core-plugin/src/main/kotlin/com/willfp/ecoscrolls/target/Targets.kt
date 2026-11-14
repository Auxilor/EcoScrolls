package com.willfp.ecoscrolls.target

import com.willfp.ecoscrolls.plugin
import org.bukkit.inventory.ItemStack
import wiki.ecohub.core.registry.Registry
import wiki.ecohub.data.cache.EcoCache
import wiki.ecohub.game.item.HashedItem
import java.time.Duration

object Targets : Registry<Target>() {
    init {
        register(AllTarget)
        update()
    }

    private fun getForItem(item: ItemStack): List<Target> {
        return values()
            .filter { !it.id.equals("all", ignoreCase = true) }
            .filter { it.matches(item) }
    }

    val ItemStack.targets: List<Target>
        get() = targetsCache.get(HashedItem.of(this)) {
            getForItem(this)
        }

    internal fun update() {
        for (target in values()) {
            if (target is AllTarget) {
                continue
            }
            remove(target)
        }

        for (config in plugin.targetsYml.getSubsections("targets")) {
            register(ConfiguredTarget(config))
        }

        AllTarget.updateItems()
    }
}

private val targetsCache = EcoCache.builder<HashedItem, List<Target>>()
    .expireAfterAccess(Duration.ofSeconds(5))
    .build()
