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

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}

        val contour = Circle(center = drawer.bounds.center, radius = drawer.bounds.width * 4 / 9).contour

        val pointCount = 48

        val step = (
            (pointCount / 2) - if (pointCount % 2 == 0) {
                if (pointCount % 4 != 0) {
                    2
                } else {
                    1
                }
            } else {
                0
            }
        ).toDouble() / pointCount

        val lineloop = List(pointCount) { contour.position((0.25 + (it * step)) % 1.0) }

        extend {
            drawer.clear(ColorRGBa.BLACK)

            design.clear()
            design.draw {
                group {
                    fill = null
                    stroke = ColorRGBa.WHITE
                    lineLoop(lineloop)
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
