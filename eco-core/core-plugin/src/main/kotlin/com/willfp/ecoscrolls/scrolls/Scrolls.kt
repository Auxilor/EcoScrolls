package com.willfp.ecoscrolls.scrolls

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.recipe.Recipes
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.RegistrableCategory

object Scrolls : RegistrableCategory<Scroll>("scroll", "scrolls") {
    override fun clear(plugin: LibreforgePlugin) {
        for (scroll in registry.values()) {
            scroll.itemRecipe?.let { Recipes.unregister(it) }
        }
        registry.clear()
    }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        registry.register(Scroll(id, config))
    }
}
