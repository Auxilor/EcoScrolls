package com.willfp.ecoscrolls.config

import com.willfp.eco.core.config.BaseConfig
import com.willfp.eco.core.config.ConfigType
import com.willfp.ecoscrolls.EcoScrollsPlugin

class TargetsYml(plugin: EcoScrollsPlugin): BaseConfig("targets", plugin, true, ConfigType.YAML)

class TypesYml(plugin: EcoScrollsPlugin): BaseConfig("types", plugin, true, ConfigType.YAML)
