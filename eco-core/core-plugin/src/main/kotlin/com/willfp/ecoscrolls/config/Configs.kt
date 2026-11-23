package com.willfp.ecoscrolls.config

import com.willfp.ecoscrolls.EcoScrollsPlugin
import wiki.ecohub.config.ConfigType
import wiki.ecohub.config.file.BaseConfig

class TargetsYml(plugin: EcoScrollsPlugin): BaseConfig("targets", plugin, true, ConfigType.YAML)

class TypesYml(plugin: EcoScrollsPlugin): BaseConfig("types", plugin, true, ConfigType.YAML)
