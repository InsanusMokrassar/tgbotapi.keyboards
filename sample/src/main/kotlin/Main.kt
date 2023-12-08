package dev.inmo.tgbotapi.keyboards.sample

import dev.inmo.kslog.common.KSLog
import dev.inmo.kslog.common.LogLevel
import dev.inmo.kslog.common.defaultMessageFormatterWithErrorPrint
import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.edit.edit
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.keyboards.lib.KeyboardBuilder
import dev.inmo.tgbotapi.keyboards.lib.dsl.*
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.SwitchInlineQueryChosenChat
import dev.inmo.tgbotapi.types.queries.callback.InlineMessageIdDataCallbackQuery
import dev.inmo.tgbotapi.types.queries.callback.MessageDataCallbackQuery
import dev.inmo.tgbotapi.utils.row

suspend fun main(args: Array<String>) {
    val bot = telegramBot(args.first()) {
        logger = KSLog { level: LogLevel, tag: String?, message: Any, throwable: Throwable? ->
            println(defaultMessageFormatterWithErrorPrint(level, tag, message, throwable))
        }
    }

    val menu = buildMenu<BehaviourContext> globalMenu@{
        row {
            dataWithNewMenu(
                "sample",
                "Sample"
            ) {
                row {
                    data(
                        "back_to_global",
                        "Back",
                        this@globalMenu.buildLazy()
                    )
                    data(
                        "sample2",
                        "Sample 2"
                    ) {
                        println(it)
                    }
                }
                row {
                    url(
                        text = "Open google",
                        url = "google.com",
                    )
                }
                row {
                    switchInlineQuery(
                        "Inline query",
                        "Query"
                    ) {
                        answer(it)
                    }
                    switchInlineQueryChosenChat(
                        "Inline query",
                        SwitchInlineQueryChosenChat(
                            "Sample",
                            allowUsers = true,
                            allowGroups = true,
                            allowChannels = true
                        )
                    ) {
                        answer(it)
                    }
                    switchInlineQueryCurrentChat(
                        "Inline query",
                        "Sample"
                    ) {
                        answer(it)
                    }
                }
            }
        }
    }

    bot.buildBehaviourWithLongPolling {
        menu.setupTriggers(this)

        onCommand("start") {
            reply(
                it,
                "Hi, here your menu:",
                replyMarkup = menu.buildButtons(this)
            )
        }
    }.join()
}
