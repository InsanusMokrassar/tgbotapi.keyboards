package dev.inmo.tgbotapi.keyboards.lib

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.buttons.Matrix

class KeyboardMenu<BC : BehaviourContext> internal constructor(
    private val buttonsGetter: suspend BC.() -> Matrix<KeyboardBuilder.Button<BC>>
) {
    internal constructor(matrix: Matrix<KeyboardBuilder.Button<BC>>) : this({ matrix })

    suspend fun setupTriggers(context: BC) {
        context.buttonsGetter().forEach { row ->
            row.forEach {
                it.includeTriggers(context)
            }
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