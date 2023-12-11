package dev.inmo.tgbotapi.keyboards.lib.dsl

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.CustomBehaviourContextAndTypeReceiver
import dev.inmo.tgbotapi.extensions.behaviour_builder.utils.SimpleFilter
import dev.inmo.tgbotapi.keyboards.lib.KeyboardBuilder
import dev.inmo.tgbotapi.keyboards.lib.KeyboardMenu
import dev.inmo.tgbotapi.types.InlineQueries.query.BaseInlineQuery
import dev.inmo.tgbotapi.types.InlineQueries.query.InlineQuery
import dev.inmo.tgbotapi.types.LoginURL
import dev.inmo.tgbotapi.types.payments.PreCheckoutQuery
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import dev.inmo.tgbotapi.utils.RowBuilder

fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.pay(
    textBuilder: suspend BC.() -> String,
) = +KeyboardBuilder.Button.Pay(
    textBuilder = textBuilder,
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.pay(
    text: String,
) = pay(
    textBuilder = { text },
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.pay(
    textBuilder: suspend BC.() -> String,
    initialFilter: SimpleFilter<PreCheckoutQuery>? = null,
    onPreCheckoutQueryCallback: CustomBehaviourContextAndTypeReceiver<BC, Unit, PreCheckoutQuery>
) = +KeyboardBuilder.Button.Pay(
    textBuilder = textBuilder,
    initialFilter = initialFilter,
    onPreCheckoutQueryCallback = onPreCheckoutQueryCallback
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.pay(
    text: String,
    initialFilter: SimpleFilter<PreCheckoutQuery>? = null,
    onPreCheckoutQueryCallback: CustomBehaviourContextAndTypeReceiver<BC, Unit, PreCheckoutQuery>
) = pay(
    textBuilder = { text },
    initialFilter = initialFilter,
    onPreCheckoutQueryCallback = onPreCheckoutQueryCallback
)
