package dev.inmo.tgbotapi.keyboards.lib.dsl

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.keyboards.lib.KeyboardBuilder
import dev.inmo.tgbotapi.keyboards.lib.KeyboardMenu
import dev.inmo.tgbotapi.types.LoginURL
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import dev.inmo.tgbotapi.utils.RowBuilder

fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.loginUrl(
    textBuilder: suspend BC.() -> String,
    loginUrlBuilder: suspend BC.() -> LoginURL
) = +KeyboardBuilder.Button.LoginUrl(
    textBuilder = textBuilder,
    loginUrlBuilder = loginUrlBuilder
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.loginUrl(
    loginUrl: LoginURL,
    textBuilder: suspend BC.() -> String,
) = loginUrl(
    textBuilder = textBuilder,
    loginUrlBuilder = { loginUrl }
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.loginUrl(
    text: String,
    loginUrlBuilder: suspend BC.() -> LoginURL
) = loginUrl(
    textBuilder = { text },
    loginUrlBuilder = loginUrlBuilder
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.loginUrl(
    text: String,
    loginUrl: LoginURL
) = loginUrl({ text }) { loginUrl }
