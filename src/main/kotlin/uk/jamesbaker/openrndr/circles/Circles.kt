package uk.jamesbaker.openrndr.circles

import org.openrndr.application
import org.openrndr.color.ColorRGBa.Companion.BLACK
import org.openrndr.color.ColorRGBa.Companion.PINK
import org.openrndr.color.ColorRGBa.Companion.WHITE
import org.openrndr.extensions.Screenshots

fun main() = application {
    configure {
        width = 800
        height = 800
    }
    program {
        extend(Screenshots())

        backgroundColor = PINK

        extend {
            drawer.stroke = null

            drawer.fill = PINK.mix(other = WHITE, factor = 0.55)
            drawer.circle(position = drawer.bounds.center, radius = 350.0)

            drawer.fill = PINK
            drawer.circle(position = drawer.bounds.center, radius = 50.0)

            drawer.fill = PINK.mix(other = BLACK, factor = 0.5)
            drawer.circle(position = drawer.bounds.center, radius = 20.0)
        }
    }
}
