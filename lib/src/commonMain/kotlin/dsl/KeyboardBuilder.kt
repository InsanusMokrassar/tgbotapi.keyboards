package dev.inmo.tgbotapi.keyboards.lib.dsl

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.keyboards.lib.KeyboardBuilder
import dev.inmo.tgbotapi.keyboards.lib.KeyboardMenu
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery

inline fun <BC : BehaviourContext> buildMenu(menuBuilder: KeyboardBuilder<BC>.() -> Unit): KeyboardMenu<BC> {
    return KeyboardBuilder<BC>().apply(menuBuilder).buildFreezed()
}

fun <BC : BehaviourContext> KeyboardBuilder.Button.Data.Reaction.Companion.Keyboard(
    menuBuilder: KeyboardBuilder<BC>.(DataCallbackQuery?) -> Unit
): KeyboardBuilder.Button.Data.Reaction.Keyboard<BC> = KeyboardBuilder.Button.Data.Reaction.Keyboard {
    buildMenu {
        menuBuilder(it)
    }
}
