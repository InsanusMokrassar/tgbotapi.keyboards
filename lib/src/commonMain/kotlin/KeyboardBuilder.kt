package dev.inmo.tgbotapi.keyboards.lib

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onDataCallbackQuery
import dev.inmo.tgbotapi.requests.edit.reply_markup.EditChatMessageReplyMarkup
import dev.inmo.tgbotapi.requests.edit.reply_markup.EditInlineMessageReplyMarkup
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.CallbackDataInlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.InlineKeyboardButton
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import dev.inmo.tgbotapi.types.queries.callback.InlineMessageIdDataCallbackQuery
import dev.inmo.tgbotapi.types.queries.callback.MessageDataCallbackQuery
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