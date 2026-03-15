package uk.jamesbaker.openrndr.everyday.march

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.dialogs.saveFileDialog
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.composition.composition
import org.openrndr.extra.composition.draw
import org.openrndr.extra.composition.drawComposition
import org.openrndr.extra.noise.scatter
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
        val random = Random(0b111101)

        val circle = Circle(
            center = drawer.bounds.center,
            radius = drawer.bounds.width / 3,
        )

        val placementRadius = drawer.bounds.width / 75
        val circles = drawer.bounds
            .scatter(placementRadius = placementRadius, random = random)
            .map { Circle(center = it, radius = random.nextDouble(from = placementRadius * 0.75, until = placementRadius * 3)) }

        extend {
            drawer.clear(ColorRGBa.BLACK)

            design.clear()
            design.draw {
                group {
                    fill = ColorRGBa.WHITE
                    stroke = null
                    circle(circle)
                }
                group {
                    fill = null
                    strokeWeight = 3.0
                    stroke = ColorRGBa.BLACK
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
