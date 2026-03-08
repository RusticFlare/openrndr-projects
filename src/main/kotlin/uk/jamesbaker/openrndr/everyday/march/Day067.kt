package uk.jamesbaker.openrndr.everyday.march

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.dialogs.saveFileDialog
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.composition.composition
import org.openrndr.extra.composition.draw
import org.openrndr.extra.composition.drawComposition
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.shapes.utilities.splitAt
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
        val random = Random(111001)

        val circleCount = 100

        val maxRadius = drawer.bounds.dimensions.length / 2
        val radiusStep = maxRadius / circleCount

        val contours = List(circleCount) { index ->
            val indexFilter: (Int) -> Boolean = if (random.nextBoolean()) {
                { it != 1 }
            } else {
                { it == 1 }
            }

            Circle(
                center = drawer.bounds.center,
                radius = radiusStep * (index + 1),
            ).contour
                .splitAt(listOf(random.nextDouble(), random.nextDouble()).sorted())
                .filterIndexed { contourIndex, _ -> indexFilter(contourIndex) }
        }.flatten()

        extend {
            drawer.clear(ColorRGBa.BLACK)
            drawer.stroke = ColorRGBa.WHITE

            design.clear()
            design.draw {
                group {
                    fill = null
                    stroke = ColorRGBa.WHITE
                    contours(contours)
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
