package uk.jamesbaker.openrndr.everyday.march

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.dialogs.saveFileDialog
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.composition.composition
import org.openrndr.extra.composition.draw
import org.openrndr.extra.composition.drawComposition
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.shapes.hobbycurve.hobbyCurve
import org.openrndr.extra.svg.saveToFile
import org.openrndr.math.Vector2
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
        val random = Random(0b111100)

        val circle = Circle(
            center = drawer.bounds.center,
            radius = drawer.bounds.width / 4,
        )

        val points = List(0b111100) { Vector2(x = random.nextInt(width).toDouble(), y = random.nextInt(height).toDouble()) }

        val contour = hobbyCurve(points, closed = true)

        extend {
            drawer.clear(ColorRGBa.BLACK)

            design.clear()
            design.draw {
                group {
                    fill = ColorRGBa.fromHex("ffd1dc")
                    stroke = null
                    circle(circle)
                }
                group {
                    fill = null
                    strokeWeight = 4.0
                    stroke = ColorRGBa.WHITE
                    contour(contour)
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
