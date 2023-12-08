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

fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.switchInlineQuery(
    textBuilder: suspend BC.() -> String,
    initialInlineQueryBuilder: suspend BC.() -> String
) = +KeyboardBuilder.Button.SwitchInlineQuery(
    textBuilder = textBuilder,
    initialInlineQueryBuilder = initialInlineQueryBuilder
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.switchInlineQuery(
    textBuilder: suspend BC.() -> String,
    initialInlineQueryBuilder: String,
) = switchInlineQuery(
    textBuilder = textBuilder,
    initialInlineQueryBuilder = { initialInlineQueryBuilder }
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.switchInlineQuery(
    text: String,
    initialInlineQueryBuilder: suspend BC.() -> String
) = switchInlineQuery(
    textBuilder = { text },
    initialInlineQueryBuilder = initialInlineQueryBuilder
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.switchInlineQuery(
    text: String,
    switchInlineQuery: String
) = switchInlineQuery({ text }) { switchInlineQuery }


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.switchInlineQuery(
    textBuilder: suspend BC.() -> String,
    initialInlineQueryBuilder: suspend BC.() -> String,
    initialFilter: SimpleFilter<InlineQuery>? = null,
    onBaseInlineQueryCallback: CustomBehaviourContextAndTypeReceiver<BC, Unit, BaseInlineQuery>
) = +KeyboardBuilder.Button.SwitchInlineQuery(
    textBuilder = textBuilder,
    initialInlineQueryBuilder = initialInlineQueryBuilder,
    initialFilter = initialFilter,
    onBaseInlineQueryCallback = onBaseInlineQueryCallback
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.switchInlineQuery(
    textBuilder: suspend BC.() -> String,
    initialInlineQueryBuilder: String,
    initialFilter: SimpleFilter<InlineQuery>? = null,
    onBaseInlineQueryCallback: CustomBehaviourContextAndTypeReceiver<BC, Unit, BaseInlineQuery>
) = switchInlineQuery(
    textBuilder = textBuilder,
    initialInlineQueryBuilder = { initialInlineQueryBuilder },
    initialFilter = initialFilter,
    onBaseInlineQueryCallback = onBaseInlineQueryCallback
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.switchInlineQuery(
    text: String,
    initialInlineQueryBuilder: suspend BC.() -> String,
    initialFilter: SimpleFilter<InlineQuery>? = null,
    onBaseInlineQueryCallback: CustomBehaviourContextAndTypeReceiver<BC, Unit, BaseInlineQuery>
) = switchInlineQuery(
    textBuilder = { text },
    initialInlineQueryBuilder = initialInlineQueryBuilder,
    initialFilter = initialFilter,
    onBaseInlineQueryCallback = onBaseInlineQueryCallback
)


fun <BC : BehaviourContext> RowBuilder<KeyboardBuilder.Button<BC>>.switchInlineQuery(
    text: String,
    switchInlineQuery: String,
    initialFilter: SimpleFilter<InlineQuery>? = null,
    onBaseInlineQueryCallback: CustomBehaviourContextAndTypeReceiver<BC, Unit, BaseInlineQuery>
) = switchInlineQuery(
    { text },
    { switchInlineQuery },
    initialFilter = initialFilter,
    onBaseInlineQueryCallback = onBaseInlineQueryCallback
)
