package com.willfp.ecoscrolls.scrolls

import com.willfp.eco.core.data.newPersistentDataContainer
import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.core.fast.fast
import com.willfp.ecoscrolls.plugin
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

private val SCROLL_KEY = plugin.createNamespacedKey("scroll")
private val SCROLL_USES_LEFT_KEY = plugin.createNamespacedKey("scroll_uses_left")
private val SCROLLS_KEY = plugin.createNamespacedKey("scrolls")
private val SCROLL_ID_KEY = plugin.createNamespacedKey("scroll")
private val SCROLL_LEVEL_KEY = plugin.createNamespacedKey("level")

var ItemStack.scroll: Scroll?
    get() = this.fast().scroll
    set(value) {
        this.fast().scroll = value
    }

var FastItemStack.scroll: Scroll?
    get() = this.persistentDataContainer.scroll
    set(value) {
        this.persistentDataContainer.scroll = value
    }

var PersistentDataContainer.scroll: Scroll?
    get() {
        val id = this.get(SCROLL_KEY, PersistentDataType.STRING) ?: return null
        return Scrolls[id]
    }
    set(value) {
        if (value == null) {
            this.remove(SCROLL_KEY)
            return
        }

        this.set(SCROLL_KEY, PersistentDataType.STRING, value.id)
    }

var ItemStack.scrollUsesLeft: Int
    get() = this.fast().scrollUsesLeft
    set(value) {
        this.fast().scrollUsesLeft = value
    }

var FastItemStack.scrollUsesLeft: Int
    get() = this.persistentDataContainer.scrollUsesLeft
    set(value) {
        this.persistentDataContainer.scrollUsesLeft = value
    }

var PersistentDataContainer.scrollUsesLeft: Int
    get() {
        return this.get(SCROLL_USES_LEFT_KEY, PersistentDataType.INTEGER) ?: 0
    }
    set(value) {
        if (value == 0) {
            this.remove(SCROLL_USES_LEFT_KEY)
            return
        }

        this.set(SCROLL_USES_LEFT_KEY, PersistentDataType.INTEGER, value)
    }

fun ItemStack.useScroll() {
    if (this.scroll == null) {
        return
    }

    this.scrollUsesLeft--

    if (this.scrollUsesLeft <= 0) {
        this.amount--
    }
}

var ItemStack.scrolls: Set<ScrollLevel>
    get() = this.fast().scrolls
    set(value) {
        this.fast().scrolls = value
    }

var FastItemStack.scrolls: Set<ScrollLevel>
    get() = this.persistentDataContainer.scrolls
    set(value) {
        this.persistentDataContainer.scrolls = value
    }

var PersistentDataContainer.scrolls: Set<ScrollLevel>
    get() {
        if (!this.has(SCROLLS_KEY, PersistentDataType.TAG_CONTAINER_ARRAY)) {
            return emptySet()
        }

        val baseTag = this.get(SCROLLS_KEY, PersistentDataType.TAG_CONTAINER_ARRAY) ?: return emptySet()

        val scrolls = mutableSetOf<ScrollLevel>()

        for (tag in baseTag) {
            val scroll = Scrolls[tag.get(SCROLL_ID_KEY, PersistentDataType.STRING)] ?: continue
            val level = tag.get(SCROLL_LEVEL_KEY, PersistentDataType.INTEGER) ?: continue

            scrolls += scroll.getLevel(level)
        }

        return scrolls
    }
    set(value) {
        // Scrolls that are not loaded can't be in value, so keep their tags as they are.
        val tags = this.get(SCROLLS_KEY, PersistentDataType.TAG_CONTAINER_ARRAY)
            ?.filter { Scrolls[it.get(SCROLL_ID_KEY, PersistentDataType.STRING)] == null }
            ?.toMutableList()
            ?: mutableListOf()

        for (scroll in value) {
            val scrollTag = newPersistentDataContainer()

            scrollTag.set(SCROLL_ID_KEY, PersistentDataType.STRING, scroll.scroll.id)
            scrollTag.set(SCROLL_LEVEL_KEY, PersistentDataType.INTEGER, scroll.level)

            tags += scrollTag
        }

        if (tags.isEmpty()) {
            this.remove(SCROLLS_KEY)
            return
        }

        this.set(SCROLLS_KEY, PersistentDataType.TAG_CONTAINER_ARRAY, tags.toTypedArray())
    }

fun ItemStack.getScrollLevel(scroll: Scroll): ScrollLevel? {
    return this.scrolls.find { it.scroll == scroll }
}

fun FastItemStack.getScrollLevel(scroll: Scroll): ScrollLevel? {
    return this.scrolls.find { it.scroll == scroll }
}
