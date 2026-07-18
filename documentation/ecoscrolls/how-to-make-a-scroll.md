---
title: "How to Make a Scroll"
sidebar_position: 1
---

A **scroll** is a single config file that grants an item new **effects** when **inscribed** onto it. Each scroll lives in its own `.yml` file under the `scrolls/` folder, and the file name is its ID. This page walks you through building one from scratch, breaks down every part of the config, and points you at the shared systems it builds on.

## Quick start

1. Open `/plugins/EcoScrolls/scrolls/` on your server.
2. Copy `_example.yml` and rename it to your scroll's ID, e.g. `my_scroll.yml`.
3. Edit the `name`, `targets`, and `effects` so the scroll does what you want.
4. Run `/ecoscrolls reload` to load the new scroll.
5. Give yourself a copy with `/ecoscrolls give <you> my_scroll`, inscribe it onto a matching item, then **equip the item and trigger the effect** to confirm it works.

:::tip
`_example.yml` is included as a reference and is **never loaded**, so copy or rename it to make a real scroll. You can also organise scrolls into subfolders inside `scrolls/`, and they'll still load.
:::

## Naming and IDs

The file name, without the `.yml`, is the scroll's ID. That ID is what you pass to commands, placeholders, and the [Item Lookup System](https://plugins.auxilor.io/the-item-lookup-system) when referencing the scroll item. So `phantom_step.yml` has the ID `phantom_step`.

:::warning ID rules
IDs may only contain lowercase letters, numbers, and underscores (a-z, 0-9, _). No spaces, capitals, or hyphens, or the scroll will not load.
:::

## The structure of a scroll

A scroll config breaks into a few distinct parts:

| Part | What it controls |
| --- | --- |
| **Scroll info** | The name, lore, level cap, and use count |
| **Targets and requirements** | Which items it applies to, conflicts, type, and prerequisite scrolls |
| **Effects** | What the scroll does while active on an item |
| **Placeholders** | Custom values you can reuse in lore and other plugins |
| **Scroll item** | The physical scroll item players hold and inscribe from |
| **Inscription** | How the scroll gets applied to an item, and at what cost |

Here is a complete scroll with every part in place:

```yaml
# === Scroll info: name, lore, and limits ===
name: "&6Example Scroll" # Internal display name of the scroll
max-level: 1 # Highest level this scroll can reach
max-uses: 1 # Times the scroll can be used before it's spent

# Lore appended to an item once this scroll is inscribed on it
lore:
  - ""
  - "&7This item has been inscribed with"
  - "&6Example Scroll"

# === Targets and requirements: what it applies to ===
targets: # Items the scroll can be inscribed on; defined in targets.yml
  - sword
conflicts: [ ] # Scroll IDs that can't share an item with this one
type: combat # Optional; groups the scroll and enforces a per-type limit from types.yml

requirements: # Scrolls that must already be on the item first
  - scroll: my_requirement_scroll # ID of the prerequisite scroll
    level: 2 # Level it must be at (optional)
remove-requirements: false # Whether inscribing this removes the prerequisites

# === Placeholders: reusable dynamic values ===
placeholders: # Exposed as %ecoscrolls_scroll_<id>:<key>% for other plugins
  bonus: "%level% * 2"

# === Effects: what the scroll does while active ===
effects:
  - id: send_message
    args:
      message: "&6You have used the Example Scroll!"
    triggers:
      - alt_click
conditions: [ ] # Conditions that must hold for the effects to run

# === Scroll item: the physical scroll ===
item:
  item: paper glint # Base item; supports the Item Lookup System syntax
  name: "&6&lExample Scroll" # Name and lore can use %uses%, %max_uses%, %uses_left%
  lore:
    - "&7This is an example scroll."
    - "&7It does nothing."
  craftable: false # Whether the scroll has a crafting recipe
  recipe-permission: example.scroll.craft # Permission needed to craft it
  shapeless: false # Whether the recipe is shapeless
  recipe: [ ] # The crafting recipe grid

# === Inscription: how it's applied to an item ===
inscription:
  inscription-table: true # Allow applying via the Inscription Table GUI
  drag-and-drop: true # Allow applying by dragging the scroll onto the item
  price: # Cost to inscribe
    value: 100
    type: coins
    display: "&e%value% coins"
  price-level-multiplier: "1 + %level% * 0.5" # Scales price by the scroll's current level
  conditions: [ ] # Conditions required to inscribe
  effects: [ ] # Effects run at the moment of inscribing (e.g. add an enchantment)
```

### Scroll info

The top-level identity of the scroll: how it's named, what lore it adds to the item, and its limits.

```yaml
name: "&6Example Scroll" # Internal display name of the scroll
max-level: 1 # Highest level this scroll can reach
max-uses: 1 # Times the scroll can be used before it's spent

# Lore appended to an item once this scroll is inscribed on it
lore:
  - ""
  - "&7This item has been inscribed with"
  - "&6Example Scroll"
```

### Targets and requirements

Controls which items the scroll fits, what it clashes with, and what must come before it.

```yaml
targets: # Items the scroll can be inscribed on; defined in targets.yml
  - sword
conflicts: [ ] # Scroll IDs that can't share an item with this one
type: combat # Optional; groups the scroll and enforces a per-type limit from types.yml

requirements: # Scrolls that must already be on the item first
  - scroll: my_requirement_scroll # ID of the prerequisite scroll
    level: 2 # Level it must be at (optional)
remove-requirements: false # Whether inscribing this removes the prerequisites
```

:::info Targets live in their own file
Targets map a name like `sword` to a set of items and a slot. Edit or add your own in `targets.yml`.
:::

### Effects

What the scroll actually does once it's on an item and active.

```yaml
effects:
  - id: send_message
    args:
      message: "&6You have used the Example Scroll!"
    triggers:
      - alt_click
conditions: [ ] # Conditions that must hold for the effects to run
```

:::danger Effects are their own system
Effects and conditions are a shared system across every eco plugin, with far more options than shown here.

- [Configuring an Effect](https://plugins.auxilor.io/effects/configuring-an-effect)
- [Configuring an Effect Chain](https://plugins.auxilor.io/effects/configuring-a-chain)
:::

### Placeholders

Named values you define once and reuse, both in this config and in other plugins.

```yaml
placeholders: # Exposed as %ecoscrolls_scroll_<id>:<key>% for other plugins
  bonus: "%level% * 2" # e.g. %ecoscrolls_scroll_example:bonus%
```

:::info Values are expressions
Placeholder values are math expressions, so you can reference `%level%` and other placeholders to scale them.
:::

### Scroll item

The physical scroll item a player holds before inscribing it.

```yaml
item:
  item: paper glint # Base item; supports the Item Lookup System syntax
  name: "&6&lExample Scroll" # Name and lore can use %uses%, %max_uses%, %uses_left%
  lore:
    - "&7This is an example scroll."
    - "&7It does nothing."
  craftable: false # Whether the scroll has a crafting recipe
  recipe-permission: example.scroll.craft # Permission needed to craft it
  shapeless: false # Whether the recipe is shapeless
  recipe: [ ] # The crafting recipe grid
```

:::tip
We support shaped and shapeless recipes. See [Recipes](https://plugins.auxilor.io/the-item-lookup-system/recipes) for how to configure them.
:::

### Inscription

How the scroll moves from the item in hand onto a target item, and what it costs.

```yaml
inscription:
  inscription-table: true # Allow applying via the Inscription Table GUI
  drag-and-drop: true # Allow applying by dragging the scroll onto the item
  price: # Cost to inscribe
    value: 100
    type: coins
    display: "&e%value% coins"
  price-level-multiplier: "1 + %level% * 0.5" # Scales price by the scroll's current level
  conditions: [ ] # Conditions required to inscribe
  effects: [ ] # Effects run at the moment of inscribing (e.g. add an enchantment)
```

Put effects here, not in the top-level `effects`, when the scroll works by permanently changing the item (adding an enchantment, repairing it) rather than acting while equipped.

## Internal placeholders

| Placeholder | Value |
| --- | --- |
| `%level%` | The scroll's current level |
| `%uses%` | The number of times the scroll has been used |
| `%max_uses%` | The maximum number of uses the scroll has |
| `%uses_left%` | The number of uses remaining |

:::tip Troubleshooting
- **Scroll doesn't show up in game?** The file name has a capital, space, or hyphen, or you forgot to `/ecoscrolls reload`. Rename to lowercase letters, numbers, and underscores only.
- **Inscribing does nothing?** The item isn't in the scroll's `targets`. Check the target name exists in `targets.yml`.
- **Effects never fire?** The `triggers` don't match how you're using the item, or a `condition` is failing. Check the effect's trigger against [Configuring an Effect](https://plugins.auxilor.io/effects/configuring-an-effect).
:::

<hr/>

## Where to go next

- **Scroll Types:** group scrolls and cap how many of each fit on an item, see [Scroll Types](scroll-types).
- **Effects:** the full effect, condition, and trigger reference at [Configuring an Effect](https://plugins.auxilor.io/effects/configuring-an-effect).
- **Real examples:** the shipped scroll configs [on GitHub](https://github.com/Auxilor/EcoScrolls/tree/master/eco-core/core-plugin/src/main/resources/scrolls).
- **Community configs:** browse and share more on [lrcdb](https://lrcdb.auxilor.io/).