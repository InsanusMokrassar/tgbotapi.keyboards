package dev.inmo.tgbotapi.keyboards.lib.dsl

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.keyboards.lib.KeyboardBuilder
import dev.inmo.tgbotapi.keyboards.lib.KeyboardMenu
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import dev.inmo.tgbotapi.utils.RowBuilder

fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.data(
    id: String,
    reaction: KeyboardBuilder.Button.Data.Reaction<BC>,
    textBuilder: suspend BC.() -> String
) = +KeyboardBuilder.Button.Data(
    id = id,
    reaction = reaction,
    textBuilder = textBuilder
)

fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.data(
    id: String,
    textBuilder: suspend BC.() -> String,
    callback: suspend BC.(DataCallbackQuery) -> Unit
) = +KeyboardBuilder.Button.Data(
    id = id,
    reaction = KeyboardBuilder.Button.Data.Reaction.Action(callback),
    textBuilder = textBuilder
)
fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.data(
    id: String,
    menu: KeyboardMenu<BC>,
    transitiveRegistration: Boolean = false,
    textBuilder: suspend BC.() -> String
) = +KeyboardBuilder.Button.Data(
    id = id,
    reaction = KeyboardBuilder.Button.Data.Reaction.Keyboard(transitiveRegistration, menu),
    textBuilder = textBuilder
)
inline fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.dataWithNewMenu(
    id: String,
    noinline textBuilder: suspend BC.() -> String,
    menuBuilder: KeyboardBuilder<BC>.() -> Unit
) = data(
    id = id,
    menu = buildMenu(menuBuilder),
    transitiveRegistration = true,
    textBuilder = textBuilder
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.data(
    id: String,
    reaction: KeyboardBuilder.Button.Data.Reaction<BC>,
    text: String
) = data(
    id = id,
    reaction
) { text }

fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.data(
    id: String,
    text: String,
    callback: suspend BC.(DataCallbackQuery) -> Unit
) = data(
    id = id,
    textBuilder = { text },
    callback = callback
)
fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.data(
    id: String,
    text: String,
    menu: KeyboardMenu<BC>,
    transitiveRegistration: Boolean = false
) = data(
    id = id,
    menu = menu,
    transitiveRegistration = transitiveRegistration,
    textBuilder = { text }
)
inline fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.dataWithNewMenu(
    id: String,
    text: String,
    menuBuilder: KeyboardBuilder<BC>.() -> Unit
) = dataWithNewMenu(
    id = id,
    textBuilder = { text },
    menuBuilder = menuBuilder
)


