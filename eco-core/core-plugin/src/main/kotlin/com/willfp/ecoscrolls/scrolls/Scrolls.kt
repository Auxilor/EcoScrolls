package com.willfp.ecoscrolls.scrolls

import wiki.ecohub.config.Config
import wiki.ecohub.config.category.RegistrableCategory
import wiki.ecohub.core.plugin.EcoPlugin
import wiki.ecohub.modules.recipe.Recipes

object Scrolls : RegistrableCategory<Scroll>("scroll", "scrolls") {
    override fun clear(plugin: EcoPlugin) {
        for (scroll in registry.values()) {
            scroll.itemRecipe?.let { Recipes.unregister(it) }
        }
        registry.clear()
    }

    override fun acceptConfig(plugin: EcoPlugin, id: String, config: Config) {
        registry.register(Scroll(id, config))
    }
}
