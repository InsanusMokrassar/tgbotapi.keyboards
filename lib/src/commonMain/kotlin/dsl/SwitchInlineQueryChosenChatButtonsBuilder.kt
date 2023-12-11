package dev.inmo.tgbotapi.keyboards.lib.dsl

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.CustomBehaviourContextAndTypeReceiver
import dev.inmo.tgbotapi.extensions.behaviour_builder.utils.SimpleFilter
import dev.inmo.tgbotapi.keyboards.lib.KeyboardBuilder
import dev.inmo.tgbotapi.keyboards.lib.KeyboardMenu
import dev.inmo.tgbotapi.types.InlineQueries.query.BaseInlineQuery
import dev.inmo.tgbotapi.types.InlineQueries.query.InlineQuery
import dev.inmo.tgbotapi.types.LoginURL
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import dev.inmo.tgbotapi.utils.RowBuilder

fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.switchInlineQueryChosenChat(
    textBuilder: suspend BC.() -> String,
    parametersBuilder: suspend BC.() -> dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.SwitchInlineQueryChosenChat
) = +KeyboardBuilder.Button.SwitchInlineQueryChosenChat(
    textBuilder = textBuilder,
    parametersBuilder = parametersBuilder
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.switchInlineQueryChosenChat(
    parametersBuilder: dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.SwitchInlineQueryChosenChat,
    textBuilder: suspend BC.() -> String,
) = switchInlineQueryChosenChat(
    textBuilder = textBuilder,
    parametersBuilder = { parametersBuilder }
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.switchInlineQueryChosenChat(
    text: String,
    parametersBuilder: suspend BC.() -> dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.SwitchInlineQueryChosenChat
) = switchInlineQueryChosenChat(
    textBuilder = { text },
    parametersBuilder = parametersBuilder
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.switchInlineQueryChosenChat(
    text: String,
    switchInlineQueryChosenChat: dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.SwitchInlineQueryChosenChat
) = switchInlineQueryChosenChat({ text }) { switchInlineQueryChosenChat }


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.switchInlineQueryChosenChat(
    textBuilder: suspend BC.() -> String,
    parametersBuilder: suspend BC.() -> dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.SwitchInlineQueryChosenChat,
    initialFilter: SimpleFilter<InlineQuery>? = null,
    onBaseInlineQueryCallback: CustomBehaviourContextAndTypeReceiver<BC, Unit, BaseInlineQuery>
) = +KeyboardBuilder.Button.SwitchInlineQueryChosenChat(
    textBuilder = textBuilder,
    parametersBuilder = parametersBuilder,
    initialFilter = initialFilter,
    onBaseInlineQueryCallback = onBaseInlineQueryCallback
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.switchInlineQueryChosenChat(
    textBuilder: suspend BC.() -> String,
    parametersBuilder: dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.SwitchInlineQueryChosenChat,
    initialFilter: SimpleFilter<InlineQuery>? = null,
    onBaseInlineQueryCallback: CustomBehaviourContextAndTypeReceiver<BC, Unit, BaseInlineQuery>
) = switchInlineQueryChosenChat(
    textBuilder = textBuilder,
    parametersBuilder = { parametersBuilder },
    initialFilter = initialFilter,
    onBaseInlineQueryCallback = onBaseInlineQueryCallback
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.switchInlineQueryChosenChat(
    text: String,
    parametersBuilder: suspend BC.() -> dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.SwitchInlineQueryChosenChat,
    initialFilter: SimpleFilter<InlineQuery>? = null,
    onBaseInlineQueryCallback: CustomBehaviourContextAndTypeReceiver<BC, Unit, BaseInlineQuery>
) = switchInlineQueryChosenChat(
    textBuilder = { text },
    parametersBuilder = parametersBuilder,
    initialFilter = initialFilter,
    onBaseInlineQueryCallback = onBaseInlineQueryCallback
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.switchInlineQueryChosenChat(
    text: String,
    switchInlineQueryChosenChat: dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.SwitchInlineQueryChosenChat,
    initialFilter: SimpleFilter<InlineQuery>? = null,
    onBaseInlineQueryCallback: CustomBehaviourContextAndTypeReceiver<BC, Unit, BaseInlineQuery>
) = switchInlineQueryChosenChat(
    { text },
    { switchInlineQueryChosenChat },
    initialFilter = initialFilter,
    onBaseInlineQueryCallback = onBaseInlineQueryCallback
)
