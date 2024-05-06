# [KTgBotAPI](https://github.com/InsanusMokrassar/ktgbotapi) keyboards library [![Maven Central](https://maven-badges.herokuapp.com/maven-central/dev.inmo/ktgbotapi.keyboards.lib/badge.svg)](https://maven-badges.herokuapp.com/maven-central/dev.inmo/ktgbotapi.keyboards.lib)

This library provides special DSL for Telegram Bots API keyboards.

## Connection

Gradle Groovy:

```groovy
implementation "dev.inmo:tgbotapi.keyboards.lib:$version"
```

Gradle Kotlin:

```kotlin
implementation("dev.inmo:tgbotapi.keyboards.lib:$version")
```

Maven:

```xml
<dependency>
  <groupId>dev.inmo</groupId>
  <artifactId>tgbotapi.keyboards.lib</artifactId>
  <version>$version</version>
</dependency>
```

## How to use

```kotlin
val menu = buildMenu rootMenu@{
    row {
        dataWithsubMenu(
            id = "sample", // id for button to be used in current menu
            text = "Sample", // text for button
            callbacksRegex = Regex("sample") // optional regex for all callbacks to be registered in the future
        ) { // callback for builder, here `it` is `DataCallbackQuery?`, where null-value means initial setup
            row {
                dataWithOptionalSubMenu(
                    id = "back", // id for button to be used in current menu
                    text = "Back", // text for button
                    callbacksRegex = Regex("back") // optional regex for all callbacks to be registered in the future
                ) {
                    if (it != null) { // when real request
                        this@rootMenu.buildLazy()
                    } else { // when initial call
                        null
                    }
                }
            }
        }
    }
}
```

As you may see, there are two phases in common case:

* Initialization of menus
* Handling of requests

### Initialization of menus

So, we have `menu` and now we may setup it in bot:

```kotlin
val bot = telegramBot("YOUR TOKEN")
bot.buildBehaviourWithLongPolling {
    setupMenuTriggers(menu) // setting up listening of buttons clicking
}
```
