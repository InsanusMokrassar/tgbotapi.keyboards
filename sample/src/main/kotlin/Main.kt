package dev.inmo.tgbotapi.keyboards.sample

import dev.inmo.kslog.common.KSLog
import dev.inmo.kslog.common.LogLevel
import dev.inmo.kslog.common.defaultMessageFormatterWithErrorPrint
import dev.inmo.micro_utils.coroutines.launchSafelyWithoutExceptions
import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommandWithArgs
import dev.inmo.tgbotapi.keyboards.lib.KeyboardMenu
import dev.inmo.tgbotapi.keyboards.lib.attachToMessageWithWaiters
import dev.inmo.tgbotapi.keyboards.lib.dsl.*
import dev.inmo.tgbotapi.keyboards.lib.setupMenuTriggers
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.SwitchInlineQueryChosenChat
import dev.inmo.tgbotapi.utils.row

fun buildMenuWithParameters(parameter: String?): KeyboardMenu<BehaviourContext> {
    val actualParameter = parameter ?.let { " $parameter" } ?: ""
    return buildMenu globalMenu@{
        row {
            dataWithNewMenu(
                id = "sample$actualParameter",
                text = "Sample$actualParameter"
            ) {
                row {
                    data(
                        id = "back_to_global$actualParameter",
                        text = "Back$actualParameter",
                        menu = this@globalMenu.buildLazy()
                    )
                    data(
                        id = "sample2$actualParameter",
                        text = "Sample 2$actualParameter"
                    ) {
                        println(it)
                    }
                }
                row {
                    url(
                        text = "Open google$actualParameter",
                        url = "google.com",
                    )
                }
                row {
                    switchInlineQuery(
                        text = "Inline query$actualParameter",
                        switchInlineQuery = "Query"
                    ) {
                        answer(it)
                    }
                    switchInlineQueryChosenChat(
                        text = "Inline query$actualParameter",
                        switchInlineQueryChosenChat = SwitchInlineQueryChosenChat(
                            "Sample",
                            allowUsers = true,
                            allowGroups = true,
                            allowChannels = true
                        )
                    ) {
                        answer(it)
                    }
                    switchInlineQueryCurrentChat(
                        text = "Inline query$actualParameter",
                        switchInlineQueryCurrentChat = "Sample"
                    ) {
                        answer(it)
                    }
                }
            }
        }
    }
}

suspend fun main(args: Array<String>) {
    val bot = telegramBot(args.first()) {
        logger = KSLog { level: LogLevel, tag: String?, message: Any, throwable: Throwable? ->
            println(defaultMessageFormatterWithErrorPrint(level, tag, message, throwable))
        }
    }

    val globalMenu = buildMenuWithParameters(null)

    bot.buildBehaviourWithLongPolling {
        setupMenuTriggers(globalMenu)

        onCommand("start") {
            reply(
                it,
                "Hi, here your menu:",
                replyMarkup = globalMenu.buildButtons(this)
            )
        }

        onCommandWithArgs("start") { message, args ->
            if (args.isEmpty()) return@onCommandWithArgs

            val menu = buildMenuWithParameters(args.joinToString())
            val sentMessage = reply(
                message,
                "Hi, here your menu:",
                replyMarkup = menu.buildButtons(this)
            )
            launchSafelyWithoutExceptions {
                menu.attachToMessageWithWaiters(this@onCommandWithArgs, sentMessage)
            }
        }
    }.join()
}
