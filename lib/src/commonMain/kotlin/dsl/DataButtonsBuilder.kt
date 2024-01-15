package dev.inmo.tgbotapi.keyboards.lib.dsl

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.keyboards.lib.KeyboardBuilder
import dev.inmo.tgbotapi.keyboards.lib.KeyboardMenu
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import dev.inmo.tgbotapi.utils.RowBuilder

fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.data(
    id: String,
    reaction: KeyboardBuilder.Button.Data.Reaction<BC>,
    callbacksRegex: Regex = Regex(id),
    textBuilder: suspend BC.() -> String
) = +KeyboardBuilder.Button.Data(
    id = id,
    reaction = reaction,
    callbacksRegex = callbacksRegex,
    textBuilder = textBuilder
)

fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.data(
    id: String,
    textBuilder: suspend BC.() -> String,
    callbacksRegex: Regex = Regex(id),
    callback: suspend BC.(DataCallbackQuery) -> Unit
) = +KeyboardBuilder.Button.Data(
    id = id,
    reaction = KeyboardBuilder.Button.Data.Reaction.Action(callback),
    callbacksRegex = callbacksRegex,
    textBuilder = textBuilder
)
fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.data(
    id: String,
    menu: KeyboardMenu<BC>?,
    transitiveRegistration: Boolean = false,
    callbacksRegex: Regex = Regex(id),
    menuBuilder: suspend BC.(DataCallbackQuery) -> KeyboardMenu<BC>? = { menu },
    textBuilder: suspend BC.() -> String
) = +KeyboardBuilder.Button.Data(
    id = id,
    reaction = KeyboardBuilder.Button.Data.Reaction.Keyboard(menu, transitiveRegistration, menuBuilder),
    callbacksRegex = callbacksRegex,
    textBuilder = textBuilder
)
fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.dataWithNewMenu(
    id: String,
    textBuilder: suspend BC.() -> String,
    callbacksRegex: Regex = Regex(id),
    menuBuilder: KeyboardBuilder<BC>.(DataCallbackQuery?) -> Unit
) = data(
    id = id,
    menu = buildMenu {
        menuBuilder(null)
    },
    transitiveRegistration = true,
    callbacksRegex = callbacksRegex,
    menuBuilder = {
        buildMenu {
            menuBuilder(it)
        }
    },
    textBuilder = textBuilder
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.data(
    id: String,
    reaction: KeyboardBuilder.Button.Data.Reaction<BC>,
    callbacksRegex: Regex = Regex(id),
    text: String
) = data(
    id = id,
    reaction = reaction,
    callbacksRegex = callbacksRegex,
) { text }

fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.data(
    id: String,
    text: String,
    callbacksRegex: Regex = Regex(id),
    callback: suspend BC.(DataCallbackQuery) -> Unit
) = data(
    id = id,
    textBuilder = { text },
    callbacksRegex = callbacksRegex,
    callback = callback
)
fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.data(
    id: String,
    text: String,
    menu: KeyboardMenu<BC>,
    callbacksRegex: Regex = Regex(id),
    transitiveRegistration: Boolean = false,
    menuBuilder: suspend BC.(DataCallbackQuery) -> KeyboardMenu<BC>? = { menu },
) = data(
    id = id,
    menu = menu,
    transitiveRegistration = transitiveRegistration,
    callbacksRegex = callbacksRegex,
    textBuilder = { text },
    menuBuilder = menuBuilder
)
fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.dataWithNewMenu(
    id: String,
    text: String,
    callbacksRegex: Regex = Regex(id),
    menuBuilder: KeyboardBuilder<BC>.(DataCallbackQuery?) -> Unit
) = dataWithNewMenu(
    id = id,
    textBuilder = { text },
    callbacksRegex = callbacksRegex,
    menuBuilder = {
        buildMenu {
            menuBuilder(it)
        }
    }
)


