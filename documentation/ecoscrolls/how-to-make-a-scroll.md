---
title: How to make a Scroll
sidebar_position: 1
---

## How to add scrolls
Each scroll is its own config file, placed in the `/scrolls/` folder, and you can add or remove them as you please. There's an example config called `_example.yml` to help you out!

The ID of the Scroll is the file name. This is what you use in commands and placeholders.
ID's must be lowercase letters, numbers, and underscores only.

## Example Scroll Config

```yaml
name: "&6Example Scroll"
lore:
  - ""
  - "&7This item has been inscribed with"
  - "&6Example Scroll"

max-level: 1
max-uses: 1 

targets:
  - sword
conflicts: [ ]

type: combat

requirements:
  - scroll: my_requirement_scroll
    level: 2
remove-requirements: false

placeholders:
  bonus: "%level% * 2"

effects:
  - id: send_message
    args:
      message: "&6You have used the Example Scroll!"
    triggers:
      - alt_click

conditions: [ ]

item:
  item: paper glint
  name: "&6&lExample Scroll"
  lore:
    - "&7This is an example scroll."
    - "&7It does nothing."
  craftable: false
  recipe-permission: []
  shapeless: false
  recipe: [ ]

inscription:
  inscription-table: true
  drag-and-drop: true
  price:
    value: 100
    type: coins
    display: "&e%value% coins"

  price-level-multiplier: "1 + %level% * 0.5"
  
  conditions: [ ]
  
  effects: [ ]
```

## Understanding all the sections

### The Scroll Info Section

```yaml
name: "&6Example Scroll" # The name of the scroll
lore: # The lore added to items when inscribed with the scroll
  - ""
  - "&7This item has been inscribed with"
  - "&6Example Scroll"

max-level: 1 # The max level of the scroll
max-uses: 1 # The amount of times the scroll can be used
```

### The Type Section:
```yaml
# (Optional) Assign this scroll to a type defined in types.yml.
# Types group scrolls into categories and enforce a per-item limit on how many
# different scrolls of that type can be inscribed on one item.
# Leave out this field if the scroll should have no type restriction.
type: combat
```

:::tip
See [Scroll Types](https://plugins.auxilor.io/ecoscrolls/scroll-types) for how to create and configure types.
:::

### The Requirements and Conflicts Section:
```yaml
targets: # The items that the scroll can be applied to, see targets.yml
  - sword
conflicts: [ ] # The conflicts that the scroll has with other scrolls

# (Optional) The type of the scroll. Types are defined in types.yml and limit
# how many different scrolls of the same type can be inscribed on a single item.
# See: Scroll Types
type: combat

# The scroll(s) that must be applied to the item before this scroll can be applied
requirements:
  - scroll: my_requirement_scroll # The ID of scroll to require
    level: 2 # The level required (optional)
remove-requirements: false # If inscribing this scroll should remove the required scrolls
 ```

### The Placeholders Section:
```yaml
# Item placeholders for dynamic lore in plugins like EcoItems
# The placeholder is %ecoscrolls_scroll_<scroll>:<placeholder>%, e.g.
# %ecoscrolls_scroll_example:bonus%
placeholders:
  bonus: "%level% * 2"
```

### The Effects Section:
:::dangerEffects Section

The effects section is the core functionality of the scroll. You can configure effects, conditions, filters, mutators and triggers in this section to run whilst the scroll is applied and active.

Check out [Configuring an Effect](https://plugins.auxilor.io/effects/configuring-an-effect) to understand how to configure this section correctly.

For more advanced users or setups, you can configure chains in this section to string together different effects under one trigger. Check out [Configuring an Effect Chain](https://plugins.auxilor.io/effects/configuring-a-chain) for more info.

:::
These are the effects that the scroll provides when applied and in use.
```yaml
# Read https://plugins.auxilor.io/effects/configuring-an-effect
# The effects for the scroll to give
effects:
  - id: send_message
    args:
      message: "&6You have used the Example Scroll!"
    triggers:
      - alt_click

# Read https://plugins.auxilor.io/effects/configuring-a-condition
# The conditions for the scroll to work
conditions: [ ]
```

### The Scroll Item Section:
```yaml
# Options for the physical scroll item
item:
  item: paper glint
  # Name and lore can use %uses%, %max_uses%, and %uses_left% placeholders
  name: "&6&lExample Scroll"
  lore:
    - "&7This is an example scroll."
    - "&7It does nothing."
  craftable: false
  recipe: [ ]
```

:::tip

We support shaped and shapeless recipes. Check out [Recipes](https://plugins.auxilor.io/the-item-lookup-system/recipes) for more info on how to configure these.

:::

### The Inscription Section:
:::dangerEffects Section

The inscription effects section is the core functionality for applying a scroll to an item. You can configure effects, conditions, filters, and mutators in this section to run when the scroll is applied.

Check out [Configuring an Effect](https://plugins.auxilor.io/effects/configuring-an-effect) to understand how to configure this section correctly.

For more advanced users or setups, you can configure chains in this section to string together different effects under one trigger. Check out [Configuring an Effect Chain](https://plugins.auxilor.io/effects/configuring-a-chain) for more info.

:::
```yaml
# Options for inscribing items with the scroll
inscription:
  inscription-table: true # If the scroll can be applied to items via the inscription table
  drag-and-drop: true # If the scroll can be applied to items via drag-and-drop

  # Read https://plugins.auxilor.io/all-plugins/prices
  # The price to inscribe the item
  price:
    value: 100
    type: coins
    display: "&e%value% coins"

  # The formula to multiply the price depending on the level.
  # The %level% placeholder is the *current* level of the scroll
  price-level-multiplier: "1 + %level% * 0.5"

  # The conditions required to inscribe the item
  # not-met-effects will run if someone tries to inscribe the item without meeting the conditions
  conditions: [ ]

  # The effects that will be run when the item is inscribed
  # If your scroll works by modifying the item (e.g. adding enchantments, changing durability),
  # then put those effects here.
  effects:
    - id: send_message
      args:
        message: "&6You have inscribed the item with Example Scroll!"
```
## Internal Placeholders

| Placeholder   | Value                                            |
| ------------- | ------------------------------------------------ |
| `%uses%`      | The amount of times the scroll has been used     |
| `%max_uses%`  | The maximum amount of times a scroll can be used |
| `%uses_left%` | The amount of uses left on the scroll            |

<hr/>

## Default configs
The default configs can be found [here](https://github.com/Auxilor/EcoScrolls/tree/master/eco-core/core-plugin/src/main/resources/scrolls). <br/>
You can find additional user-created configs on [lrcdb](https://lrcdb.auxilor.io/).