package dev.inmo.tgbotapi.keyboards.sample

import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.edit.edit
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.keyboards.lib.dsl.buildMenu
import dev.inmo.tgbotapi.keyboards.lib.dsl.data
import dev.inmo.tgbotapi.keyboards.lib.dsl.dataWithNewMenu
import dev.inmo.tgbotapi.types.queries.callback.InlineMessageIdDataCallbackQuery
import dev.inmo.tgbotapi.types.queries.callback.MessageDataCallbackQuery
import dev.inmo.tgbotapi.utils.row

suspend fun main(args: Array<String>) {
    val bot = telegramBot(args.first())

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
