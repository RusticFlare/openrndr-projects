package uk.jamesbaker.openrndr.everyday.march

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.dialogs.saveFileDialog
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.composition.composition
import org.openrndr.extra.composition.draw
import org.openrndr.extra.composition.drawComposition
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.shapes.primitives.toRounded
import org.openrndr.extra.svg.saveToFile
import org.openrndr.shape.Rectangle

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}

        val rectangleCount = 0b1000000
        val widthSteps = drawer.bounds.width / (rectangleCount + 1)
        val radiusSteps = 1.0 / (rectangleCount - 1)

        val roundedRectangles = List(0b1000000) {
            val rectangleWidth = widthSteps * (it + 1)
            val radius = (rectangleWidth / 2) * (1.0 - (radiusSteps * it))
            Rectangle
                .fromCenter(drawer.bounds.center, rectangleWidth)
                .toRounded(radius = radius)
                .shape
        }

        extend {
            drawer.clear(ColorRGBa.BLACK)

            design.clear()
            design.draw {
                group {
                    fill = null
                    stroke = ColorRGBa.WHITE
                    shapes(roundedRectangles)
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
