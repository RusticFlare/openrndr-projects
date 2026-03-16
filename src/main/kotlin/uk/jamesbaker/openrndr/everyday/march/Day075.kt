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
import org.openrndr.shape.LineSegment

private fun main() = application {
    configure {
        width = 1000
        height = 1000
    }
    oliveProgram {
        extend(Screenshots())
        val design = drawComposition {}

        val circleCount = 0b111111 * 8
        val stepsPerCircle = 12
        val positionStepSize = 1.0 / stepsPerCircle

        val ascendingTs = List(stepsPerCircle - 1) { (it + 1) * positionStepSize }

        val radiusStepSize = drawer.bounds.dimensions.length / circleCount

        val contours = List(circleCount) { Circle(drawer.bounds.center, radiusStepSize * (it + 1)).contour.splitAt(ascendingTs) }
            .asSequence()
            .zip(generateSequence(0) { (it + 1) % stepsPerCircle }) { c, p -> c[p] }
            .toList()

        val lineSegments = contours.zipWithNext { a, b -> LineSegment(a.position(1.0), b.position(0.0)) }

        extend {
            drawer.clear(ColorRGBa.BLACK)

            design.clear()
            design.draw {
                group {
                    fill = null
                    stroke = ColorRGBa.WHITE
                    contours(contours)
                    lineSegments(lineSegments)
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
