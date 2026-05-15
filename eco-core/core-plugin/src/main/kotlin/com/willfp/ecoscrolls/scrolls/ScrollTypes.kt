package com.willfp.ecoscrolls.scrolls

import com.willfp.eco.core.registry.Registry
import com.willfp.ecoscrolls.plugin

object ScrollTypes : Registry<ScrollType>() {
    init {
        update()
    }

    internal fun update() {
        for (type in values()) {
            remove(type)
        }
        for (config in plugin.typesYml.getSubsections("types")) {
            register(ScrollType(config))
        }
    }
}