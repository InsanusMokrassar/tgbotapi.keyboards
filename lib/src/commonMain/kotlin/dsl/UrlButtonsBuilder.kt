package dev.inmo.tgbotapi.keyboards.lib.dsl

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.keyboards.lib.KeyboardBuilder
import dev.inmo.tgbotapi.utils.RowBuilder

fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.url(
    textBuilder: suspend BC.() -> String,
    urlBuilder: suspend BC.() -> String
) = +KeyboardBuilder.Button.URL(
    textBuilder = textBuilder,
    urlBuilder = urlBuilder
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.url(
    textBuilder: suspend BC.() -> String,
    url: String,
) = url(
    textBuilder = textBuilder,
    urlBuilder = { url }
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.url(
    text: String,
    urlBuilder: suspend BC.() -> String
) = url(
    textBuilder = { text },
    urlBuilder = urlBuilder
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.url(
    text: String,
    url: String
) = url({ text }) { url }
