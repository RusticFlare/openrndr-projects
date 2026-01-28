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
import kotlin.math.sqrt

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}

        val rowsAndColumns = 0b11011

        val offset = drawer.bounds.width / rowsAndColumns
        val radius = offset / sqrt(2.0)

        val start = Vector2(0.0, 0.0)
        val right = Vector2(x = offset, y = 0.0)
        val down = Vector2(x = 0.0, y = offset)

        val circles = generateSequence(start) { it + right }
            .take(rowsAndColumns + 1)
            .flatMap { top -> generateSequence(top) { it + down }.take(rowsAndColumns + 1) }
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
