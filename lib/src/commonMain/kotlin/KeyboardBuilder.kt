package dev.inmo.tgbotapi.keyboards.lib

import dev.inmo.micro_utils.common.Either
import dev.inmo.micro_utils.common.mapOnFirst
import dev.inmo.micro_utils.common.mapOnSecond
import dev.inmo.micro_utils.coroutines.launchSafelyWithoutExceptions
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.CustomBehaviourContextAndTypeReceiver
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitBaseInlineQuery
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitInlineMessageIdDataCallbackQuery
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitMessageDataCallbackQuery
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitPreCheckoutQueries
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onBaseInlineQuery
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onDataCallbackQuery
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onPreCheckoutQuery
import dev.inmo.tgbotapi.extensions.behaviour_builder.utils.SimpleFilter
import dev.inmo.tgbotapi.extensions.utils.extensions.sameMessage
import dev.inmo.tgbotapi.keyboards.lib.dsl.DataButtonRequestBuilder
import dev.inmo.tgbotapi.keyboards.lib.dsl.DataButtonRequestBuilderNamespace
import dev.inmo.tgbotapi.keyboards.lib.dsl.DataButtonTextRequestBuilder
import dev.inmo.tgbotapi.requests.abstracts.Request
import dev.inmo.tgbotapi.requests.edit.reply_markup.EditChatMessageReplyMarkup
import dev.inmo.tgbotapi.requests.edit.reply_markup.EditInlineMessageReplyMarkup
import dev.inmo.tgbotapi.requests.edit.text.EditChatMessageText
import dev.inmo.tgbotapi.requests.edit.text.EditInlineMessageText
import dev.inmo.tgbotapi.types.*
import dev.inmo.tgbotapi.types.InlineQueries.query.BaseInlineQuery
import dev.inmo.tgbotapi.types.InlineQueries.query.InlineQuery
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.*
import dev.inmo.tgbotapi.types.payments.PreCheckoutQuery
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import dev.inmo.tgbotapi.types.queries.callback.InaccessibleMessageDataCallbackQuery
import dev.inmo.tgbotapi.types.queries.callback.InlineMessageIdDataCallbackQuery
import dev.inmo.tgbotapi.types.queries.callback.MessageDataCallbackQuery
import dev.inmo.tgbotapi.types.webapps.WebAppInfo
import dev.inmo.tgbotapi.utils.MatrixBuilder
import kotlinx.coroutines.flow.*

class KeyboardBuilder<BC : BehaviourContext> : MatrixBuilder<KeyboardBuilder.Button<BC>>() {
    sealed interface Button<BC : BehaviourContext> {
        class Data<BC : BehaviourContext> (
            val id: String,
            val reaction: Reaction<BC>,
            val callbacksRegex: Regex = Regex(id),
            val requestBuilder: DataButtonRequestBuilder<BC> = DataButtonRequestBuilderNamespace.defaultRequestBuilder(),
            val textBuilder: suspend BC.() -> String
        ) : Button<BC> {
            /**
             * Reaction onto [DataCallbackQuery] event
             */
            sealed interface Reaction<BC : BehaviourContext> {
                /**
                 * When [DataCallbackQuery] event happen, will build new [KeyboardMenu] as well as react onto event
                 *
                 * @param keyboardMenuBuilder Will receive null [DataCallbackQuery] on setup of triggers stage
                 */
                class Keyboard<BC : BehaviourContext>(
                    val keyboardMenuBuilder: suspend BC.(DataCallbackQuery?) -> KeyboardMenu<BC>?
                ) : Reaction<BC>

                /**
                 * Simple action as reaction onto [DataCallbackQuery] event
                 */
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
                        is Reaction.Action -> onDataCallbackQuery(callbacksRegex) {
                            reaction.callback(this, it)
                        }
                        is Reaction.Keyboard -> {
                            reaction.keyboardMenuBuilder.invoke(this, null) ?.setupTriggers(this)
                            onDataCallbackQuery(callbacksRegex) {
                                val keyboard = reaction.keyboardMenuBuilder(this, it) ?.buildButtons(this)
                                execute(
                                    requestBuilder(it, keyboard) ?: return@onDataCallbackQuery
                                )
                            }
                        }
                    }
                }
            }

            override suspend fun performWaiters(
                context: BC,
                messageInfo: Either<Pair<ChatIdentifier, MessageId>, InlineMessageId>
            ): Flow<KeyboardMenu<BC>?> {
                val filteredFlow = with(context) {
                    messageInfo.mapOnFirst { (chatId, messageId) ->
                        waitMessageDataCallbackQuery().filter {
                            it.message.sameMessage(chatId, messageId) && callbacksRegex.matches(it.data)
                        }
                    } ?: messageInfo.mapOnSecond { messageId ->
                        waitInlineMessageIdDataCallbackQuery().filter {
                            it.inlineMessageId == messageId && callbacksRegex.matches(it.data)
                        }
                    } ?: emptyFlow()
                }
                val resultFlow: Flow<KeyboardMenu<BC>?> = when (reaction) {
                    is Reaction.Action -> with(context) {
                        launchSafelyWithoutExceptions {
                            filteredFlow.collect {
                                reaction.callback(context, it)
                            }
                        }
                        emptyFlow()
                    }
                    is Reaction.Keyboard -> filteredFlow.map {
                        reaction.keyboardMenuBuilder(context, it)
                    }
                }
                return resultFlow
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
            val initialFilter: SimpleFilter<PreCheckoutQuery>? = null,
            val onPreCheckoutQueryCallback: CustomBehaviourContextAndTypeReceiver<BC, Unit, PreCheckoutQuery>? = null
        ) : Button<BC> {
            override suspend fun buildButton(context: BC): InlineKeyboardButton = PayInlineKeyboardButton(
                textBuilder(context),
            )

            override suspend fun includeTriggers(context: BC) {
                with(context) {
                    onPreCheckoutQueryCallback ?.let { onPreCheckout ->
                        onPreCheckoutQuery(initialFilter) {
                            onPreCheckout(it)
                        }
                    }
                }
            }

            override suspend fun performWaiters(
                context: BC,
                messageInfo: Either<Pair<ChatIdentifier, MessageId>, InlineMessageId>
            ): Flow<KeyboardMenu<BC>?> {
                with (context) {
                    onPreCheckoutQueryCallback ?.let { onPreCheckoutQueryCallback ->
                        launchSafelyWithoutExceptions {
                            waitPreCheckoutQueries().collect {
                                onPreCheckoutQueryCallback(context, it)
                            }
                        }
                    }
                }
                return super.performWaiters(context, messageInfo)
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

            override suspend fun performWaiters(
                context: BC,
                messageInfo: Either<Pair<ChatIdentifier, MessageId>, InlineMessageId>
            ): Flow<KeyboardMenu<BC>?> {
                with (context) {
                    onBaseInlineQueryCallback ?.let { onBaseInlineQueryCallback ->
                        launchSafelyWithoutExceptions {
                            waitBaseInlineQuery().collect {
                                onBaseInlineQueryCallback(context, it)
                            }
                        }
                    }
                }
                return super.performWaiters(context, messageInfo)
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


            override suspend fun performWaiters(
                context: BC,
                messageInfo: Either<Pair<ChatIdentifier, MessageId>, InlineMessageId>
            ): Flow<KeyboardMenu<BC>?> {
                with (context) {
                    onBaseInlineQueryCallback ?.let { onBaseInlineQueryCallback ->
                        launchSafelyWithoutExceptions {
                            waitBaseInlineQuery().collect {
                                onBaseInlineQueryCallback(context, it)
                            }
                        }
                    }
                }
                return super.performWaiters(context, messageInfo)
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


            override suspend fun performWaiters(
                context: BC,
                messageInfo: Either<Pair<ChatIdentifier, MessageId>, InlineMessageId>
            ): Flow<KeyboardMenu<BC>?> {
                with (context) {
                    onBaseInlineQueryCallback ?.let { onBaseInlineQueryCallback ->
                        launchSafelyWithoutExceptions {
                            waitBaseInlineQuery().collect {
                                onBaseInlineQueryCallback(context, it)
                            }
                        }
                    }
                }
                return super.performWaiters(context, messageInfo)
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
        suspend fun performWaiters(context: BC, messageInfo: Either<Pair<ChatIdentifier, MessageId>, InlineMessageId>): Flow<KeyboardMenu<BC>?> = emptyFlow()
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