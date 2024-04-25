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

/**
 * Build default data button with optionally included submenu (if not null)
 *
 * @param menuBuilder Will receive null as [DataCallbackQuery] on setup stage to setup full menu triggers
 */
fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.dataWithOptionalSubMenu(
    id: String,
    textBuilder: suspend BC.() -> String,
    callbacksRegex: Regex = Regex(id),
    menuBuilder: suspend BC.(DataCallbackQuery?) -> KeyboardMenu<BC>?
) = +KeyboardBuilder.Button.Data(
    id = id,
    reaction = KeyboardBuilder.Button.Data.Reaction.Keyboard(menuBuilder),
    callbacksRegex = callbacksRegex,
    textBuilder = textBuilder
)

/**
 * Build default data button with optionally included submenu (if not empty)
 *
 * @param menuBuilder Will receive null as [DataCallbackQuery] on setup stage to setup full menu triggers
 */
fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.dataWithSubMenu(
    id: String,
    textBuilder: suspend BC.() -> String,
    callbacksRegex: Regex = Regex(id),
    menuBuilder: KeyboardBuilder<BC>.(DataCallbackQuery?) -> Unit
) = dataWithOptionalSubMenu(
    id = id,
    textBuilder = textBuilder,
    callbacksRegex = callbacksRegex,
    menuBuilder = {
        buildMenu {
            menuBuilder(it)
        }
    }
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.data(
    id: String,
    text: String,
    reaction: KeyboardBuilder.Button.Data.Reaction<BC>,
    callbacksRegex: Regex = Regex(id)
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

/**
 * Build default data button with optionally included submenu (if not null)
 *
 * @param menuBuilder Will receive null as [DataCallbackQuery] on setup stage to setup full menu triggers
 */
fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.dataWithOptionalSubMenu(
    id: String,
    text: String,
    callbacksRegex: Regex = Regex(id),
    menuBuilder: suspend BC.(DataCallbackQuery?) -> KeyboardMenu<BC>?,
) = dataWithOptionalSubMenu(
    id = id,
    textBuilder = { text },
    callbacksRegex = callbacksRegex,
    menuBuilder = menuBuilder
)

/**
 * Build default data button with optionally included submenu (if not null)
 *
 * @param menuBuilder Will receive null as [DataCallbackQuery] on setup stage to setup full menu triggers
 */
fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.dataWithSubMenu(
    id: String,
    text: String,
    callbacksRegex: Regex = Regex(id),
    menuBuilder: KeyboardBuilder<BC>.(DataCallbackQuery?) -> Unit
) = dataWithSubMenu(
    id = id,
    textBuilder = { text },
    callbacksRegex = callbacksRegex,
    menuBuilder = menuBuilder
)


