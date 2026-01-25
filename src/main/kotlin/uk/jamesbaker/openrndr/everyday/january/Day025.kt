package uk.jamesbaker.openrndr.everyday.january

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.dialogs.saveFileDialog
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.composition.composition
import org.openrndr.extra.composition.draw
import org.openrndr.extra.composition.drawComposition
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.svg.saveToFile
import org.openrndr.math.Polar
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.shape.Rectangle
import kotlin.math.round

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}

        val radius = drawer.bounds.height / 40

        val nextPoints = listOf(
            Polar(theta = 30.0, radius = radius),
            Polar(theta = 90.0, radius = radius),
            Polar(theta = 150.0, radius = radius),
            Polar(theta = 210.0, radius = radius),
            Polar(theta = 270.0, radius = radius),
            Polar(theta = 330.0, radius = radius),
        ).map { it.cartesian }

        val seen = mutableSetOf(drawer.bounds.center)

        val bounds = Rectangle.fromCenter(
            center = drawer.bounds.center,
            width = drawer.bounds.width + radius + radius,
            height = drawer.bounds.height + radius + radius,
        )

        fun Vector2.points(): Sequence<Vector2> = sequence {
            yield(this@points)
            yieldAll(
                nextPoints
                    .asSequence()
                    .map { it + this@points }
                    .map { it.copy(x = round(it.x * 100) / 100, y = round(it.y * 100) / 100) }
                    .filter { seen.add(it) }
                    .filter { it in bounds }
                    .flatMap { it.points() },
            )
        }

        val circles = drawer.bounds.center
            .points()
//            .take(600)
            .map { Circle(center = it, radius = radius) }
            .toList()

        extend {
            drawer.clear(ColorRGBa.BLACK)

            design.clear()
            design.draw {
                group {
                    fill = null
                    stroke = ColorRGBa.WHITE

                    circles(circles)
                }
            }
            drawer.composition(design)
        }
        // Show a save dialog when pressing the `s` key, then save the design
        // with the chosen file name into the selected folder.
        keyboard.keyDown.listen {
            if (it.name == "s") {
                saveFileDialog(supportedExtensions = listOf("SVG" to listOf("svg"))) { file ->
                    design.saveToFile(file)
                }
            }
        }
    }
}
