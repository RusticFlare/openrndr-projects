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
import org.openrndr.shape.Circle
import kotlin.random.Random

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}
        val random = Random(seed = 0b10001110101)

        val steps = 80

        val stepSize = 1.0 / steps / 2

        val minRadius = drawer.bounds.width / 20
        val maxRadius = drawer.bounds.width * 9 / 20

        val positions = generateSequence(0.0) { it + stepSize }
            .take(steps * 2)
            .windowed(size = 2, step = 2) { (a, b) ->
                val c0 = Circle(
                    center = drawer.bounds.center,
                    radius = random.nextDouble(from = minRadius, until = maxRadius),
                ).contour
                val c1 = Circle(
                    center = drawer.bounds.center,
                    radius = random.nextDouble(from = minRadius, until = maxRadius),
                ).contour
                listOf(c0.position(a), c0.position(b), c1.position(b), c1.position(a))
            }.toList()

        extend {
            drawer.clear(ColorRGBa.BLACK)

            design.clear()
            design.draw {
                group {
                    fill = ColorRGBa.WHITE
                    stroke = null
                    positions.forEach { lineLoop(it) }
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
