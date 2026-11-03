---
title: "Scroll Limits"
sidebar_position: 3
---

By the end of this page you'll understand how the scroll limit of an item is set, how each scroll counts toward it, and how to change it in play.

## What the scroll limit is

The scroll limit is how much an item can hold. Each scroll on the item uses part of it. When a new scroll or a new level would use more than the limit, the inscription is denied with the `denied-global-limit` message.

The scroll limit is separate from `max-level` (how high one scroll goes) and from [scroll type](scroll-types) limits (how many different scrolls of one type fit).

## Where the limit comes from

The first of these that applies sets the base limit of an item:

1. **The item itself:** the `scroll_limit:<amount>` lookup argument, for example `iron_pickaxe scroll_limit:5`.
2. **Targets and config:** the lowest `scroll-limit` of the item's targets in `targets.yml`, capped by `inscription.scroll-limit` in `config.yml`.

```yaml
# targets.yml
targets:
  - id: pickaxe
    display-name: "Pickaxes"
    slot: mainhand
    scroll-limit: 3 # Pickaxes hold 3 scrolls
    items:
      - "*diamond_pickaxe"
```

A target's `scroll-limit` can also be an expression, worked out for each item. Item placeholders read that item, so the limit can depend on what the item is made of:

```yaml
    scroll-limit: "3 + %ecoitems_material_count_paper%" # One more for each paper part
```

The `add_scroll_limit` effect adds to the base limit of one item (see below). The total never goes below 0. An item with no limit anywhere (no target limit and `scroll-limit: -1`) stays unlimited.

:::tip
An item made with `scroll_limit:<amount>` keeps its own limit if you change the amount in your config later. Only new items get the new amount.
:::

## How much a scroll costs

By default, each scroll on an item costs 1, whatever its level. `levels-per-limit` in a scroll's config changes that:

| `levels-per-limit` | Cost at level L |
| --- | --- |
| Not set | 1 |
| `0` | 0, the scroll never counts |
| A number N | L divided by N, rounded up |

```yaml
max-level: 104
levels-per-limit: 52 # Costs 1 for levels 1 to 52 and 2 for levels 53 to 104
```

Levelling up a scroll that is already on the item is only denied if the new level costs more and the item has no room for it. A scroll at its `max-level` always shows the maximum level message.

## Changing the limit in play

The `add_scroll_limit` effect adds to the limit of the found item. A negative amount removes from it. Put it in a scroll's `inscription.effects` to make a scroll that adds a slot:

```yaml
max-level: 3
levels-per-limit: 0 # This scroll takes no room itself
inscription:
  effects:
    - id: add_scroll_limit
      args:
        amount: 1 # Adds 1 to the item's scroll limit
```

The added limit belongs to the item, not to the scroll that gave it. If a removal flow takes the scroll off with `uninscribe_item`, take the limit back in the same place:

```yaml
effects:
  - id: uninscribe_item
    args:
      scroll: modifier_slot
      type: decrease
  - id: add_scroll_limit
    args:
      amount: -1 # Takes back the slot the scroll gave
```

Scrolls already on an item stay if its limit drops below what they use. Nothing new that costs limit can be added until there is room again.

The `has_scroll_limit` condition passes when the item has at least the given amount of free limit. Use it in `inscription.conditions`, which see the item being inscribed:

```yaml
inscription:
  conditions:
    - id: has_scroll_limit
      args:
        amount: 2 # The item needs 2 free limit
```

## Placeholders

These placeholders read the item in context, for example in scroll lore:

| Placeholder | Value |
| --- | --- |
| `%ecoscrolls_limit_used%` | The limit used by all scrolls on the item |
| `%ecoscrolls_limit_total%` | The item's total limit; empty when unlimited |
| `%ecoscrolls_limit_free%` | The limit left; empty when unlimited |

```yaml
lore:
  - "&7Slots: %ecoscrolls_limit_used%/%ecoscrolls_limit_total%"
```

<hr/>

## Where to go next

- **Make a scroll:** set `levels-per-limit` while building one in [How to Make a Scroll](how-to-make-a-scroll).
- **Scroll Types:** cap how many scrolls of one type fit in [Scroll Types](scroll-types).
- **Plugin Config:** the global `scroll-limit` in [Plugin Config](plugin-config).
