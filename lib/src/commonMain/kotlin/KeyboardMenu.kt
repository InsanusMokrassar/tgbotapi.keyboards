package dev.inmo.tgbotapi.keyboards.lib

import dev.inmo.micro_utils.common.Either
import dev.inmo.micro_utils.common.either
import dev.inmo.micro_utils.common.onFirst
import dev.inmo.micro_utils.common.onSecond
import dev.inmo.micro_utils.coroutines.launchSafelyWithoutExceptions
import dev.inmo.tgbotapi.bot.exceptions.MessageIsNotModifiedException
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.createSubContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.stop
import dev.inmo.tgbotapi.requests.edit.reply_markup.EditChatMessageReplyMarkup
import dev.inmo.tgbotapi.requests.edit.reply_markup.EditInlineMessageReplyMarkup
import dev.inmo.tgbotapi.types.ChatIdentifier
import dev.inmo.tgbotapi.types.InlineMessageId
import dev.inmo.tgbotapi.types.MessageId
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.buttons.Matrix
import dev.inmo.tgbotapi.types.message.abstracts.Message
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*

class KeyboardMenu<BC : BehaviourContext> internal constructor(
    private val buttonsGetter: suspend BC.() -> Matrix<KeyboardBuilder.Button<BC>>
) {
    internal constructor(
        matrix: Matrix<KeyboardBuilder.Button<BC>>
    ) : this(
        { matrix }
    )

    suspend fun setupTriggers(context: BC) {
        context.buttonsGetter().forEach { row ->
            row.forEach {
                it.includeTriggers(context)
            }
        }
    }

    suspend fun setupWaitersPerforming(context: BC, messageInfo: Either<Pair<ChatIdentifier, MessageId>, InlineMessageId>): Job {
        val subcontext = context.createSubContext()

        return subcontext.launchSafelyWithoutExceptions {
            var newMenu: KeyboardMenu<BC>? = this@KeyboardMenu
            while (newMenu != null) {
                val currentMenu = newMenu

                val subsubcontext = subcontext.createSubContext()
                val flow = with(currentMenu) {
                    context.buttonsGetter().foldRight(emptyFlow<KeyboardMenu<BC>?>()) { row, flow ->
                        row.foldRight(flow) { it, flow ->
                            merge(flow, it.performWaiters(subsubcontext, messageInfo))
                        }
                    }
                }

                val isThereNewMenu = flow.map {
                    newMenu = it
                }.firstOrNull() != null

                if (isThereNewMenu) {
                    subsubcontext.stop()

                    with(subcontext) {
                        messageInfo.onFirst { (chatId, messageId) ->
                            execute(
                                EditChatMessageReplyMarkup(
                                    chatId,
                                    messageId,
                                    replyMarkup = newMenu ?.buildButtons(context)
                                )
                            )
                        }.onSecond { messageId ->
                            execute(
                                EditInlineMessageReplyMarkup(
                                    messageId,
                                    replyMarkup = newMenu ?.buildButtons(context)
                                )
                            )
                        }
                    }
                } else {
                    break
                }
            }
        }
    }

    suspend fun attachToMessage(
        context: BC,
        messageInfo: Either<Pair<ChatIdentifier, MessageId>, InlineMessageId>,
        useWaiters: Boolean = false
    ) = with(context) {
        runCatching {
            messageInfo.onFirst { (chatId, messageId) ->
                execute(
                    EditChatMessageReplyMarkup(
                        chatId,
                        messageId,
                        replyMarkup = buildButtons(context)
                    )
                )
            }.onSecond { messageId ->
                execute(
                    EditInlineMessageReplyMarkup(
                        messageId,
                        replyMarkup = buildButtons(context)
                    )
                )
            }
        }.onFailure {
            if (it is MessageIsNotModifiedException) {
                return@onFailure
            } else {
                throw it
            }
        }

        if (useWaiters) {
            setupWaitersPerforming(context, messageInfo)
        } else {
            null
        }
    }

    suspend fun buildButtons(context: BC): InlineKeyboardMarkup {
        return InlineKeyboardMarkup(
            context.buttonsGetter().map {
                it.map {
                    it.buildButton(context)
                }
            }
        )
    }
}

suspend fun <BC : BehaviourContext> BC.setupMenuTriggers(menu: KeyboardMenu<in BC>): KeyboardMenu<in BC> {
    menu.setupTriggers(this)
    return menu
}

suspend fun <BC : BehaviourContext> KeyboardMenu<BC>.attachToMessage(
    context: BC,
    chatIdentifier: ChatIdentifier,
    messageId: MessageId
) {
    attachToMessage(context, (chatIdentifier to messageId).either())
}

suspend fun <BC : BehaviourContext> KeyboardMenu<BC>.attachToMessage(
    context: BC,
    message: Message
) {
    attachToMessage(context, message.chat.id, message.messageId)
}

suspend fun <BC : BehaviourContext> KeyboardMenu<BC>.attachToMessage(
    context: BC,
    messageId: InlineMessageId
) {
    attachToMessage(context, messageId.either())
}

suspend fun <BC : BehaviourContext> KeyboardMenu<BC>.attachToMessageWithWaiters(
    context: BC,
    chatIdentifier: ChatIdentifier,
    messageId: MessageId
): Job {
    return attachToMessage(context = context, messageInfo = (chatIdentifier to messageId).either(), useWaiters = true)!!
}

suspend fun <BC : BehaviourContext> KeyboardMenu<BC>.attachToMessageWithWaiters(
    context: BC,
    message: Message
): Job = attachToMessageWithWaiters(context, message.chat.id, message.messageId)

suspend fun <BC : BehaviourContext> KeyboardMenu<BC>.attachToMessageWithWaiters(
    context: BC,
    messageId: InlineMessageId
): Job {
    return attachToMessage(context = context, messageInfo = messageId.either(), useWaiters = true)!!
}