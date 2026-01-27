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
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}

        val maxRadius = drawer.bounds.width / 3

        val centers = listOf(
            Vector2(x = maxRadius, y = maxRadius),
            drawer.bounds.center,
            Vector2(x = maxRadius, y = maxRadius * 2),
            Vector2(x = maxRadius * 2, y = maxRadius),
            Vector2(x = maxRadius * 2, y = maxRadius * 2),
        )

        val radius = centers.first().minus(drawer.bounds.center).length

        val circles = centers.map { Circle(center = it, radius = radius) }

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
