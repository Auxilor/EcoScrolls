package com.willfp.ecoscrolls.scrolls

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.KRegistrable
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
