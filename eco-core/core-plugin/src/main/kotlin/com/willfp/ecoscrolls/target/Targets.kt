package com.willfp.ecoscrolls.target

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.items.HashedItem
import com.willfp.eco.core.registry.Registry
import com.willfp.ecoscrolls.plugin
import org.bukkit.inventory.ItemStack
import java.util.concurrent.TimeUnit

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

private val targetsCache = Caffeine.newBuilder()
    .expireAfterAccess(5, TimeUnit.SECONDS)
    .build<HashedItem, List<Target>>()
