package uk.jamesbaker.openrndr.everyday.february

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

        val circleCount = 0b101111
        val widthStep = drawer.bounds.width / (circleCount + 1)

        val center = drawer.bounds.center
        val shapes = List(circleCount) {
            val width = widthStep * (it + 1)
            val percentage = it.toDouble() / (circleCount - 1)
            val radius = width * percentage / 2
            Rectangle
                .fromCenter(
                    center = center,
                    width = width,
                ).toRounded(radius = radius)
                .shape
        }

        extend {
            drawer.clear(ColorRGBa.BLACK)
            drawer.stroke = ColorRGBa.WHITE

            design.clear()
            design.draw {
                group {
                    fill = null
                    stroke = ColorRGBa.WHITE
                    shapes(shapes)
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
