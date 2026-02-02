package uk.jamesbaker.openrndr.everyday.february

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
        val random = Random(seed = 0b11111)

        val steps = 360

        val stepSize = 1.0 / steps

        val minRadius = drawer.bounds.width / 20
        val maxRadius = drawer.bounds.width * 9 / 20
        val midRadius = (maxRadius + minRadius) / 2

        val positions0 = generateSequence(0.0) { it + stepSize }
            .take(steps)
            .map { a ->
                Circle(
                    center = drawer.bounds.center,
                    radius = random.nextDouble(from = minRadius, until = midRadius),
                ).contour.position(a)
            }.toList()
        val positions1 = generateSequence(0.0) { it + stepSize }
            .take(steps)
            .map { a ->
                Circle(
                    center = drawer.bounds.center,
                    radius = random.nextDouble(from = midRadius, until = maxRadius),
                ).contour.position(a)
            }.toList()

        extend {
            drawer.clear(ColorRGBa.BLACK)

            design.clear()
            design.draw {
                group {
                    stroke = null
                    fill = ColorRGBa.WHITE
                    lineLoop(positions1)
                    fill = ColorRGBa.BLACK
                    lineLoop(positions0)
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
