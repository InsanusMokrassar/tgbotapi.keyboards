package dev.inmo.tgbotapi.keyboards.lib

import dev.inmo.micro_utils.coroutines.subscribeSafelyWithoutExceptions
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.CustomBehaviourContextAndTypeReceiver
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onBaseInlineQuery
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onDataCallbackQuery
import dev.inmo.tgbotapi.extensions.behaviour_builder.utils.SimpleFilter
import dev.inmo.tgbotapi.requests.edit.reply_markup.EditChatMessageReplyMarkup
import dev.inmo.tgbotapi.requests.edit.reply_markup.EditInlineMessageReplyMarkup
import dev.inmo.tgbotapi.types.InlineQueries.query.BaseInlineQuery
import dev.inmo.tgbotapi.types.InlineQueries.query.InlineQuery
import dev.inmo.tgbotapi.types.LoginURL
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.*
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import dev.inmo.tgbotapi.types.queries.callback.InlineMessageIdDataCallbackQuery
import dev.inmo.tgbotapi.types.queries.callback.MessageDataCallbackQuery
import dev.inmo.tgbotapi.types.webapps.WebAppInfo
import dev.inmo.tgbotapi.utils.MatrixBuilder

class KeyboardBuilder<BC : BehaviourContext> : MatrixBuilder<KeyboardBuilder.Button<BC>>() {
    sealed interface Button<BC : BehaviourContext> {
        class Data<BC : BehaviourContext> (
            val id: String,
            val reaction: Reaction<BC>,
            val textBuilder: suspend BC.() -> String
        ) : Button<BC> {
            sealed interface Reaction<BC : BehaviourContext> {
                class Keyboard<BC : BehaviourContext>(
                    val transitiveRegistration: Boolean = true,
                    val keyboardMenu: KeyboardMenu<BC>
                ) : Reaction<BC>
                class Action<BC : BehaviourContext>(
                    val callback: suspend BC.(DataCallbackQuery) -> Unit
                ) : Reaction<BC>

                companion object
            }

            override suspend fun buildButton(context: BC): InlineKeyboardButton = CallbackDataInlineKeyboardButton(
                text = context.textBuilder(),
                callbackData = id
            )

            override suspend fun includeTriggers(context: BC) {
                with(context) {
                    when (reaction) {
                        is Reaction.Action -> onDataCallbackQuery(id) {
                            reaction.callback(this, it)
                        }
                        is Reaction.Keyboard -> {
                            if (reaction.transitiveRegistration) {
                                reaction.keyboardMenu.setupTriggers(this)
                            }
                            onDataCallbackQuery(id) {
                                val keyboard = reaction.keyboardMenu.buildButtons(this)
                                when (it) {
                                    is InlineMessageIdDataCallbackQuery -> execute(
                                        EditInlineMessageReplyMarkup(
                                            it.inlineMessageId,
                                            keyboard
                                        )
                                    )
                                    is MessageDataCallbackQuery -> execute(
                                        EditChatMessageReplyMarkup(
                                            it.message.chat.id,
                                            it.message.messageId,
                                            keyboard
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        class Game<BC : BehaviourContext> (
            val textBuilder: suspend BC.() -> String
        ) : Button<BC> {
            override suspend fun buildButton(context: BC): InlineKeyboardButton = CallbackGameInlineKeyboardButton(
                textBuilder(context)
            )

            override suspend fun includeTriggers(context: BC) {}
        }
        class LoginUrl<BC : BehaviourContext> (
            val textBuilder: suspend BC.() -> String,
            val loginUrlBuilder: suspend BC.() -> LoginURL
        ) : Button<BC> {
            override suspend fun buildButton(context: BC): InlineKeyboardButton = LoginURLInlineKeyboardButton(
                textBuilder(context),
                loginUrlBuilder(context)
            )

            override suspend fun includeTriggers(context: BC) {}
        }
        class Pay<BC : BehaviourContext> (
            val textBuilder: suspend BC.() -> String,
        ) : Button<BC> {
            override suspend fun buildButton(context: BC): InlineKeyboardButton = PayInlineKeyboardButton(
                textBuilder(context),
            )

            override suspend fun includeTriggers(context: BC) {
            }
        }
        class SwitchInlineQueryChosenChat<BC : BehaviourContext> (
            val textBuilder: suspend BC.() -> String,
            val parametersBuilder: suspend BC.() -> dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.SwitchInlineQueryChosenChat,
            val initialFilter: SimpleFilter<InlineQuery>? = null,
            val onBaseInlineQueryCallback: CustomBehaviourContextAndTypeReceiver<BC, Unit, BaseInlineQuery>? = null
        ) : Button<BC> {
            override suspend fun buildButton(context: BC): InlineKeyboardButton = SwitchInlineQueryChosenChatInlineKeyboardButton(
                textBuilder(context),
                parametersBuilder(context),
            )

            override suspend fun includeTriggers(context: BC) {
                with(context) {
                    onBaseInlineQueryCallback ?.let { onBaseInlineQueryCallback ->
                        onBaseInlineQuery(initialFilter) {
                            onBaseInlineQueryCallback(it)
                        }
                    }
                }
            }
        }
        class SwitchInlineQueryCurrentChat<BC : BehaviourContext> (
            val textBuilder: suspend BC.() -> String,
            val initialInlineQueryBuilder: suspend BC.() -> String,
            val initialFilter: SimpleFilter<InlineQuery>? = null,
            val onBaseInlineQueryCallback: CustomBehaviourContextAndTypeReceiver<BC, Unit, BaseInlineQuery>? = null
        ) : Button<BC> {
            override suspend fun buildButton(context: BC): InlineKeyboardButton = SwitchInlineQueryCurrentChatInlineKeyboardButton(
                textBuilder(context),
                initialInlineQueryBuilder(context),
            )

            override suspend fun includeTriggers(context: BC) {
                with(context) {
                    onBaseInlineQueryCallback ?.let { onBaseInlineQueryCallback ->
                        onBaseInlineQuery(initialFilter) {
                            onBaseInlineQueryCallback(it)
                        }
                    }
                }
            }
        }
        class SwitchInlineQuery<BC : BehaviourContext> (
            val textBuilder: suspend BC.() -> String,
            val initialInlineQueryBuilder: suspend BC.() -> String,
            val initialFilter: SimpleFilter<InlineQuery>? = null,
            val onBaseInlineQueryCallback: CustomBehaviourContextAndTypeReceiver<BC, Unit, BaseInlineQuery>? = null
        ) : Button<BC> {
            override suspend fun buildButton(context: BC): InlineKeyboardButton = SwitchInlineQueryInlineKeyboardButton(
                textBuilder(context),
                initialInlineQueryBuilder(context),
            )

            override suspend fun includeTriggers(context: BC) {
                with(context) {
                    onBaseInlineQueryCallback ?.let { onBaseInlineQueryCallback ->
                        onBaseInlineQuery(initialFilter) {
                            onBaseInlineQueryCallback(it)
                        }
                    }
                }
            }
        }
        class URL<BC : BehaviourContext> (
            val textBuilder: suspend BC.() -> String,
            val urlBuilder: suspend BC.() -> String
        ) : Button<BC> {
            override suspend fun buildButton(context: BC): InlineKeyboardButton = URLInlineKeyboardButton(
                textBuilder(context),
                urlBuilder(context),
            )

            override suspend fun includeTriggers(context: BC) {}
        }
        class WebApp<BC : BehaviourContext> (
            val textBuilder: suspend BC.() -> String,
            val webAppInfoBuilder: suspend BC.() -> WebAppInfo
        ) : Button<BC> {
            override suspend fun buildButton(context: BC): InlineKeyboardButton = WebAppInlineKeyboardButton(
                textBuilder(context),
                webAppInfoBuilder(context),
            )

            override suspend fun includeTriggers(context: BC) {}
        }


        suspend fun buildButton(context: BC): InlineKeyboardButton
        suspend fun includeTriggers(context: BC)
    }

    fun buildFreezed(): KeyboardMenu<BC> {
        val freezedMatrix = matrix.map { it.toList() }
        return KeyboardMenu(
            freezedMatrix
        )
    }

    fun buildLazy(): KeyboardMenu<BC> {
        return KeyboardMenu {
            matrix
        }
    }
}