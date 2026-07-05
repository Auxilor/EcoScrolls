---
title: "Plugin Config"
sidebar_position: 6
---

`config.yml` holds the server-wide settings for EcoScrolls: the Inscription Table GUI, inscription effects, sounds, and lore ordering. It lives at `/plugins/EcoScrolls/config.yml`. After editing it, run `/ecoscrolls reload` to apply your changes.

## Default config.yml

```yaml
discover-recipes: true # Whether all scroll crafting recipes are unlocked by default

# Options for the Inscription Table GUI
gui:
  rows: 6 # Number of rows in the GUI
  title: "Inscription Table" # GUI window title

  mask:
    # The mask is a list of materials plus a pattern that places them.
    # Each pattern row is 9 long, with one row per GUI row.
    # 0 is empty; 1 is the first material, 2 the second, up to 9.
    items: # Materials referenced by the pattern numbers
      - black_stained_glass_pane
      - magenta_stained_glass_pane
    pattern: # Layout of the mask materials
      - "111111111"
      - "100010001"
      - "111000111"
      - "211101112"
      - "221111122"
      - "222101222"

  indicator:
    allow-item: lime_stained_glass_pane # Shown in slots where inscribing is allowed
    deny-item: red_stained_glass_pane # Shown in slots where inscribing is denied
    pattern: # 1 marks a show-allowed slot, 0 does not
      - "000000000"
      - "001101100"
      - "000111000"
      - "000000000"
      - "000000000"
      - "000000000"

  inscribe-slot: # Slot holding the inscribe button
    row: 4
    column: 5

  scroll-slot: # Slot the player drops the scroll into
    row: 2
    column: 8

  item-slot: # Slot the player drops the target item into
    row: 2
    column: 2

  close: # The close button
    item: barrier # Item used for the button
    name: "&cClose" # Button name
    lore: [] # Button lore
    location:
      row: 6
      column: 5

  allow: # Button shown when the item can be inscribed
    item: feather unbreaking:1 hide_enchants
    name: "&aInscribe Item"
    lore:
      - "&7Inscribe %scroll% &7onto"
      - "&7the item on the left, granting it"
      - "&7a new ability or effect."
      - ""
      - "&7Price:"
      - "%price%"
      - ""
      - "&eClick to inscribe!"

  deny: # Button shown when the item can't be inscribed
    item: barrier
    name: "&cCannot Inscribe"
    lore:
      - "&7You cannot inscribe this item with this!"

  empty: # Button shown before a scroll and item are placed
    item: feather unbreaking:1 hide_enchants
    name: "&aInscribe Item"
    lore:
      - "&7Place an item on the left and"
      - "&7a scroll on the right to inscribe it."

  open-effects: [ ] # Effects run when the GUI is opened
  close-effects: [ ] # Effects run when the GUI is closed

  # Custom GUI slots; see https://hub.auxilor.io/wiki/eco/pages
  custom-slots: [ ]

# Options for inscribing items
inscription:
  # Effects run when an item is inscribed.
  # The text placeholder is the scroll name; see
  # https://hub.auxilor.io/wiki/libreforge/configuring-an-effect#placeholders
  # You can also use %scroll%, %scroll_id%, and %level%.
  apply-effects: [ ]

  # Effects run when inscribing is attempted without meeting the conditions.
  # Same placeholders as apply-effects.
  deny-effects: [ ]

  scroll-limit: -1 # Max scrolls per item; -1 for unlimited

# Sounds played on inscription events
sounds:
  inscribe: # Played on a successful inscribe
    enabled: true # Whether the sound plays
    sound: ENTITY_PLAYER_LEVELUP # Sound to play
    volume: 1 # Playback volume
    pitch: 1 # Playback pitch
    category: PLAYERS # Sound category
  inscribe-fail: # Played on a failed inscribe
    enabled: true
    sound: ENTITY_VILLAGER_NO
    volume: 1
    pitch: 1
    category: PLAYERS

# Order scroll lore appears on items, grouped by type.
# List type IDs in display order; use "other" for untyped or unlisted scrolls.
# If "other" is omitted, those scrolls appear at the end.
# Leave empty to use the default (inscription) order.
lore-order:
  - combat
  - utility
  - other
```

<hr/>

## Where to go next

- **Scroll Types:** how `lore-order` and type limits work in [Scroll Types](scroll-types).
- **Make a scroll:** build a scroll to inscribe through this GUI in [How to Make a Scroll](how-to-make-a-scroll).
- **Effects:** the shared effect system used by `apply-effects` at [Configuring an Effect](https://hub.auxilor.io/wiki/libreforge/configuring-an-effect).