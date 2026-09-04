---
title: "Scroll Types"
sidebar_position: 2
---

By the end of this page you'll understand what scroll types are, how to define them, and how they limit which scrolls stack on an item and in what order their lore appears.

## What scroll types are

A scroll type is a category that groups scrolls and caps how many **different** scrolls from that category can be inscribed on a single item. For example, if the `combat` type has a limit of 3, a player can inscribe at most 3 different combat scrolls on one item.

Scrolls without a `type` have no restriction and don't count toward any limit.

## Defining types

Types live in `types.yml`. Each entry needs an ID, a display name, and a limit:

```yaml
types:
  - id: combat # ID referenced by a scroll's "type" field
    display-name: "&cCombat" # Shown in lore; supports color codes
    limit: 3 # Max different combat scrolls per item; -1 for unlimited

  - id: utility
    display-name: "&bUtility"
    limit: 2
```

## Assigning a type to a scroll

In the scroll's own config file, set `type` to a type ID. See [How to Make a Scroll](how-to-make-a-scroll) for the rest of the config.

```yaml
type: combat # Must match an id in types.yml
```

## Ordering lore by type

The `lore-order` option in `config.yml` controls the order scroll lore appears on an item, grouped by type. Use `other` for scrolls with no type, or whose type isn't listed:

```yaml
lore-order:
  - combat
  - utility
  - other # Catches untyped and unlisted scrolls; omit to push them to the end
```

Leave `lore-order` empty to fall back to the default inscription order. See [Plugin Config](plugin-config) for the full file.

<hr/>

## Where to go next

- **Make a scroll:** assign a type while building one in [How to Make a Scroll](how-to-make-a-scroll).
- **Plugin Config:** the full `config.yml`, including `lore-order`, in [Plugin Config](plugin-config).