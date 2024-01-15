package dev.inmo.tgbotapi.keyboards.lib.dsl

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.keyboards.lib.KeyboardBuilder
import dev.inmo.tgbotapi.keyboards.lib.KeyboardMenu
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import dev.inmo.tgbotapi.utils.RowBuilder

fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.game(
    textBuilder: suspend BC.() -> String
) = +KeyboardBuilder.Button.Game(
    textBuilder = textBuilder
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.game(
    text: String
) = game { text }
