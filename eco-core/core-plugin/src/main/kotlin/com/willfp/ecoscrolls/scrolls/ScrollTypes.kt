package com.willfp.ecoscrolls.scrolls

import com.willfp.ecoscrolls.plugin
import wiki.ecohub.core.registry.Registry

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