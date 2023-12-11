package dev.inmo.tgbotapi.keyboards.lib.dsl

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.keyboards.lib.KeyboardBuilder
import dev.inmo.tgbotapi.keyboards.lib.KeyboardMenu
import dev.inmo.tgbotapi.types.LoginURL
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import dev.inmo.tgbotapi.types.webapps.WebAppInfo
import dev.inmo.tgbotapi.utils.RowBuilder

fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.webApp(
    textBuilder: suspend BC.() -> String,
    webAppInfoBuilder: suspend BC.() -> WebAppInfo
) = +KeyboardBuilder.Button.WebApp(
    textBuilder = textBuilder,
    webAppInfoBuilder = webAppInfoBuilder
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.webApp(
    webApp: WebAppInfo,
    textBuilder: suspend BC.() -> String,
) = webApp(
    textBuilder = textBuilder,
    webAppInfoBuilder = { webApp }
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.webApp(
    text: String,
    webAppInfoBuilder: suspend BC.() -> WebAppInfo
) = webApp(
    textBuilder = { text },
    webAppInfoBuilder = webAppInfoBuilder
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.webApp(
    text: String,
    webApp: WebAppInfo
) = webApp({ text }) { webApp }
