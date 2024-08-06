package dev.inmo.tgbotapi.keyboards.lib.dsl

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.requests.abstracts.Request
import dev.inmo.tgbotapi.requests.edit.reply_markup.EditChatMessageReplyMarkup
import dev.inmo.tgbotapi.requests.edit.reply_markup.EditInlineMessageReplyMarkup
import dev.inmo.tgbotapi.requests.edit.text.EditChatMessageText
import dev.inmo.tgbotapi.requests.edit.text.EditInlineMessageText
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.message.textsources.TextSourcesList
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import dev.inmo.tgbotapi.types.queries.callback.InaccessibleMessageDataCallbackQuery
import dev.inmo.tgbotapi.types.queries.callback.InlineMessageIdDataCallbackQuery
import dev.inmo.tgbotapi.types.queries.callback.MessageDataCallbackQuery

typealias DataButtonRequestBuilder<BC> = suspend BC.(DataCallbackQuery, InlineKeyboardMarkup?) -> Request<out Any>?
typealias DataButtonTextRequestBuilder<BC> = suspend BC.(DataCallbackQuery) -> TextSourcesList?

object DataButtonRequestBuilderNamespace {
    fun <BC : BehaviourContext> defaultRequestBuilder(): DataButtonRequestBuilder<BC> = { it, keyboard ->
        when (it) {
            is InlineMessageIdDataCallbackQuery -> EditInlineMessageReplyMarkup(
                it.inlineMessageId,
                keyboard
            )
            is MessageDataCallbackQuery -> EditChatMessageReplyMarkup(
                it.message.chat.id,
                it.message.messageId,
                replyMarkup = keyboard
            )
            is InaccessibleMessageDataCallbackQuery -> null
        }
    }
    fun <BC : BehaviourContext> editTextRequestBuilder(
        textBuilder: DataButtonTextRequestBuilder<BC>
    ): DataButtonRequestBuilder<BC> {
        val fallback = defaultRequestBuilder<BC>()
        return builder@{ it, keyboard ->
            val text = textBuilder(it) ?: return@builder fallback(it, keyboard)
            when (it) {
                is InlineMessageIdDataCallbackQuery -> EditInlineMessageText(
                    it.inlineMessageId,
                    text,
                    replyMarkup = keyboard
                )
                is MessageDataCallbackQuery -> EditChatMessageText(
                    it.message.chat.id,
                    it.message.messageId,
                    text,
                    replyMarkup = keyboard
                )
                is InaccessibleMessageDataCallbackQuery -> null
            }
        }
    }
}