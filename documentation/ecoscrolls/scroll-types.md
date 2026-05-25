---
title: Scroll Types
sidebar_position: 2
---

## What are Scroll Types?

Scroll types group scrolls into categories and enforce a per-item limit on how many **different** scrolls from the same type can be inscribed on a single item.

For example, if the `combat` type has a limit of 3, a player can inscribe at most 3 different combat scrolls on one item.

## Configuring Types

Types are defined in `types.yml`:

```yaml
types:
  - id: combat
    display-name: "&cCombat"
    limit: 3

  - id: utility
    display-name: "&bUtility"
    limit: 2
```

| Option         | Description                                                       |
| -------------- | ----------------------------------------------------------------- |
| `id`           | Unique identifier for the type (used in scroll configs)           |
| `display-name` | Display name shown in lore (supports color codes)                 |
| `limit`        | Max number of different scrolls of this type per item (`-1` = unlimited) |

## Assigning a Type to a Scroll

In a scroll's config file, set the `type` field to a type ID:

```yaml
type: combat
```

Scrolls without a `type` field have no type restriction and don't count toward any type limit.

## Lore Ordering

The `lore-order` option in `config.yml` controls the display order of scroll lore grouped by type.
Use `other` as a placeholder for scrolls with no type or types not explicitly listed:

```yaml
lore-order:
  - combat
  - utility
  - other
```

Leave `lore-order` empty to use the default inscription order.