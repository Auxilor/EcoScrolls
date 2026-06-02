---
title: "API"
sidebar_position: 7
---

This page is for developers who want to hook into EcoScrolls from their own plugin. EcoScrolls is open-source, so you can read the code, depend on it, and build against its API.

## Source code

The full source is on GitHub at [Auxilor/EcoScrolls](https://github.com/Auxilor/EcoScrolls).

## Adding the dependency

1. Add the Auxilor repository to your `build.gradle.kts`.
2. Add EcoScrolls as a `compileOnly` dependency.

```kotlin
repositories {
    maven("https://repo.auxilor.io/repository/maven-public/")
}

dependencies {
    compileOnly("com.willfp:EcoScrolls:<version>")
}
```

The latest version available on the repo can be found [here](https://github.com/Auxilor/EcoScrolls/tags).

<hr/>

## Where to go next

- **eco framework:** the shared APIs EcoScrolls builds on live in [eco](https://github.com/Auxilor/eco).
- **Make a scroll:** the config side of scrolls in [How to Make a Scroll](how-to-make-a-scroll).