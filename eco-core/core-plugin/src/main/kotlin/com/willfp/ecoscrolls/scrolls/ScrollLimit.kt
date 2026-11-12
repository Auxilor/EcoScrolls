package com.willfp.ecoscrolls.scrolls

import com.willfp.ecoscrolls.plugin
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import wiki.ecohub.game.item.fast

private val SCROLL_LIMIT_KEY = plugin.createNamespacedKey("scroll_limit")
private val SCROLL_LIMIT_BONUS_KEY = plugin.createNamespacedKey("scroll_limit_bonus")

const val UNLIMITED = Int.MAX_VALUE

var ItemStack.scrollLimitBase: Int?
    get() = this.fast().persistentDataContainer.scrollLimitBase
    set(value) {
        this.fast().persistentDataContainer.scrollLimitBase = value
    }

var PersistentDataContainer.scrollLimitBase: Int?
    get() = this.get(SCROLL_LIMIT_KEY, PersistentDataType.INTEGER)
    set(value) {
        if (value == null) {
            this.remove(SCROLL_LIMIT_KEY)
        } else {
            this.set(SCROLL_LIMIT_KEY, PersistentDataType.INTEGER, value)
        }
    }

var ItemStack.scrollLimitBonus: Int
    get() = this.fast().persistentDataContainer.get(SCROLL_LIMIT_BONUS_KEY, PersistentDataType.INTEGER) ?: 0
    set(value) {
        val container = this.fast().persistentDataContainer
        if (value == 0) {
            container.remove(SCROLL_LIMIT_BONUS_KEY)
        } else {
            container.set(SCROLL_LIMIT_BONUS_KEY, PersistentDataType.INTEGER, value)
        }
    }

val ItemStack.usedScrollLimit: Int
    get() = this.scrolls.sumOf { it.scroll.limitCost(it.level) }
