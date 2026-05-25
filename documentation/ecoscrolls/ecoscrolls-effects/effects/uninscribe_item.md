# `uninscribe_item`
:::infoRequires:
EcoScrolls
:::

:::dangerTriggered Effect
This effect requires a [Trigger](https://plugins.auxilor.io/effects/all-triggers) to activate.
:::

Removes or decreases the level of a scroll inscribed on an item

# Effect Syntax
```yaml
- id: uninscribe_item
  args:
    scroll: example_scroll # The scroll ID to remove
    type: remove # (Optional) Use "remove" to remove the scroll entirely, "decrease" reduces level by 1 (removes at level 0). Defaults to "remove"
  ...other config (eg triggers, filters, mutators, etc)
```