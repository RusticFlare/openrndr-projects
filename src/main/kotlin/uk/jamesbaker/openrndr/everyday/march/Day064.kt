package uk.jamesbaker.openrndr.everyday.march

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

        fun circleList(
            radius: Double,
            pointCount: Int,
            stepSize: Int,
        ): List<Circle> {
            val points = Circle(
                center = drawer.bounds.center,
                radius = radius,
            ).contour.equidistantPositions(pointCount)

            val smallRadius = points.asSequence().windowed(stepSize) { points -> points.first().distanceTo(points.last()) }.first()

            return points.map { Circle(center = it, radius = smallRadius) }
        }

        val circles0 = circleList(
            radius = drawer.bounds.width / 3,
            pointCount = 60,
            stepSize = 3,
        )
        val circles1 = circleList(
            radius = drawer.bounds.width / 6,
            pointCount = 60,
            stepSize = 3,
        )
        val circles2 = circleList(
            radius = drawer.bounds.width / 12,
            pointCount = 60,
            stepSize = 3,
        )

        extend {
            drawer.clear(ColorRGBa.BLACK)
            drawer.stroke = ColorRGBa.WHITE

            design.clear()
            design.draw {
                group {
                    fill = null
                    stroke = ColorRGBa.WHITE
                    circles(circles0)
                    circles(circles1)
                    circles(circles2)
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
