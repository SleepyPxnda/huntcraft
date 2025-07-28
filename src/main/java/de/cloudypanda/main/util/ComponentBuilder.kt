package de.cloudypanda.main.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor

class ComponentBuilder {
    private val component: Component = Component.empty()

    fun append(text: String): ComponentBuilder {
        component.append(Component.text(text))
        return this
    }

    fun append(text: String, color: TextColor): ComponentBuilder {
        component.append(Component.text(text).color(color))
        return this
    }

    fun append(component: Component): ComponentBuilder {
        this.component.append(component)
        return this
    }

    fun appendCondition(condition: Boolean, trueText: ConditionObject, falseText: ConditionObject): ComponentBuilder {
        if (condition) {
            component.append(Component.text(trueText.text, trueText.color ?: TextColor.color(255, 255, 255)))
        } else {
            component.append(Component.text(falseText.text, falseText.color ?: TextColor.color(255, 255, 255)))
        }
        return this
    }

    fun newLine(): ComponentBuilder {
        component.append(Component.newline())
        return this
    }

    fun newLine(count: Int): ComponentBuilder {
        repeat(count) {
            component.append(Component.newline())
        }
        return this
    }

    fun build(): Component {
        return component
    }
}