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
import org.openrndr.math.Polar
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
        val random = Random(0b100101)

        val circleCount = 50

        val maxRadius = drawer.bounds.dimensions.length / 2

        val radiusStep = maxRadius / circleCount

        val radii = generateSequence(maxRadius) { it - radiusStep }
            .map { it + random.nextDouble(from = -3.0, until = 0.0) }
            .take(circleCount)

        val circles = generateSequence {
            Polar(
                theta = random.nextDouble(from = 0.0, until = 360.0),
                radius = random.nextDouble(until = (radiusStep / 4)),
            )
        }.map { it.cartesian }
            .map { drawer.bounds.center + it }
            .zip(radii, ::Circle)
            .toList()

        extend {
            drawer.clear(ColorRGBa.BLACK)

            design.clear()
            design.draw {
                group {
                    stroke = null
                    circles.forEachIndexed { index, circle ->
                        fill = if (index % 2 == 0) ColorRGBa.BLACK else ColorRGBa.WHITE
                        circle(circle)
                    }
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
