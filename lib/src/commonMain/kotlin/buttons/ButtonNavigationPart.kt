package dev.inmo.tgbotapi.keyboards.lib.buttons

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.keyboards.lib.KeyboardNavigationBuilder
import dev.inmo.tgbotapi.types.InlineQueries.query.BaseInlineQuery
import dev.inmo.tgbotapi.types.InlineQueries.query.InlineQuery
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.CallbackDataInlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.InlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.SwitchInlineQueryChosenChatInlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.SwitchInlineQueryCurrentChatInlineKeyboardButton
import dev.inmo.tgbotapi.types.queries.callback.MessageDataCallbackQuery

sealed interface ButtonNavigationPart {
    val buttonBuilder: suspend BehaviourContext.() -> InlineKeyboardButton
    class CallbackData(
        override val buttonBuilder: suspend BehaviourContext.() -> CallbackDataInlineKeyboardButton,
        val onClick: suspend BehaviourContext.(MessageDataCallbackQuery) -> KeyboardNavigationBuilder?
    ) : ButtonNavigationPart

    class SwitchInlineQueryChosen(
        override val buttonBuilder: suspend BehaviourContext.() -> SwitchInlineQueryChosenChatInlineKeyboardButton,
        val onInlineQuery: (BehaviourContext.(BaseInlineQuery) -> Unit)? = null
    ) : ButtonNavigationPart

    class SwitchInlineQueryCurrent(
        override val buttonBuilder: suspend BehaviourContext.() -> SwitchInlineQueryCurrentChatInlineKeyboardButton,
        val onInlineQuery: (BehaviourContext.(BaseInlineQuery) -> Unit)? = null
    ) : ButtonNavigationPart

    class Simple(
        override val buttonBuilder: suspend BehaviourContext.() -> InlineKeyboardButton
    ) : ButtonNavigationPart
}