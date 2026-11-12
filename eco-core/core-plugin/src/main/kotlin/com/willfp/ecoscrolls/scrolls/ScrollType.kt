package com.willfp.ecoscrolls.scrolls

import wiki.ecohub.config.Config
import wiki.ecohub.core.registry.KRegistrable
import java.util.Objects

class ScrollType(config: Config) : KRegistrable {
    override val id = config.getString("id")
    val displayName = config.getFormattedString("display-name")
    val limit = config.getInt("limit")

    override fun equals(other: Any?): Boolean {
        if (other !is ScrollType) return false
        return this.id == other.id
    }

    override fun hashCode(): Int = Objects.hash(id)
}
